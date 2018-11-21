package com.github.ltat_06_007_project.Server.Objects;

import java.net.Socket;

public class ClientObject {
    private final String socketId;
    private final Socket socket;

    public ClientObject(String socketId, Socket socket) {
        this.socketId = socketId;
        this.socket = socket;
    }

    public String getSocketId() {
        return socketId;
    }
    public Socket getSocket() {
        return socket;
    }
}
