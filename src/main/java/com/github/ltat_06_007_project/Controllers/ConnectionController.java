package com.github.ltat_06_007_project.Controllers;

import com.github.ltat_06_007_project.Models.ContactModel;
import com.github.ltat_06_007_project.Objects.ConnectionObject;
import com.github.ltat_06_007_project.Objects.ContactObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionController {

    private List<ConnectionObject> activeConnections = new ArrayList<>();
    private List<ContactObject> pendingConnections = new CopyOnWriteArrayList<>();
    private ContactModel contactModel;

    @Autowired
    public ConnectionController(ContactModel contactModel) {
        this.contactModel = contactModel;

        Runnable connectionHandler = this::createConnections;
        new Thread(connectionHandler).start();

    }

    private void createConnections() {
        contactModel.getAll()
                .stream()
                .filter(c -> c.getAllowed())
                .forEach(c -> pendingConnections.add(c));

        while (!Thread.interrupted()) {

            //for (ContactObject contact : pendingConnections.)

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                break;
            }
        }

    }
    /*
    private void establishConnection(ContactObject contact) {
        var connection = new ConnectionObject();
        try {
            //TODO create a persistent tcp connection with the target on the last known ip
            throw new IOException();
            activeConnections.add(connection);
            //TODO log connection created
        } catch (IOException e) {
            //TODO log failed connection
            pendingConnections.add(contact);
        }
    }*/
}
