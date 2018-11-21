package com.github.ltat_06_007_project.Controllers;

import com.github.ltat_06_007_project.Cryptography;
import com.github.ltat_06_007_project.MainApplication;
import com.github.ltat_06_007_project.Models.ChatModel;
import com.github.ltat_06_007_project.Models.ContactModel;
import com.github.ltat_06_007_project.Objects.ContactObject;
import com.github.ltat_06_007_project.Objects.MessageObject;
import com.github.ltat_06_007_project.Views.ChatView.ChatViewController;
import org.digidoc4j.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

public class TcpConnection {
    private static final Logger log = LoggerFactory.getLogger(TcpConnection.class);

    private final ConnectionController connectionController;
    private final ContactModel contactModel;
    private final ChatViewController chatViewController;
    private final ChatModel chatModel;
    private final Executor executor;

    private volatile boolean online = false;


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


    TcpConnection(Socket socket, ConnectionController connectionController, ContactModel contactModel, ApplicationContext applicationContext, ChatModel chatModel, Executor executor) {
        this.connectionController = connectionController;
        this.contactModel = contactModel;
        this.chatViewController = applicationContext.getBean(ChatViewController.class);
        this.chatModel = chatModel;

        this.socket = socket;
        this.executor = executor;
        this.listenerThread = new Thread(this::startConnection);
        this.senderThread = new Thread(this::send);

        isIncoming = true;

    }

    TcpConnection(String contactId, ConnectionController connectionController, ContactModel contactModel, ApplicationContext applicationContext,  ChatModel chatModel, Executor executor) {
        this.connectionController = connectionController;
        this.contactModel = contactModel;
        this.chatViewController = applicationContext.getBean(ChatViewController.class);
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

        connectionController.notifyClose(this);
    }

    private void startConnection() {
        try {
            try {
                if (isIncoming) {
                    incomingConnection();
                } else {
                    outgoingConnection();
                }
            } catch (IOException e) {
                log.info("",e);
            }
        } catch (Exception e) {
            log.info("",e);
        } finally {
            close();
        }
    }

    private void incomingConnection() throws IOException {
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
        log.info("incoming TCP connection from {}", socket.getInetAddress().getHostAddress());

        contactId = inputStream.readUTF();
        log.info("connection from {} is identified as {}", socket.getInetAddress().getHostAddress(), contactId);

        if (!connectionController.notifyStart(this)) {
            log.info("stopping duplicate connection to {}", contactId);
            return;
        }

        Optional<ContactObject> optionalContactObject = contactModel.getById(contactId);
        if (!optionalContactObject.isPresent()) {
            log.info("couldn't find contact {}, contactId");
            return;
        }

        if (optionalContactObject.get().getAllowed()) {
            outputStream.writeUTF(MainApplication.userIdCode);
            outputStream.flush();
            byte[] encryptedKey = Base64.getDecoder().decode(inputStream.readUTF());
            key = new SecretKeySpec(Cryptography.decryptBytes(MainApplication.privateKey,encryptedKey), "AES");
            log.info("connection from {} has been secured, starting communication", contactId);
            //TODO: sync states
            online = true;
            executor.execute(senderThread);
            receive();
        } else {
            log.info("denied connection to unauthorized id {}", contactId);
        }

    }

    private void outgoingConnection() throws IOException {

        Optional<ContactObject> optionalContactObject = contactModel.getById(contactId);
        if (!optionalContactObject.isPresent()) {
            return;
        }
        socket = new Socket(optionalContactObject.get().getIpAddress(), 42069);
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());


        optionalContactObject = contactModel.getById(contactId);
        if (!optionalContactObject.isPresent()) {
            return;
        }


        log.info("established outgoing TCP connection with {}",socket.getInetAddress().getHostAddress());
        Container contactPublicKeyContainer = Cryptography.containerFromBytes(optionalContactObject.get().getPublicKey());
        PublicKey contactPublicKey = Cryptography.keyFromBytes((contactPublicKeyContainer).getDataFiles().get(0).getBytes());
        outputStream.writeUTF(MainApplication.userIdCode);
        outputStream.flush();

        if (!connectionController.notifyStart(this)) {
            log.info("stopping duplicate connection to {}", contactId);
            return;
        }


        String claimedId = inputStream.readUTF();
        if (claimedId.equals(contactId)) {
            log.info("connection to {} is confirmed as {}", socket.getInetAddress().getHostAddress(), contactId);
            key = Cryptography.genAESKey();
            optionalContactObject = contactModel.getById(contactId);
            if (!optionalContactObject.isPresent()) {
                return;
            }
            byte[] encryptedKey = Cryptography.encryptBytes(contactPublicKey, key.getEncoded());
            outputStream.writeUTF(Base64.getEncoder().encodeToString(encryptedKey));
            outputStream.flush();
            log.info("connection to {} has been secured, starting communication", contactId);
            online = true;
            //TODO: sync states
            executor.execute(senderThread);
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
                outputStream.flush();
                log.info("sent message to {}",contactId);
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
                chatViewController.insertMessage(chatModel.insertMessage(message));
                log.info("received message from {}",contactId);
            } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
                e.printStackTrace();
            }
        }
    }

    void sendMessage(MessageObject messageObject) {
        try {
            messageQueue.put(messageObject);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean isOnline() {
        return online;
    }
}
