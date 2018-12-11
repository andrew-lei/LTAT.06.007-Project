package com.github.ltat_06_007_project.Server.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

//type 3
public class ContactRequest {
    private String encryptedContactRequest;

    @JsonCreator
    public ContactRequest(@JsonProperty("encryptedContactRequest")String encryptedContactRequest) {
        this.encryptedContactRequest = encryptedContactRequest;
    }
}
