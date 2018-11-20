package com.github.ltat_06_007_project.Server.Objects;

import java.net.Socket;

public class ClientObject {
    private final String id;
    private final String publicKey;
    private final Socket socket;

    public ClientObject(String id, String publicKey, Socket socket) {
        this.id = id;
        this.publicKey = publicKey;
        this.socket = socket;
    }
}
