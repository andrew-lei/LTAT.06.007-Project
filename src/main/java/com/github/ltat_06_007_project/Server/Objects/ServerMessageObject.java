package com.github.ltat_06_007_project.Server.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ServerMessageObject {
    private final int messageType;
    private final String socketId;
    private final byte[] message;

    @JsonCreator
    public ServerMessageObject(@JsonProperty("messageType")int messageType, @JsonProperty("socketId")String socketId, @JsonProperty("message")byte[] message) {
        this.messageType = messageType;
        this.socketId = socketId;
        this.message = message;
    }

    public int getMessageType() {
        return messageType;
    }
    public String getSocketId() {
        return socketId;
    }
    public byte[] getMessage(){ return message; }
}
