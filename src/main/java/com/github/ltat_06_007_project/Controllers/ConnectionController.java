package com.github.ltat_06_007_project.Controllers;

import com.github.ltat_06_007_project.Models.ContactModel;
import com.github.ltat_06_007_project.Objects.ContactObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionController {

    private LinkedBlockingQueue<ContactObject> pendingConnections = new LinkedBlockingQueue<>();
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

        Executor executor = Executors.newCachedThreadPool();
        while (!Thread.interrupted()) {
            try {
                executor.execute(new TcpConnection(pendingConnections.take(), this)::establishConnection);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void put(ContactObject contact) throws InterruptedException {
        pendingConnections.put(contact);
    }


}
