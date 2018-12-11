package com.github.ltat_06_007_project.Server.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ServerMessageObject {
    private final int messageType;
    private final String content;
    private String socketId;

    @JsonCreator
    public ServerMessageObject(@JsonProperty("messageType")int messageType,
                               @JsonProperty("content")String content) {
        this.messageType = messageType;
        this.content = content;
    }

    public void setSocketId(String socketId) { this.socketId = socketId; }

    public int getMessageType() {
        return messageType;
    }
    public String getContent() { return content; }
    public String getSocketId() { return socketId; }
}
