package com.github.ltat_06_007_project;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import java.io.IOException;
import java.security.*;

@SpringBootApplication
public class MainApplication extends Application {

    private static ConfigurableApplicationContext springContext;
    private Scene scene;
    private static Stage stage;

    public static PrivateKey privateKey;
    public static byte[] signedPublicKey;
    public static String userIdCode;
    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(final String[] args){
        Cryptography.init();
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
        springContext.close();
    }

    @Override
    public void start(Stage newStage) {
        stage = newStage;
        stage.setOnCloseRequest( event -> stop() );
        stage.setTitle("EID IM");
        stage.setScene(scene);
        stage.show();
    }

    public static void login(String password, String keyPath) throws Exception {
        privateKey = Cryptography.readPrivateKey(keyPath,password);
        signedPublicKey = Cryptography.readPublicKeyContainer(keyPath);
        userIdCode = Cryptography.getSignerInfo(Cryptography.containerFromBytes(signedPublicKey)).get(0);
    }

    public static void createUser(String password, String keyPath, char[] pin) throws Exception {
        Cryptography.generateKeyPair(keyPath, password, pin);
        login(password, keyPath);
    }

    public void openChatWindow()throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("ChatView.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        Parent root = fxmlLoader.load();
        Scene chatScene = new Scene(root);
        stage.setScene(chatScene);
    }
    public void displayAlert(String header, String content){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
