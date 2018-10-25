package com.github.ltat_06_007_project.Objects;

public class ConnectionObject {

    enum ConnectionState {
        DISABLED, DISCONNECTED, UNTRUSTED, UNSECURE, TRUSTED
    }
    private ContactObject contact;
    private ConnectionState state = ConnectionState.DISABLED;
    private String ip;
    private String name;
    private String address;


    public ConnectionObject(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }
    public String getAddress() {
        return address;
    }

    public void setName(String name) {
        this.name = name;
    }
}
