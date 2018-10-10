package com.github.ltat_06_007_project.Controllers;

import com.github.ltat_06_007_project.Message;
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


    public Message addMessage(String content) {
        return chatModel.insertMessage(new Message(content));
    }

    public List<Message> getAllMessages() {
        return chatModel.getMessages();
    }
}
