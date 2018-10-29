package com.github.ltat_06_007_project.NetworkMessage;

public class ContactRequest {

    private final String requesterIp;
    private final String reqiesteeIp;

    public ContactRequest(String requesterIp, String reqiesteeIp) {
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
