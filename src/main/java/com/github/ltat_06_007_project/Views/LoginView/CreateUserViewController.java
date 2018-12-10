package com.github.ltat_06_007_project.Views.LoginView;

import com.github.ltat_06_007_project.MainApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.InvalidKeyException;

@Component
public class CreateUserViewController {

    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField pinField;
    @FXML
    private Button cancelButton;
    @FXML
    private Button createButton;

    private ConfigurableApplicationContext springContext;
    private MainApplication mainApp;

    @Autowired
    public CreateUserViewController(ConfigurableApplicationContext context, MainApplication main) {
        this.springContext = context;
        this.mainApp = main;
    }
    @FXML
    void createAccount(ActionEvent event) throws Exception {
        //Todo:create user keys etc.
        try{
            char[] pin = pinField.getText().toCharArray();
            String password = passwordField.getText();
            if(password == null || password.isEmpty()){
                mainApp.displayAlert("Empty password", "Please fill password and try again");
                return;
            }
            if(pin.length != 4){
                mainApp.displayAlert("Incorrect PIN1", "PIN1 must have 4 digits");
                return;
            }
            MainApplication.createUser(password, ".", pin);
            mainApp.openChatWindow();
            Stage stage = (Stage) createButton.getScene().getWindow();
            stage.close();
        }catch (InvalidKeyException e){
            mainApp.displayAlert("Incorrect PIN1", "Please try again");
        }
    }
    @FXML
    void cancel(ActionEvent event){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

}
