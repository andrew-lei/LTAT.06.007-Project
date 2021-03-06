package com.github.ltat_06_007_project.Views.ChatView;

import com.github.ltat_06_007_project.Controllers.ChatController;
import com.github.ltat_06_007_project.Controllers.ConnectionController;
import com.github.ltat_06_007_project.Controllers.ContactController;
import com.github.ltat_06_007_project.MainApplication;
import com.github.ltat_06_007_project.Objects.MessageObject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.sql.Array;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingDeque;
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
    private ConnectionController connectionController;
    @FXML
    private Button addContactButton;


    @FXML
    private TextField newContactField;

    private String currentContact;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UserLabel.setText(MainApplication.userIdCode);
        loadContacts();
        initMessageBox();
    }

    private void initMessageBox() {
        messageBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                if (event.isShiftDown()) {
                    messageBox.appendText(System.getProperty("line.separator"));
                } else {
                    if(!messageBox.getText().isEmpty()){
                        sendMessage();
                    }
                }
            }
        });
    }

    @Autowired
    public ChatViewController(ChatController chatController, ContactController contactController, ConnectionController connectionController) {
        this.chatController = chatController;
        this.contactController = contactController;
        this.connectionController = connectionController;

    }
    public void loadContacts(){
        contactBox.getItems().clear();
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
        participants.setText(contactController.getName(contactId));
        chatBox.getItems().clear();
        setOnlineStatus(contactId);
        loadOutput();
    }

    private void sendMessage(){
        MessageObject message = chatController.addMessage(messageBox.getText(),currentContact);
        messageBox.clear();
        try {
            createMessageBox(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        chatBox.scrollTo(chatBox.getItems().size()-1);
    }
    private void loadOutput(){
        chatController.getAllMessages(currentContact, MainApplication.userIdCode).forEach(m -> {
            try {
                createMessageBox(m);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        chatBox.scrollTo(chatBox.getItems().size()-1);
    }
    private void setOnlineStatus(String id){
        this.onlineStatus.setText(this.connectionController.isOnline(id) ? "Online" : "Offline");
    }
    @FXML
    private void createMessageBox(MessageObject message) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("MessageComponent.fxml"));
        Object messageComponent = fxmlLoader.load();
        MessageComponentController messageController = (MessageComponentController)fxmlLoader.getController();
        messageController.SetMessageSent(message.getMessageSentTime().toString());
        messageController.SetMessageText(message.getContent());
        messageController.setMessageAlignment(message.getReceiverId().equals(MainApplication.userIdCode));
        chatBox.getItems().add(messageComponent);

    }
    private void createContactBox(String contactId){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("ContactComponent.fxml"));
            Object contactComponent = fxmlLoader.load();
            ContactComponentController contactController = (ContactComponentController)fxmlLoader.getController();
            contactController.setChatViewController(this, this.contactController);
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
            if(currentContact.equalsIgnoreCase(message.getSenderId())){
                createMessageBox(message);
            }
        }
        catch(Exception e) {
        }
    }

    private LinkedBlockingQueue<List<String>> queue =  new LinkedBlockingQueue<>();

    public void connectionChanged(String id, boolean isOnline){
        if (id == null) {
            return;
        }
        List<String> temp = new ArrayList<String>();
        temp.add(id);
        if (isOnline) {
            temp.add("");
        }
        queue.add(temp);
        Platform.runLater(this::test);
    }

    void test() {
        try {
            List<String> temp = queue.take();
            boolean isOnline = temp.size() == 2;
            if(currentContact != null && currentContact.toLowerCase().equals(temp.get(0).toLowerCase())){
                onlineStatus.setText(isOnline ? "Online" : "Offline");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

}