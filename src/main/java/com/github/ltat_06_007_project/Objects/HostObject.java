package com.github.ltat_06_007_project.Objects;

import java.net.Socket;

public class HostObject {
    private String name;

    private String address;


    public HostObject(String name, String address, Socket socket) {
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
