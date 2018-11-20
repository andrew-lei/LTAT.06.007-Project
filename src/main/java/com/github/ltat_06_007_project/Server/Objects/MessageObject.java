package com.github.ltat_06_007_project.Server.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageObject {
    private final int messageType;
    private final String clientId;
    private final byte[] message;

    @JsonCreator
    public MessageObject(@JsonProperty("messageType")int messageType, @JsonProperty("clientId")String clientId, @JsonProperty("message")byte[] message) {
        this.messageType = messageType;
        this.clientId = clientId;
        this.message = message;
    }

    public int getMessageType() {
        return messageType;
    }
    public String getClientId() {
        return clientId;
    }
    public byte[] getMessage(){ return message; }
}
