package com.github.ltat_06_007_project.Controllers;

import com.github.ltat_06_007_project.Objects.ContactObject;

import java.io.IOException;

public class TcpConnection {

    private final ContactObject contact;
    private final ConnectionController connectionController;


    public TcpConnection(ContactObject contact, ConnectionController connectionController) {
        this.contact = contact;
        this.connectionController = connectionController;
    }

    public void establishConnection() {
        try {
            //var connection = new ConnectionObject();
            //TODO create a persistent tcp connection with the target on the last known ip
            throw new IOException();
            //activeConnections.add(connection);
            //TODO log connection created
        } catch (IOException e) {
            //TODO log failed connection
            try {
                Thread.sleep(5000);
                connectionController.put(contact);
            } catch (InterruptedException ex) {
            }
        }
    }
}
