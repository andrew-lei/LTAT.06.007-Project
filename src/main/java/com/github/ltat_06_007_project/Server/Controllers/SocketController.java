package com.github.ltat_06_007_project.Server.Controllers;

import com.github.ltat_06_007_project.Controllers.TcpConnection;
import com.github.ltat_06_007_project.Cryptography;
import com.github.ltat_06_007_project.MainApplication;
import com.github.ltat_06_007_project.Objects.MessageObject;
import com.github.ltat_06_007_project.Server.Objects.ServerMessageObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.util.Base64;
import java.util.concurrent.LinkedBlockingQueue;

public class SocketController extends Thread {
    private static final Logger log = LoggerFactory.getLogger(SocketController.class);
    private final Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private final LinkedBlockingQueue<ServerMessageObject> messageQueue = new LinkedBlockingQueue<>();

    private SecretKey key;
    private String socketId;

    public SocketController(String socketId, Socket socket ){
        this.socketId = socketId;
        this.socket = socket;
        try {
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send(){
        while (!Thread.interrupted()) {
            try {
                ServerMessageObject message = messageQueue.take();
                String serializedMessage = MainApplication.mapper.writeValueAsString(message);
                String cypherText = Cryptography.encryptText(serializedMessage,key,"pass");
                outputStream.writeUTF(cypherText);
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
            String serializedMessage = Cryptography.decryptText(inputStream.readUTF(),key,"pass");
            ServerMessageObject message = MainApplication.mapper.readValue(serializedMessage, ServerMessageObject.class);
            // DO SOMETHING WITH MESSAGE
            log.info("received message from {}",socketId);
        }
    }

    @Override
    public void run() {
        super.run();
    }

    private void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
