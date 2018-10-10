package com.github.ltat_06_007_project.Models;

import java.util.HashMap;

public class MessageModel {
    // Hashmap is not the best.
    private final HashMap<String,String> hostNameToMessage = new HashMap<>();

    public void updateMessages(String host, String message) {
        hostNameToMessage.put(host, message);
    }
}
