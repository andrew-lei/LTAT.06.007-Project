package com.github.ltat_06_007_project.Controllers;

import com.github.ltat_06_007_project.MainApplication;
import com.github.ltat_06_007_project.Models.ContactModel;
import com.github.ltat_06_007_project.Objects.MessageObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

class OutgoingTcpConnection {

    private final String contactId;
    private final ContactModel contactModel;
    private final ConnectionController connectionController;
    private final LinkedBlockingQueue<MessageObject> messageQueue = new LinkedBlockingQueue<>();

    private static final Logger log = LoggerFactory.getLogger(OutgoingTcpConnection.class);



    OutgoingTcpConnection(String contactId, ContactModel contactModel, ConnectionController connectionController) {
        this.contactId = contactId;
        this.contactModel = contactModel;
        this.connectionController = connectionController;
    }

    void establishConnection() {
        while (!Thread.interrupted()) {
            try (var socket = new Socket(contactModel.getById(contactId).getIp(), 42069)) {
                log.info("established connection with {}",socket.getInetAddress().getHostAddress());
                var inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                var outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

                outputStream.writeUTF(MainApplication.userIdCode);
                String response = inputStream.readUTF();
                if (!response.equals(contactId)) {
                    log.info("{} is not {}, dropping connection",socket.getInetAddress().getHostAddress(),contactId);
                    continue;
                }
                log.info("{} is {}, securing connection",socket.getInetAddress().getHostAddress(),contactId);

                connectionController.confirmContactConnection(contactId);

                //TODO: sync states
                //TODO: generate and send new symmetric key
                //TODO: start message listener thread

                while (!Thread.interrupted()) {
                    try {
                        outputStream.writeUTF(messageQueue.take().toString());
                    } catch (InterruptedException e) {
                        break;
                    }
                }

            } catch (IOException e) {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException ex) {
                    break;
                }
            }


        }
    }
}
