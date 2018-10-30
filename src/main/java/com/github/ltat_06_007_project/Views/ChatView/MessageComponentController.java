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
        var messageText = new Text(text);
        this.messageTextFlow.getChildren().add(messageText);
    }
    void SetMessageSent(String messageSent){
        this.messageSentTime.setText(messageSent);
    }
    void setMessageAlignment(boolean leftAlignment){
        messageTextFlow.setTextAlignment(leftAlignment? TextAlignment.LEFT : TextAlignment.RIGHT);
        VBox.setVgrow(leftAlignment ? rightRegion : leftRegion, Priority.ALWAYS);
    }
}