package com.github.ltat_06_007_project.Models;

import com.github.ltat_06_007_project.MessageDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChatModel {


    private final MessageDatabase messageDatabase;

    @Autowired
    public ChatModel(MessageDatabase messageDatabase) {
        this.messageDatabase = messageDatabase;
    }

    public String insertMessage(String message) {
        return messageDatabase.insertMessage(message);
    }

    public List<String> getMessages() {
        return messageDatabase.getAllMessages();
    }
}
