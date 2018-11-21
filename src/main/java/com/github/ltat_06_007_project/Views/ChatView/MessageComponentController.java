package com.github.ltat_06_007_project.Views.ChatView;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class MessageComponentController {

    @FXML
    private VBox messageContainer;
    @FXML
    private Region rightRegion;
    @FXML
    private Region leftRegion;
    @FXML
    private TextFlow messageTextFlow;

    @FXML
    private Label messageSentTime;

    void SetMessageText(String text){
        Text messageText = new Text(text);
        this.messageTextFlow.getChildren().add(messageText);
    }
    void SetMessageSent(String messageSent){
        this.messageSentTime.setText(messageSent);
    }
    void setMessageAlignment(boolean leftAlignment){
        if(leftAlignment){
            leftRegion.setMaxWidth(0.0);
        }else{
            rightRegion.setMaxWidth(0.0);
        }
    }
}