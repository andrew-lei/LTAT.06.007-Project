package com.github.ltat_06_007_project;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

import java.io.IOException;

@SpringBootApplication
public class MainApplication extends Application {

    private ConfigurableApplicationContext springContext;
    private Scene scene;

    public static PrivateKey privateKey;
    public static PublicKey publicKey;
    public static String userIdCode = "39430121337";
    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(final String[] args) {
        try {
            privateKey = Cryptography.readKey("cer,key");
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
        launch(MainApplication.class, args);
    }

    @Override
    public void init() throws IOException{
        springContext = SpringApplication.run(MainApplication.class);
        var fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("LoginView.fxml"));
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
