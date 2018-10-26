package com.github.ltat_06_007_project.Controllers;

import com.github.ltat_06_007_project.Models.ContactModel;

import java.io.IOException;
import java.net.Socket;

class OutgoingTcpConnection {

    private final String contactId;
    private final ContactModel contactModel;


    OutgoingTcpConnection(String contactId, ContactModel contactModel) {
        this.contactId = contactId;
        this.contactModel = contactModel;
    }

    void establishConnection() {
        while (!Thread.interrupted()) {
            try (var socket = new Socket(contactModel.getById(contactId).getIp(), 42069)) {
                //TODO create a persistent tcp connection with the target on the last known ip
                //attempt to create connection
                //send out
                throw new IOException();
                //activeConnections.add(connection);
                //TODO log connection created
            } catch (IOException e) {
                //TODO log failed connection
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    break;
                }
            }

        }
    }
}
