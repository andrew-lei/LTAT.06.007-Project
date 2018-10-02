package com.github.ltat_06_007_project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ChatModel {

    @Autowired
    public ChatView chatView;

    @Autowired
    public MessageDatabase messageDatabase;


    @PostConstruct
    public void loadMessagesFromDatabase(){
        messageDatabase.getAllMessages()
                .forEach( message -> chatView.getOutput().appendText("\n" + message));
    }
    public void addMessage(String message) {
        messageDatabase.insertMessage(message);
        chatView.getOutput().appendText("\n" + message);
    }
}
