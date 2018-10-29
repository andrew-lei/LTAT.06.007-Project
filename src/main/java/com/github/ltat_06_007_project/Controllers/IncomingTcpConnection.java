package com.github.ltat_06_007_project.Controllers;

import com.github.ltat_06_007_project.Models.ContactModel;
import com.github.ltat_06_007_project.Objects.MessageObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

class IncomingTcpConnection {


    private static final Logger log = LoggerFactory.getLogger(IncomingTcpConnection.class);

    private final Socket socket;
    private final ConnectionController connectionController;
    private final ContactModel contactModel;
    private final Thread thread;
    private String id;
    private boolean connectionTrusted = false;

    private final LinkedBlockingQueue<MessageObject> messageQueue = new LinkedBlockingQueue<>();


    IncomingTcpConnection(Socket socket, ConnectionController connectionController, ContactModel contactModel) {
        this.socket = socket;
        this.connectionController = connectionController;
        this.contactModel = contactModel;
        thread = new Thread(this::connect);
        connectionController.addThread(thread);
    }

    private void connect() {

        try {
            var inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            var outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            id = inputStream.readUTF();
            log.info("incoming TCP connection from {} at ip {}",id,socket.getInetAddress().getHostAddress());
            if (contactModel.getById(id).getAllowed()) {
                connectionController.confirmContactConnection(thread,id);
                connectionTrusted = true;
                outputStream.writeUTF("39412301337");
            }


            //TODO: sync states
            //TODO: get new symmetric key
            //TODO: start message listener thread
            while (!Thread.interrupted()) {
                try {
                    outputStream.writeUTF(messageQueue.take().toString());
                } catch (InterruptedException e) {
                    break;
                }
            }

        } catch (IOException e) {

        }
        if (connectionTrusted) {
            connectionController.allowContact(id);
        }
    }

}
