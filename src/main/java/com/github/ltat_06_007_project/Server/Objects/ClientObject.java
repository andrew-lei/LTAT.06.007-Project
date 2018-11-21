package com.github.ltat_06_007_project.Server.Objects;

import java.net.Socket;

public class ClientObject {
    private final String clientId;
    private final String publicKey;
    private final Socket socket;

    public ClientObject(String clientId, String publicKey, Socket socket) {
        this.clientId = clientId;
        this.publicKey = publicKey;
        this.socket = socket;
    }

    public String getClientId() {
        return clientId;
    }
    public String getPublicKey() {
        return publicKey;
    }
    public Socket getSocket() {
        return socket;
    }
}
