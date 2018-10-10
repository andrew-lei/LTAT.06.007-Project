package com.github.ltat_06_007_project.Models;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class MessageModel {
    // Hashmap is not the best.
    private final HashMap<String,String> hostNameToMessage = new HashMap<>();

    public void updateMessages(String host, String message) {
        hostNameToMessage.put(host, message);
    }
}
