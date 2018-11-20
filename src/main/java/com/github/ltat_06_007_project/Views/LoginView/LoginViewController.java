package com.github.ltat_06_007_project.Views.LoginView;

import com.github.ltat_06_007_project.Controllers.ChatController;
import com.github.ltat_06_007_project.MainApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class LoginViewController {
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField keyPath;
    @FXML
    private Button loginButton;

    @FXML
    private Button createAccountButton;

    private ConfigurableApplicationContext springContext;
    private Scene scene;
    private Stage stage;

    @Autowired
    public LoginViewController(ConfigurableApplicationContext context) {
        this.springContext = context;
    }
    @FXML
    void createAccount(ActionEvent event) {
        //Todo:create user keys etc.

    }

    @FXML
    void loginAction(ActionEvent event)throws IOException {
    //ToDo: Validate user
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("ChatView.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        Parent root = fxmlLoader.load();
        this.scene = new Scene(root);
        this.stage = new Stage();
        stage.setScene(scene);
        stage.show();

    }

}
