package com.github.ltat_06_007_project.Views.ChatView;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ContactComponentController {

    @FXML
    private Label contactNameLabel;

    public void setContactNameLabel(String contactNameLabel) {
        this.contactNameLabel.setText(contactNameLabel);
    }
}