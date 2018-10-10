package com.github.ltat_06_007_project.Objects;

import java.net.Socket;

public class HostObject {
    private String name;

    private String address;

    private Socket socket;

    public HostObject(String name, String address, Socket socket) {
        this.name = name;
        this.address = address;
        this.socket = socket;
    }

    public String getName() {
        return name;
    }
    public String getAddress() {
        return address;
    }
    public Socket getSocket() { return socket; }

    public void setName(String name) {
        this.name = name;
    }
    public void setSocket(Socket socket) { this.socket = socket; }
}
