package com.github.ltat_06_007_project.Views.ChatView;

import com.github.ltat_06_007_project.Controllers.ContactController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ContactComponentController {

    @FXML
    private Label contactNameLabel;

    private ChatViewController chatViewController;
    private ContactController contactController;

    public void setChatViewController(ChatViewController controller, ContactController contactController){
        this.chatViewController = controller;
        this.contactController = contactController;
    }

    public void setContactNameLabel(String contactNameLabel) {
        this.contactNameLabel.setText(contactNameLabel);
    }
    public void onMouseClicked(){
        chatViewController.setContact(contactNameLabel.getText());
    }
    public void removeContact(){
        contactController.removeContact(contactNameLabel.getText());
        chatViewController.loadContacts();
    }
}