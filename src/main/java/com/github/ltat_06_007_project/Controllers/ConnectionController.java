package com.github.ltat_06_007_project.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.ltat_06_007_project.MainApplication;
import com.github.ltat_06_007_project.Models.ChatModel;
import com.github.ltat_06_007_project.Models.ContactModel;
import com.github.ltat_06_007_project.NetworkMessage.ContactRequest;
import com.github.ltat_06_007_project.NetworkMessage.NetworkMessageWrapper;
import com.github.ltat_06_007_project.Objects.ContactObject;
import com.github.ltat_06_007_project.Objects.MessageObject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component
public class ConnectionController implements ApplicationContextAware {

    private final CopyOnWriteArraySet<String> allowedContacts;
    private final ConcurrentHashMap<String, TcpConnection> idToConnection = new ConcurrentHashMap<>();
    private final Executor executor = Executors.newCachedThreadPool();
    private final ContactModel contactModel;
    private final NetworkNodeController networkNodeController;
    private final ChatModel chatModel;

    private ApplicationContext applicationContext;


    @Autowired
    public ConnectionController(ContactModel contactModel, NetworkNodeController networkNodeController, ChatModel chatModel) {
        this.contactModel = contactModel;
        this.networkNodeController = networkNodeController;
        this.chatModel = chatModel;

        this.allowedContacts = contactModel.getAll()
                .stream()
                .filter(ContactObject::isAllowed)
                .filter(c -> !c.getIdCode().equals(MainApplication.userIdCode))
                .map(ContactObject::getIdCode)
                .collect(Collectors.toCollection(CopyOnWriteArraySet::new));

        new Thread(this::listenForConnections).start();
        new Thread(this::sendContactRequests).start();
    }

    private void listenForConnections() {
        while (!Thread.interrupted()) {
            try (ServerSocket serverSocket = new ServerSocket(42069)) {
                Socket socket = serverSocket.accept();
                new TcpConnection(socket, this, contactModel, applicationContext, chatModel, executor).start();
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


                            Optional<ContactObject> optionalContactObject = contactModel.getById(contactId);
                            if (!optionalContactObject.isPresent()) {
                                continue;
                            }
                            if(!optionalContactObject.get().getIpAddress().equals("")) {
                                new TcpConnection(contactId, this,  contactModel, applicationContext,  chatModel, executor).start();
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
            TcpConnection oldConnection = idToConnection.get(tcpConnection.getContactId());
            if (oldConnection!= null && oldConnection == tcpConnection) {
                idToConnection.remove(tcpConnection.getContactId());
            }
        }
    }

    void sendMessage(MessageObject messageObject) {
        synchronized (idToConnection) {
            TcpConnection connection = idToConnection.get(messageObject.getReceiverId());
            if (connection != null) {
                connection.sendMessage(messageObject);
            }
        }
    }


    public boolean isOnline(String id) {
        synchronized (idToConnection) {
            TcpConnection connection = idToConnection.get(id);
            if(connection == null) return false;
            return connection.isOnline();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void addConnection(String contactId) {
        allowedContacts.add(contactId);
    }
}
