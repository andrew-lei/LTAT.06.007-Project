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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;

import java.io.IOException;

@SpringBootApplication
public class MainApplication extends Application {

    private ConfigurableApplicationContext springContext;
    private Scene scene;

    public static PrivateKey privateKey;
    public static PublicKey publicKey;
    public static String userIdCode = "39430121338";
    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(final String[] args) {
        try {
            privateKey = Cryptography.readKey("cert.key");
            publicKey = Cryptography.readPub("cert.pub");
        } catch (IOException e) {
            try {
                Cryptography.genKeyPair("cert");
                privateKey = Cryptography.readKey("cert.key");
                publicKey = Cryptography.readPub("cert.pub");
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
        }
        try {
            //Cryptography.signKey("cert.key","C:/Windows/SysWOW64/onepin-opensc-pkcs11.dll",new char[]{'2','3','2','5'}, "signed.pub");
            Container container = Cryptography.containerFromB64Bytes(Files.readAllBytes(Paths.get("signed.pub")));
            System.out.println(new String(Cryptography.getData(container)));

            Signature signature = container.getSignatures().get(0);
            System.out.println(signature.getSigningCertificate().getSubjectName(X509Cert.SubjectName.SURNAME));

        } catch (IOException e) {
            e.printStackTrace();
        }
        launch(MainApplication.class, args);
    }

    @Override
    public void init() throws IOException{
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
    public void start(Stage stage)  {
        stage.setTitle("EID IM");
        stage.setScene(scene);
        stage.show();
    }
}
