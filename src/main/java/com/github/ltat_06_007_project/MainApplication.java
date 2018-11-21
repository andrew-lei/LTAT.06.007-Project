package com.github.ltat_06_007_project;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.digidoc4j.Container;
import org.digidoc4j.Signature;
import org.digidoc4j.X509Cert;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;

import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;

@SpringBootApplication
public class MainApplication extends Application {

    private ConfigurableApplicationContext springContext;
    private Scene scene;

    public static PrivateKey privateKey;
    public static byte[] signedPublicKey;
    public static String userIdCode;
    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(final String[] args) throws IOException {
        //login("1234567890123456",".");
        launch(MainApplication.class, args);
    }

    @Override
    public void init() throws IOException {
        springContext = SpringApplication.run(MainApplication.class);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("LoginView.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        Parent root = fxmlLoader.load();
        scene = new Scene(root);
        scene.setRoot(root);
    }

    @Override
    public void stop() {
        springContext.stop();
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("EID IM");
        stage.setScene(scene);
        stage.show();
    }

    public static void login(String password, String keyPath) throws IOException {
        password = addPasswordPadding(password);

        byte[] encryptedPrivateKey = Files.readAllBytes(Paths.get(keyPath + "/user.key"));
        SecretKey key = new SecretKeySpec(password.getBytes(), "AES");
        try {
            PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(Cryptography.decryptText(key, encryptedPrivateKey));
            try {
                KeyFactory kf = KeyFactory.getInstance("RSA");
                privateKey = kf.generatePrivate(ks);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }

        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {

        }
        signedPublicKey = Files.readAllBytes(Paths.get(keyPath + "/user.pub"));
        userIdCode = Cryptography.containerFromB64Bytes(signedPublicKey).getSignatures().get(0).getSigningCertificate().getSubjectName(X509Cert.SubjectName.SERIALNUMBER);

    }

    public static void createUser(String password, String keyPath, char[] pin) throws IOException {
        password = addPasswordPadding(password);
        Cryptography.genKeyPair(keyPath, password, pin);
        login(password, keyPath);
    }
    private static String addPasswordPadding(String password){
        while (password.length() < 16) {
            password = password + password;
        }
        return password.substring(0,16);
    }
}
