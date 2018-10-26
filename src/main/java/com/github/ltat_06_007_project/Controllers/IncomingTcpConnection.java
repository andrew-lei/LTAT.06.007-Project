package com.github.ltat_06_007_project.Controllers;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

class IncomingTcpConnection {

    private final Socket socket;
    private final ConnectionController connectionController;
    private final Thread thread;
    private String id;


    IncomingTcpConnection(Socket socket, ConnectionController connectionController) {
        this.socket = socket;
        this.connectionController = connectionController;
        thread = new Thread(this::connect);
        connectionController.addThread(thread);
    }

    private void connect() {

        try {
            var inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            //TODO handle the connection
            //Get their auth token
            id = "";//from their auth token
            //send your auth token
            //start state sync state
            connectionController.confirmContactConnection(thread,id);
            //finish state sync
            //get new private key
            //keep connection open

        } catch (IOException e) {
            if (id != null) {
                connectionController.allowContact(id);
            }
        }
    }

}
