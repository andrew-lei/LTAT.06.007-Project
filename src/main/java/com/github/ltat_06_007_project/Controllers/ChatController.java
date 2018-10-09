package com.github.ltat_06_007_project.Controllers;

import com.github.ltat_06_007_project.Models.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChatController {

    private final ChatModel chatModel;

    @Autowired
    public ChatController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }


    public String addMessage(String message) {
        return chatModel.insertMessage(message);
    }

    public List<String> getAllMessages() {
        return chatModel.getMessages();
    }
}
