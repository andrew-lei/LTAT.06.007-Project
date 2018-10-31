package com.github.ltat_06_007_project.Views.ChatView;

import com.github.ltat_06_007_project.Controllers.ChatController;
import com.github.ltat_06_007_project.Controllers.ContactController;
import com.github.ltat_06_007_project.MainApplication;
import com.github.ltat_06_007_project.Objects.ContactObject;
import com.github.ltat_06_007_project.Objects.MessageObject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class ChatViewController implements Initializable {

    @FXML
    private ListView contactBox;
    @FXML
    private Label participants;
    @FXML
    private Label onlineStatus;
    @FXML
    private ListView chatBox;

    @FXML
    private TextArea messageBox;

    @FXML
    public Button sendButton;

    @FXML
    private Label UserLabel;

    private ChatController chatController;
    private ContactController contactController;
    @FXML
    private Button addContactButton;


    @FXML
    private TextField newContactField;

    private String currentContact;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UserLabel.setText(MainApplication.userIdCode);
        loadContacts();
    }
    @Autowired
    public ChatViewController(ChatController chatController, ContactController contactController) {
        this.chatController = chatController;
        this.contactController = contactController;
    }
    private void loadContacts(){
        contactController.getAllContacts().forEach(c -> createContactBox(c.getIdCode()));
    }
    public void sendButtonAction(){
        sendMessage();
    }
    @FXML
    void addContactButtonClicked(ActionEvent event) {
        addContact();
    }

    private void addContact() {
        contactController.addContact(newContactField.getText());
        createContactBox(newContactField.getText());
        newContactField.clear();
    }


    public void setContact(String contactId) {
        currentContact = contactId;
        participants.setText(contactId);
        chatBox.getItems().clear();
        loadOutput();
    }

    private void sendMessage(){
        var message = chatController.addMessage(messageBox.getText(),currentContact);
        messageBox.clear();
        try {
            createMessageBox(message.getContent(), new Date().toString(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        chatBox.scrollTo(chatBox.getItems().size()-1);
    }
    private void loadOutput(){
        chatController.getAllMessages(currentContact).forEach(m -> {
            try {
                createMessageBox(m.getContent(), new Date().toString(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        chatBox.scrollTo(chatBox.getItems().size()-1);
    }
    @FXML
    private void createMessageBox(String message, String messageSent, boolean sentToCurrentUser) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("MessageComponent.fxml"));
        var messageComponent = fxmlLoader.load();
        var messageController = (MessageComponentController)fxmlLoader.getController();
        messageController.SetMessageSent(messageSent);
        messageController.SetMessageText(message);
        messageController.setMessageAlignment(sentToCurrentUser);
        chatBox.getItems().add(messageComponent);

    }
    private void createContactBox(String contactId){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("ContactComponent.fxml"));
            var contactComponent = fxmlLoader.load();
            var contactController = (ContactComponentController)fxmlLoader.getController();
            contactController.setChatViewController(this);
            contactController.setContactNameLabel(contactId);
            contactBox.getItems().add(contactComponent);
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private final LinkedBlockingQueue<MessageObject> messageQueue = new LinkedBlockingQueue<>();

    public void insertMessage(MessageObject message){
        messageQueue.add(message);
        Platform.runLater(this::printMessageFromNetwork);

    }
    public void printMessageFromNetwork(){
        try {
            MessageObject message = messageQueue.take();
            createMessageBox(message.getContent(), new Date().toString(), false);
        }
        catch(Exception e) {
        }
    }

}