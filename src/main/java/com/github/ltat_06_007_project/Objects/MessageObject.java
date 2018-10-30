package com.github.ltat_06_007_project.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageObject {
    private final String content;
    private final String contactId;

    @JsonCreator
    public MessageObject(@JsonProperty("content")String content) {
        this.content = content;
        contactId = "";
    }

    public String getContent() {
        return content;
    }

    public String getContactId() {
        return contactId;
    }
}
