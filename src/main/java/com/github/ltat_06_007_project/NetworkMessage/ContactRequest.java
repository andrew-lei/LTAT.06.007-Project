package com.github.ltat_06_007_project.NetworkMessage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ContactRequest {

    private final String requesterIp;
    private final String requesteeIp;

    @JsonCreator
    public ContactRequest(@JsonProperty("requesterIp")String requesterIp, @JsonProperty("requesteeIp")String requesteeIp) {
        this.requesterIp = requesterIp;
        this.requesteeIp = requesteeIp;
    }

    public String getRequesterIp() {
        return requesterIp;
    }

    public String getRequesteeIp() {
        return requesteeIp;
    }
}
