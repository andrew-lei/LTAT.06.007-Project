package com.github.ltat_06_007_project.Controllers;

import com.github.ltat_06_007_project.Objects.MessageObject;
import com.github.ltat_06_007_project.Models.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChatController {

    private final ChatModel chatModel;
    private final ConnectionController connectionController;

    @Autowired
    public ChatController(ChatModel chatModel, ConnectionController connectionController) {
        this.chatModel = chatModel;
        this.connectionController = connectionController;
    }
    
    public MessageObject addMessage(String content) {
        MessageObject message = chatModel.insertMessage(new MessageObject(content));
        connectionController.sendMessage(message);
        return message;
    }

    public List<MessageObject> getAllMessages() {
        return chatModel.getMessages();
    }
}
