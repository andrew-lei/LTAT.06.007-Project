package com.github.ltat_06_007_project.Models;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MessageModel {
    private final HashMap<String, Queue<byte[]>> hostNameToMessage = new HashMap<>();

    public void updateMessages(String host, byte[] message) {
        Queue<byte[]> messageList;
        if (hostNameToMessage.containsKey(host)){
            messageList = hostNameToMessage.get(host);
        }
        else {
            messageList = new LinkedList<>();
            hostNameToMessage.put(host, messageList);
        }
        messageList.add(message);
        System.out.println(hostNameToMessage.toString());
    }

    public boolean hasMessages() {
        return !hostNameToMessage.keySet().isEmpty();
    }

    public Set<String> hasMessagesSet() {
        return hostNameToMessage.keySet();
    }

    public Queue<byte[]> getMessages(String host){
        if (hostNameToMessage.containsKey(host)){
            Queue<byte[]> messageList = hostNameToMessage.get(host);
            hostNameToMessage.remove(host);
            return messageList;
        }
        else return null;
    }

    public byte[] getFirstMessage(String host){
        if (hostNameToMessage.containsKey(host)){
            Queue<byte[]> messageList = hostNameToMessage.get(host);
            byte[] message = messageList.remove();
            if (messageList.isEmpty()) hostNameToMessage.remove(host);
            return message;
        }
        else return null;
    }
}
