package com.github.ltat_06_007_project;

import java.net.ServerSocket;

public class LocalServer {

    public int port;
    public ServerSocket serverSocket;

    LocalServer(int port) throws Exception {
        this.port = port;
        this.serverSocket = new ServerSocket(port);
    }

    // Blocking function that waits for connections.
    public LocalConnection accept() throws Exception {
        return new LocalConnection(this.serverSocket.accept(), this.port);
    }
}
