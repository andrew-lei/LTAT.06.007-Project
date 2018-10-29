package com.github.ltat_06_007_project.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.ltat_06_007_project.MainApplication;
import com.github.ltat_06_007_project.Models.ContactModel;
import com.github.ltat_06_007_project.NetworkMessage.ContactRequest;
import com.github.ltat_06_007_project.NetworkMessage.NetworkMessageWrapper;
import com.github.ltat_06_007_project.Objects.ContactObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

@Component
public class ConnectionController {

    private final CopyOnWriteArrayList<String> disconnectedContactIdList = new CopyOnWriteArrayList<>();
    private final ConcurrentHashMap<String, Thread> idToConnection = new ConcurrentHashMap<>();
    private final Executor executor = Executors.newCachedThreadPool();
    private final ContactModel contactModel;
    private final NetworkNodeController networkNodeController;

    void allowContact(String id) {
        synchronized (disconnectedContactIdList) {
            if (disconnectedContactIdList.stream().noneMatch(i -> i.equals(id))) {
                createConnection(id);
            }
        }
    }

    public void removeContact(String id) {
        synchronized (disconnectedContactIdList) {
            disconnectedContactIdList.removeIf(i -> i.equals(id));
            synchronized (idToConnection) {
                idToConnection.get(id).interrupt();
                idToConnection.remove(id);
            }
        }
    }

    void confirmContactConnection(Thread connectionThread, String id) {
        synchronized (disconnectedContactIdList) {
            disconnectedContactIdList.removeIf(i -> i.equals(id));
            synchronized (idToConnection) {
                idToConnection.get(id).interrupt();
                idToConnection.put(id, connectionThread);
            }
        }
    }

    void confirmContactConnection(String id) {
        synchronized (disconnectedContactIdList) {
            disconnectedContactIdList.removeIf(i -> i.equals(id));
        }
    }

    void addThread(Thread thread) {
        executor.execute(thread);
    }


    @Autowired
    public ConnectionController(ContactModel contactModel, NetworkNodeController networkNodeController) {
        this.contactModel = contactModel;
        this.networkNodeController = networkNodeController;

        contactModel.getAll()
                .stream()
                .filter(ContactObject::getAllowed)
                .map(ContactObject::getId)
                .forEach(this::createConnection);

        new Thread(this::listenForConnections).start();
        new Thread(this::sendContactRequests).start();
    }

    private void createConnection(String contactId) {
        synchronized (disconnectedContactIdList) {
            disconnectedContactIdList.add(contactId);
            synchronized (idToConnection) {
                Thread connectionThread = new Thread(new OutgoingTcpConnection(contactId, contactModel, this)::establishConnection);
                executor.execute(connectionThread);
                idToConnection.put(contactId,connectionThread);
            }
        }
    }

    private void listenForConnections() {
        while (!Thread.interrupted()) {
            try (var serverSocket = new ServerSocket(42069)) {
                Socket socket = serverSocket.accept();
                new IncomingTcpConnection(socket, this, contactModel);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendContactRequests() {
        while (!Thread.interrupted()) {
            synchronized (disconnectedContactIdList) {
                try {
                    for (String contactId : disconnectedContactIdList) {
                        ContactRequest contactRequest = new ContactRequest(MainApplication.userIdCode, contactId);
                        String contactRequestSerialized = MainApplication.mapper.writeValueAsString(contactRequest);
                        networkNodeController.addToOutbox(new NetworkMessageWrapper(1, contactRequestSerialized));
                    }
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    break;
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
