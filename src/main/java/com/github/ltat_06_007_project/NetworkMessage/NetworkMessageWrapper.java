package com.github.ltat_06_007_project.NetworkMessage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NetworkMessageWrapper {

    private final int messageType;
    private final String serializedMessage;

    @JsonCreator
    public NetworkMessageWrapper(@JsonProperty("messageType") int messageType, @JsonProperty("serializedMessage") String serializedMessage) {
        this.messageType = messageType;
        this.serializedMessage = serializedMessage;
    }

    public String getSerializedMessage() {
        return serializedMessage;
    }

    public int getMessageType() {
        return messageType;
    }
}
