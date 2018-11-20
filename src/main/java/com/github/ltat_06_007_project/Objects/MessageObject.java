package com.github.ltat_06_007_project.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class MessageObject {

    private final String content;
    private final String contactId;
    private final Date messageSentTime;

    @JsonCreator
    public MessageObject(@JsonProperty("content")String content, @JsonProperty("contactId")String contactId, @JsonProperty("messageSentTime")Date messageSentTime) {
        this.content = content;
        this.contactId = contactId;
        this.messageSentTime = messageSentTime;
    }

    public String getContent() {
        return content;
    }

    public String getContactId() {
        return contactId;
    }
    public Date getMessageSentTime(){ return messageSentTime; }
}
