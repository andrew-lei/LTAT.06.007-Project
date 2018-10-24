package com.github.ltat_06_007_project.Controllers;

import com.github.ltat_06_007_project.Models.HostModel;
import com.github.ltat_06_007_project.Models.MessageModel;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class MessageListenerController implements Runnable {
    private final MessageModel messageModel;
    private final Socket socket;


    public MessageListenerController(Socket socket, MessageModel messageModel) {
        this.socket = socket;
        this.messageModel = messageModel;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            byte[] messageBuffer = new byte[1024];
            int bytes;

            while ((bytes = inputStream.read(messageBuffer)) != -1){
                byte[] message = Arrays.copyOf(messageBuffer, bytes);
                messageModel.updateMessages(
                        socket.getInetAddress().toString().replace("/", ""), message);
                System.out.println("From: " + socket.getInetAddress().toString() + " Message: " + new String(message));
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
