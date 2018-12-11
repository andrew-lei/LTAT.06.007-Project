package com.github.ltat_06_007_project.Server.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

//type 4 - encrypted message for the contact with the correct id
public class MessageRelay {
    private String socketId;
    private final String encryptedMessage;

    @JsonCreator
    public MessageRelay(@JsonProperty("socketId")String socketId,
                        @JsonProperty("encryptedMessage")String encryptedMessage) {
        this.socketId = socketId;
        this.encryptedMessage = encryptedMessage;
    }

    public void setSocketId(String socketId) {
        this.socketId = socketId;
    }

    public String getSocketId() {
        return socketId;
    }
}
