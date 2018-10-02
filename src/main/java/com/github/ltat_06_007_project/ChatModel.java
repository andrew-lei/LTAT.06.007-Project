package com.github.ltat_06_007_project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatModel {

    @Autowired
    public ChatView chatView;

    public void addMessage(String message) {
        chatView.getOutput().appendText("\n" + message);
    }
}
