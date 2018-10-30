package com.github.ltat_06_007_project.Controllers;

import com.github.ltat_06_007_project.Cryptography;
import com.github.ltat_06_007_project.MainApplication;
import com.github.ltat_06_007_project.Models.ChatModel;
import com.github.ltat_06_007_project.Models.ContactModel;
import com.github.ltat_06_007_project.Objects.MessageObject;
import com.github.ltat_06_007_project.Views.ChatView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.util.Base64;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

public class TcpConnection {
    private static final Logger log = LoggerFactory.getLogger(TcpConnection.class);

    private final ConnectionController connectionController;
    private final ContactModel contactModel;
    private final ChatView chatView;
    private final ChatModel chatModel;
    private final Executor executor;


    private final LinkedBlockingQueue<MessageObject> messageQueue = new LinkedBlockingQueue<>();
    private final Thread listenerThread;
    private final Thread senderThread;

    private Socket socket;
    private String contactId;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private SecretKey key;

    final boolean isIncoming;


    String getContactId() {
        return contactId;
    }


    TcpConnection(Socket socket, ConnectionController connectionController, ContactModel contactModel, ChatView chatView, ChatModel chatModel, Executor executor) {
        this.connectionController = connectionController;
        this.contactModel = contactModel;
        this.chatView = chatView;
        this.chatModel = chatModel;

        this.socket = socket;
        this.executor = executor;
        this.listenerThread = new Thread(this::startConnection);
        this.senderThread = new Thread(this::send);

        isIncoming = true;

    }

    TcpConnection(String contactId, ConnectionController connectionController, ContactModel contactModel, ChatView chatView, ChatModel chatModel, Executor executor) {
        this.connectionController = connectionController;
        this.contactModel = contactModel;
        this.chatView = chatView;
        this.chatModel = chatModel;

        this.contactId = contactId;
        this.executor = executor;
        this.listenerThread = new Thread(this::startConnection);
        this.senderThread = new Thread(this::send);

        isIncoming = false;
    }

    void start() {
        executor.execute(listenerThread);
    }

    void close() {
        listenerThread.interrupt();
        senderThread.interrupt();
        try {
            inputStream.close();
        } catch (NullPointerException | IOException e) {
        }
        try {
            outputStream.close();
        } catch (NullPointerException | IOException e) {
        }
        try {
            socket.close();
        } catch (NullPointerException | IOException e) {
        }
        connectionController.notifyClose(this);
    }

    private void startConnection() {
        try {
            if (isIncoming) {
                incomingConnection();
            } else {
                outgoingConnection();
            }
        } catch (IOException e) {
            log.info("",e);
        }
        close();
    }

    private void incomingConnection() throws IOException {
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
        log.info("incoming TCP connection from {}", socket.getInetAddress().getHostAddress());

        contactId = inputStream.readUTF();
        log.info("connection from {} is identified as {}", socket.getInetAddress().getHostAddress(), contactId);

        if (contactModel.getById(contactId).getAllowed() && connectionController.notifyStart(this)) {
            outputStream.writeUTF(MainApplication.userIdCode);
            outputStream.flush();
            key = new SecretKeySpec(Base64.getDecoder().decode(inputStream.readUTF()), "AES");
            //TODO: sync states
            executor.execute(listenerThread);
            receive();
        } else {
            log.info("denied connection to unauthorized id {}", contactId);
        }

    }

    private void outgoingConnection() throws IOException {
        socket = new Socket(contactModel.getById(contactId).getIp(), 42069);
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
        log.info("established outgoing TCP connection with {}",socket.getInetAddress().getHostAddress());
        outputStream.writeUTF(MainApplication.userIdCode);
        outputStream.flush();
        String claimedId = inputStream.readUTF();
        if (claimedId.equals(contactId) && connectionController.notifyStart(this)) {
            log.info("connection to {} is confirmed as {}", socket.getInetAddress().getHostAddress(), contactId);
            key = Cryptography.genAESKey();
            outputStream.writeUTF(Base64.getEncoder().encodeToString(key.getEncoded()));
            outputStream.flush();
            //TODO: sync states
            executor.execute(listenerThread);
            receive();


        } else {
            log.info("denied connection to false id {}", claimedId);
        }
    }


    private void send() {
        while (!Thread.interrupted()) {
            try {
                MessageObject message = messageQueue.take();
                byte[] serializedMessage = MainApplication.mapper.writeValueAsBytes(message);
                String cypherText = Base64.getEncoder().encodeToString(Cryptography.encryptText(key, serializedMessage));
                outputStream.writeUTF(cypherText);
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
            try {
                byte[] cypherTextBytes = Base64.getDecoder().decode(inputStream.readUTF());
                byte[] serializedMessage = Cryptography.decryptText(key, cypherTextBytes);
                MessageObject message = MainApplication.mapper.readValue(serializedMessage, MessageObject.class);
                chatView.insertMessage(chatModel.insertMessage(message));
            } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(MessageObject messageObject) {
        try {
            messageQueue.put(messageObject);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
