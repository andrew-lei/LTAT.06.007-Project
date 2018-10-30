package com.github.ltat_06_007_project.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.ltat_06_007_project.MainApplication;
import com.github.ltat_06_007_project.Models.ChatModel;
import com.github.ltat_06_007_project.Models.ContactModel;
import com.github.ltat_06_007_project.NetworkMessage.ContactRequest;
import com.github.ltat_06_007_project.NetworkMessage.NetworkMessageWrapper;
import com.github.ltat_06_007_project.Objects.ContactObject;
import com.github.ltat_06_007_project.Views.ChatView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component
public class ConnectionController {

    private final CopyOnWriteArraySet<String> allowedContacts;
    private final ConcurrentHashMap<String, TcpConnection> idToConnection = new ConcurrentHashMap<>();
    private final Executor executor = Executors.newCachedThreadPool();
    private final ContactModel contactModel;
    private final NetworkNodeController networkNodeController;
    private final ChatView chatView;
    private final ChatModel chatModel;

    @Autowired
    public ConnectionController(ContactModel contactModel, NetworkNodeController networkNodeController, ChatView chatView, ChatModel chatModel) {
        this.contactModel = contactModel;
        this.networkNodeController = networkNodeController;
        this.chatView = chatView;
        this.chatModel = chatModel;

        this.allowedContacts = contactModel.getAll()
                .stream()
                .filter(ContactObject::getAllowed)
                .map(ContactObject::getId)
                .collect(Collectors.toCollection(CopyOnWriteArraySet::new));

        new Thread(this::listenForConnections).start();
        new Thread(this::sendContactRequests).start();
    }

    private void listenForConnections() {
        while (!Thread.interrupted()) {
            try (var serverSocket = new ServerSocket(42069)) {
                Socket socket = serverSocket.accept();
                new TcpConnection(socket, this, contactModel, chatView, chatModel, executor);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendContactRequests() {
        while (!Thread.interrupted()) {
            synchronized (allowedContacts) {
                synchronized (idToConnection) {
                    try {
                        Set<String> disconnectedContacts = allowedContacts.stream()
                                .filter(c -> !idToConnection.containsKey(c))
                                .collect(Collectors.toSet());

                        for (String contactId : disconnectedContacts) {
                            ContactRequest contactRequest = new ContactRequest(MainApplication.userIdCode, contactId);
                            String contactRequestSerialized = MainApplication.mapper.writeValueAsString(contactRequest);
                            networkNodeController.addToOutbox(new NetworkMessageWrapper(1, contactRequestSerialized));

                            if(!contactModel.getById(contactId).getIp().equals("")) {
                                new TcpConnection(contactId, this,  contactModel,  chatView,  chatModel, executor).start();
                            }
                        }
                    } catch (InterruptedException e) {
                        break;
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    boolean notifyStart(TcpConnection tcpConnection) {
        synchronized (idToConnection) {
            if (!idToConnection.containsKey(tcpConnection.getContactId())) {
                idToConnection.put(tcpConnection.getContactId(), tcpConnection);
                return true;
            } else if (tcpConnection.isIncoming == idToConnection.get(tcpConnection.getContactId()).isIncoming) {
                return false;
            } else {
                boolean hasPriority;
                if (tcpConnection.isIncoming) {
                    hasPriority = Long.parseLong(MainApplication.userIdCode) - Long.parseLong(tcpConnection.getContactId()) > 0;
                } else {
                    hasPriority = Long.parseLong(MainApplication.userIdCode) - Long.parseLong(tcpConnection.getContactId()) < 0;
                }

                if (hasPriority) {
                    idToConnection.get(tcpConnection.getContactId()).close();
                    idToConnection.put(tcpConnection.getContactId(), tcpConnection);
                    return true;
                } else {
                    return false;
                }

            }

        }

    }

    void notifyClose(TcpConnection tcpConnection) {
        synchronized (idToConnection) {
            if (idToConnection.get(tcpConnection.getContactId()) == tcpConnection) {
                idToConnection.remove(tcpConnection.getContactId());
            }
        }
    }
}
