package com.github.ltat_06_007_project.Views;

import com.github.ltat_06_007_project.Controllers.ChatController;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ChatView extends VBox {

    private TextArea output;
    private TextField input;
    private Button sendButton;
    private ChatController chatController;
    @Autowired
    public ChatView(ChatController chatController) {
        this.chatController = chatController;
        initComponents();
        Platform.runLater(this::createLayout);
    }

    private void createLayout() {
        getChildren().addAll(output, input, sendButton);
    }
    private void initComponents(){
        createOutputArea();
        createInputField();
        createSendMessageButton();
    }
    private void createOutputArea(){
        output = new TextArea();
        output.setDisable(true);
        loadOutput();
    }
    private void createSendMessageButton(){
        sendButton = new Button();
        sendButton.setText("Send");
        sendButton.setOnAction(e -> sendMessage());
    }
    private void createInputField(){
        input = new TextField();
        input.setOnAction(e -> sendMessage());

    }
    private void sendMessage(){
        var message = chatController.addMessage(input.getText());
        input.clear();
        output.appendText(System.lineSeparator() + message.getContent());
    }
    private void loadOutput(){
        chatController.getAllMessages().forEach(m -> output.appendText(System.lineSeparator() + m.getContent()));
    }
}
