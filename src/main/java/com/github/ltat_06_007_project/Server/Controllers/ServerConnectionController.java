package com.github.ltat_06_007_project.Server.Controllers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.ltat_06_007_project.Configuration;
import com.github.ltat_06_007_project.Cryptography;
import com.github.ltat_06_007_project.MainApplication;
import com.github.ltat_06_007_project.Models.ContactModel;
import com.github.ltat_06_007_project.Server.Objects.ContactRequest;
import com.github.ltat_06_007_project.Server.Objects.MessageRelay;
import com.github.ltat_06_007_project.Server.Objects.PublicKeyShare;
import com.github.ltat_06_007_project.Server.Objects.ServerMessageObject;
import org.digidoc4j.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class ServerConnectionController{
    private static final Logger log = LoggerFactory.getLogger(ServerConnectionController.class);

    private final ConcurrentHashMap<String, SocketController> idToController = new ConcurrentHashMap<>();
    private final LinkedBlockingQueue<ServerMessageObject> inbox = new LinkedBlockingQueue<>();
    private final Executor executor = Executors.newCachedThreadPool();
    private final ContactModel contactModel;

    boolean socketIdExsists(String socketId){
        return idToController.containsKey(socketId);
    }

    void removeSocketId(String socketId){
        idToController.remove(socketId);
    }


    @Autowired
    public ServerConnectionController(ContactModel contactModel) {
        this.contactModel = contactModel;

        new Thread(this::listenForConnections).start();
        new Thread(this::handleInbox).start();
    }

    private void listenForConnections() {
        while (!Thread.interrupted()) {
            try (ServerSocket serverSocket = new ServerSocket(Configuration.getServerPort())) {
                Socket socket = serverSocket.accept();
                new SocketController(socket, this, executor).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    void sendMessage(ServerMessageObject messageObject) {
        synchronized (idToController) {
            SocketController controller = idToController.get(messageObject.getSocketId());
            if (controller != null) {
                controller.sendMessage(messageObject);
            }
        }
    }

    void pseudoBroadcast(ServerMessageObject messageObject) {
        synchronized (idToController) {
            for (SocketController controller : idToController.values()){
                if (controller != null) {
                    controller.sendMessage(messageObject);
                }
            }
        }
    }

    public boolean isOnline(String id) {
        synchronized (idToController) {
            SocketController Controller = idToController.get(id);
            if(Controller == null) return false;
            return Controller.isOnline();
        }
    }


    public void addToInbox(ServerMessageObject messageObject) { inbox.add(messageObject); }
    
    private void handleInbox() {
        while (!Thread.interrupted()) {
            try {
                ServerMessageObject message = inbox.take();
                String fromSocket = message.getSocketId();

                if (message.getMessageType() == 0) {
                    PublicKeyShare publicKeyShare =
                            MainApplication.mapper.readValue(message.getContent(), PublicKeyShare.class);
                    Container container = Cryptography.containerFromBytes(publicKeyShare.getPublicKey());
                    String id = Cryptography.getSignerInfo(container).get(0);
                    contactModel.updatePublicKey(id, publicKeyShare.getPublicKey());
                    log.info("Updated public key from {}", id);

                } else if (message.getMessageType() == 1) {

                    log.info("PublicKeyAdvertisment");

                } else if (message.getMessageType() == 2) {

                    log.info("PublicKeyRequest");

                } else if (message.getMessageType() == 3) {
                    pseudoBroadcast(message);
                    log.info("Contact request from {}", fromSocket);

                } else if (message.getMessageType() == 4) {
                    MessageRelay messageRelay =
                            MainApplication.mapper.readValue(message.getContent(), MessageRelay.class);
                    String targetSocket = messageRelay.getSocketId();
                    messageRelay.setSocketId(fromSocket);
                    message.setSocketId(targetSocket);
                    sendMessage(message);
                    log.info("Message from {} sent to {}", fromSocket, targetSocket);
                }
            } catch (InterruptedException e) {
                break;
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
