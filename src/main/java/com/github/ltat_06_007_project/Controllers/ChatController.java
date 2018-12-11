package com.github.ltat_06_007_project.Controllers;

import com.github.ltat_06_007_project.MainApplication;
import com.github.ltat_06_007_project.Objects.MessageObject;
import com.github.ltat_06_007_project.Models.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    
    public MessageObject addMessage(String content, String receiverId) {
        MessageObject message = chatModel.insertMessage(new MessageObject(content, MainApplication.userIdCode, receiverId, new Date()));
        connectionController.sendMessage(message);
        return message;
    }

    public List<MessageObject> getAllMessages() {
        return chatModel.getMessages();
    }

    public List<MessageObject> getAllMessages(String contactId, String userId) {
        return chatModel.getMessages().stream().filter(m ->
                m.getSenderId().equals(contactId) && m.getReceiverId().equals(userId) ||
                        m.getSenderId().equals(userId) && m.getReceiverId().equals(contactId)).collect(Collectors.toList());
    }
}
