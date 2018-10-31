package com.github.ltat_06_007_project.Controllers;

import com.github.ltat_06_007_project.Objects.MessageObject;
import com.github.ltat_06_007_project.Models.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChatController {

    private final ChatModel chatModel;
    private final ConnectionController connectionController;

    @Autowired
    public ChatController(ChatModel chatModel, ConnectionController connectionController) {
        this.chatModel = chatModel;
        this.connectionController = connectionController;
    }
    
    public MessageObject addMessage(String content, String contactId) {
        MessageObject message = chatModel.insertMessage(new MessageObject(content,contactId));
        connectionController.sendMessage(message);
        return message;
    }

    public List<MessageObject> getAllMessages() {
        return chatModel.getMessages();
    }

    public List<MessageObject> getAllMessages(String contactId) {
        return chatModel.getMessages().stream().filter(m -> m.getContactId().equals(contactId)).collect(Collectors.toList());
    }
}
