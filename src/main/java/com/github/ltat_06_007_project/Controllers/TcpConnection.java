package com.github.ltat_06_007_project.Controllers;

import com.fasterxml.jackson.core.type.TypeReference;
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

import javax.crypto.SecretKey;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class TcpConnection {
    private static final Logger log = LoggerFactory.getLogger(TcpConnection.class);

    private final ConnectionController connectionController;
    private final ContactModel contactModel;
    private final ChatViewController chatViewController;
    private final ChatModel chatModel;
    private final Executor executor;

    private volatile boolean online = false;


    private final LinkedBlockingQueue<MessageObject> messageQueue = new LinkedBlockingQueue<>();
    private final Thread senderThread;
    private final Thread listenerThread;

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
        this.senderThread = new Thread(this::startConnection);
        this.listenerThread = new Thread(this::receive);

        isIncoming = true;

    }

    TcpConnection(String contactId, ConnectionController connectionController, ContactModel contactModel, ApplicationContext applicationContext,  ChatModel chatModel, Executor executor) {
        this.connectionController = connectionController;
        this.contactModel = contactModel;
        this.chatViewController = applicationContext.getBean(ChatViewController.class);
        this.chatModel = chatModel;

        this.contactId = contactId;
        this.executor = executor;
        this.senderThread = new Thread(this::startConnection);
        this.listenerThread = new Thread(this::receive);

        isIncoming = false;
    }

    void start() {
        executor.execute(senderThread);
    }

    void close() {
        senderThread.interrupt();
        listenerThread.interrupt();
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
            if (contactId != null) {
                chatViewController.connectionChanged(contactId,false);
            }
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

        if (optionalContactObject.get().isAllowed()) {
            outputStream.writeUTF(MainApplication.userIdCode);
            outputStream.flush();
            key = Cryptography.decryptAESKey(inputStream.readUTF(),MainApplication.privateKey);
            log.info("connection from {} has been secured, starting communication", contactId);
            send();
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
        PublicKey contactPublicKey = Cryptography.getPublicKey(contactPublicKeyContainer);
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
            String encryptedKey = Cryptography.encryptAESKey(key,contactPublicKey);
            outputStream.writeUTF(encryptedKey);
            outputStream.flush();
            log.info("connection to {} has been secured, starting communication", contactId);
            send();


        } else {
            log.info("denied connection to false id {}", claimedId);
        }
    }


    private void send() throws IOException  {

        chatViewController.connectionChanged(contactId,true);
        online = true;

        List<String> localMessages = chatModel.getMessages().stream()
                .filter(m -> m.getSenderId().equals(contactId))
                .map(m->hashMessage(m))
                .collect(Collectors.toList());

        outputStream.writeUTF(MainApplication.mapper.writeValueAsString(localMessages));

        TypeReference<List<String>> tr = new TypeReference<List<String>>(){};
        List<String> theirMessages = MainApplication.mapper.readValue(inputStream.readUTF(),tr);

        executor.execute(listenerThread);
        chatModel.getMessages().stream()
                .filter(m -> m.getSenderId().equals(contactId))
                .filter(m -> !theirMessages.contains(hashMessage(m)))
                .forEach(m -> {
                    try {
                        outputStream.writeUTF(MainApplication.mapper.writeValueAsString(m));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        while (!Thread.interrupted()) {
            try{
                MessageObject message = messageQueue.take();
                String serializedMessage = MainApplication.mapper.writeValueAsString(message);
                outputStream.writeUTF(Cryptography.encryptText(serializedMessage,key, "pass"));
                outputStream.flush();
                log.info("sent message to {}",contactId);
            } catch (InterruptedException e){
                break;
            }
        }
    }


    private void receive(){
        while (!Thread.interrupted()) {
            try {
                String serializedMessage = Cryptography.decryptText(inputStream.readUTF(),key, "pass");
                MessageObject message = MainApplication.mapper.readValue(serializedMessage, MessageObject.class);
                chatViewController.insertMessage(chatModel.insertMessage(message));
                log.info("received message from {}",contactId);
            } catch (IOException e) {
                log.info("",e);
                close();
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

    boolean isOnline() {
        return online;
    }

    private static String hashMessage(MessageObject message) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String messageSum =  message.getSenderId()+message.getReceiverId()+message.getContent();
            byte[] encodedhash = digest.digest(messageSum.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
