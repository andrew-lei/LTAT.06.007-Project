package com.github.ltat_06_007_project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ChatController {

    @Autowired
    public ChatModel model;
    @Autowired
    public ChatView view;

    @PostConstruct
    public void addActionHandler() {
        view.getInput().setOnAction(e -> enterMessage());
    }

    private void enterMessage() {
        model.addMessage(view.getInput().getCharacters().toString());
        view.getInput().clear();
    }
}
