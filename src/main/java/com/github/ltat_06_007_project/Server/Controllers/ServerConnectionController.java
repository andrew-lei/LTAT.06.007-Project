package com.github.ltat_06_007_project.Server.Controllers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.ltat_06_007_project.MainApplication;
import com.github.ltat_06_007_project.Server.Objects.MessageRelay;
import com.github.ltat_06_007_project.Server.Objects.ServerMessageObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class ServerConnectionController{
    private static final Logger log = LoggerFactory.getLogger(ServerConnectionController.class);

    private final ConcurrentHashMap<String, SocketController> idToConnection = new ConcurrentHashMap<>();
    private final LinkedBlockingQueue<ServerMessageObject> inbox = new LinkedBlockingQueue<>();
    private final Executor executor = Executors.newCachedThreadPool();

    boolean socketIdExsists(String socketId){
        return idToConnection.containsKey(socketId);
    }

    void removeSocketId(String socketId){
        idToConnection.remove(socketId);
    }


    @Autowired
    public ServerConnectionController() {
        new Thread(this::listenForConnections).start();
        new Thread(this::handleInbox).start();
    }

    private void listenForConnections() {
        while (!Thread.interrupted()) {
            try (ServerSocket serverSocket = new ServerSocket(42069)) {
                Socket socket = serverSocket.accept();
                new SocketController(socket, this, executor).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // TODO
    void sendMessage(ServerMessageObject messageObject) {
        synchronized (idToConnection) {
            SocketController connection = idToConnection.get(messageObject.getSocketId());
            if (connection != null) {
                connection.sendMessage(messageObject);
            }
        }
    }


    public boolean isOnline(String id) {
        synchronized (idToConnection) {
            SocketController connection = idToConnection.get(id);
            if(connection == null) return false;
            return connection.isOnline();
        }
    }


    public void addToInbox(ServerMessageObject messageObject) { inbox.add(messageObject); }
    // TODO
    private void handleInbox() {
        while (!Thread.interrupted()) {
            try {
                ServerMessageObject message = inbox.take();
                String fromSocket = message.getSocketId();

                if (message.getMessageType() == 0) {

                    log.info("PublicKeyShare");
                } else if (message.getMessageType() == 1) {

                    log.info("PublicKeyAdvertisment");
                } else if (message.getMessageType() == 2) {

                    log.info("ContactRequest");
                } else if (message.getMessageType() == 3) {

                    log.info("PublicKeyRequest");

                } else if (message.getMessageType() == 4) {
                    MessageRelay messageRelay = MainApplication.mapper
                            .readValue(message.getContent(), MessageRelay.class);
                    String targetSocket = messageRelay.socketId;
                    messageRelay.socketId = fromSocket;
                    message.setSocketId(targetSocket);
                    sendMessage(message);
                    log.info("Message From {} sent to {}", fromSocket, targetSocket);
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
