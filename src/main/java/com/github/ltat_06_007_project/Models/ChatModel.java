package com.github.ltat_06_007_project.Models;

import com.github.ltat_06_007_project.Objects.MessageObject;
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

    public MessageObject insertMessage(MessageObject content) {
        return messageDatabase.insertMessage(content);
    }

    public List<MessageObject> getMessages() {
        return messageDatabase.getAllMessages();
    }
}
