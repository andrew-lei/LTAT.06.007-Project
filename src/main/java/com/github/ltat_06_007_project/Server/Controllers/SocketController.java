package com.github.ltat_06_007_project.Server.Controllers;

import com.github.ltat_06_007_project.Controllers.TcpConnection;
import com.github.ltat_06_007_project.MainApplication;
import com.github.ltat_06_007_project.Server.Objects.ServerMessageObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

public class SocketController{
    private static final Logger log = LoggerFactory.getLogger(TcpConnection.class);

    private final ServerConnectionController connectionController;
    private final Executor executor;

    private volatile boolean online = false;


    private final LinkedBlockingQueue<ServerMessageObject> messageQueue = new LinkedBlockingQueue<>();
    private final Thread listenerThread;
    private final Thread senderThread;

    private Socket socket;
    private String socketId;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    String getSocketId() {
        return socketId;
    }


    SocketController(Socket socket, ServerConnectionController connectionController, Executor executor) {
        this.connectionController = connectionController;

        this.socket = socket;
        this.executor = executor;

        this.listenerThread = new Thread(this::startConnection);
        this.senderThread = new Thread(this::send);
    }

    void start() {
        executor.execute(listenerThread);
    }

    void close() {
        listenerThread.interrupt();
        senderThread.interrupt();
        connectionController.removeSocketId(socketId);
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }

        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
            }
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {

            }
        }
    }

    private void startConnection() {
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            log.info("incoming connection from {}", socket.getInetAddress().getHostAddress());
            online = true;
            executor.execute(senderThread);
            socketId = UUID.randomUUID().toString();
            while (connectionController.socketIdExsists(socketId)){
                socketId = UUID.randomUUID().toString();
            }
            log.info("connection from {} is identified as {}",
                    socket.getInetAddress().getHostAddress(), socketId);
            receive();
        } catch (Exception e) {
            log.info("Exception starting connection:",e);
        } finally {
            close();
        }
    }

    private void send() {
        while (!Thread.interrupted()) {
            try {
                ServerMessageObject message = messageQueue.take();
                String serializedMessage = MainApplication.mapper.writeValueAsString(message);
                outputStream.writeUTF(serializedMessage);
                outputStream.flush();
                log.info("sent message to {}",socketId);
            } catch (InterruptedException e) {
                break;
            } catch (IOException e) {
                log.info("",e);
                close();
            }
        }
    }


    private void receive() throws IOException {
        while (!Thread.interrupted()) {
            ServerMessageObject message =
                    MainApplication.mapper.readValue(inputStream.readUTF(), ServerMessageObject.class);
            message.setSocketId(socketId);
            connectionController.addToInbox(message);
            log.info("received message from {}",socketId);
        }
    }

    void sendMessage(ServerMessageObject messageObject) {
        try {
            messageQueue.put(messageObject);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    boolean isOnline() {
        return online;
    }
}
