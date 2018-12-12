package com.github.ltat_06_007_project.Views.LoginView;

import com.github.ltat_06_007_project.MainApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import java.io.IOException;
@Component
public class LoginViewController {
    @FXML
    private PasswordField passwordField;


    private ConfigurableApplicationContext springContext;
    private MainApplication mainApp;

    @Autowired
    public LoginViewController(ConfigurableApplicationContext context, MainApplication main) {
        this.springContext = context;
        this.mainApp = main;
    }
    @FXML
    void createAccount(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("CreateUserView.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        Parent root = fxmlLoader.load();
        Scene createAccountScene = new Scene(root);
        Stage createAccountStage = new Stage();
        createAccountStage.setScene(createAccountScene);
        createAccountStage.initModality(Modality.APPLICATION_MODAL);
        createAccountStage.showAndWait();
    }

    @FXML
    void loginAction(ActionEvent event)throws Exception {
        try{
            String password = passwordField.getText();
            if(password == null || password.isEmpty()){
                mainApp.displayAlert("Empty password", "Please fill password and try again");
                return;
            }
            MainApplication.login(password, ".");
            mainApp.openChatWindow();
        }catch (BadPaddingException e){
           mainApp.displayAlert("Incorrect password", "Please try again");
        }catch (IllegalArgumentException e){
            mainApp.displayAlert("Certificate corrupted", "Please create new account");
        }


    }


}
