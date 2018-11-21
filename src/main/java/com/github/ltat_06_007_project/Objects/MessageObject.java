package com.github.ltat_06_007_project.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class MessageObject {

    private final String content;
    private final String senderId;
    private final String receiverId;
    private final Date messageSentTime;

    @JsonCreator
    public MessageObject(@JsonProperty("content")String content, @JsonProperty("senderId")String senderId,  @JsonProperty("receiverId")String receiverId, @JsonProperty("messageSentTime")Date messageSentTime) {
        this.content = content;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageSentTime = messageSentTime;
    }

    public String getContent() {
        return content;
    }

    public String getSenderId() {
        return senderId;
    }
    public Date getMessageSentTime(){ return messageSentTime; }
    public String getReceiverId() {
        return receiverId;
    }
}
