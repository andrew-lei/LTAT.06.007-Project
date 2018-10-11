package com.github.ltat_06_007_project.Controllers;

import com.github.ltat_06_007_project.Models.HostModel;
import com.github.ltat_06_007_project.Models.MessageModel;
import com.github.ltat_06_007_project.Objects.HostObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.*;

@Component
public class LanController {

    private final HostModel hostModel;
    private final MessageModel messageModel;

    @Autowired
    public LanController(HostModel hostModel, MessageModel messageModel){
        this.hostModel = hostModel;
        this.messageModel = messageModel;

        Runnable listenConnections = () -> listenConnections();
        new Thread(listenConnections).start();
    }

    public void multicastMessage(String message){
        for (String ip : hostModel.getAllIps()) {
            sendMessage(ip, message);
        }
    }

    public static void sendMessage(String ip, String message) {
        try {
            Socket socket = new Socket(ip, 42069);
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream, true);
            printWriter.println(message);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Listens for new connections connections
    public void listenConnections() {
        try {
            ServerSocket serverSocket = new ServerSocket(42069);
            while (!Thread.interrupted()) {
                Socket socket = serverSocket.accept();
                System.out.println(hostModel.getAllIps().toString());
                MessageListenerController messageListenerController
                        = new MessageListenerController(socket, messageModel);
                new Thread(messageListenerController).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
