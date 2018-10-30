package com.github.ltat_06_007_project.Objects;

public class MessageObject {
    private final String content;
    private final String contactId;

    public MessageObject(String content) {
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
