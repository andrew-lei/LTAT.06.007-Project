package com.github.ltat_06_007_project.NetworkMessage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ContactRequest {

    private final String requesterIp;
    private final String reqiesteeIp;

    @JsonCreator
    public ContactRequest(@JsonProperty("requesterIp")String requesterIp, @JsonProperty("reqiesteeIp")String reqiesteeIp) {
        this.requesterIp = requesterIp;
        this.reqiesteeIp = reqiesteeIp;
    }

    public String getRequesterIp() {
        return requesterIp;
    }

    public String getReqiesteeIp() {
        return reqiesteeIp;
    }
}
