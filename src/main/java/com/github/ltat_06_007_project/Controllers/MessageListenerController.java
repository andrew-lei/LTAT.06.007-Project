package com.github.ltat_06_007_project.Controllers;

import com.github.ltat_06_007_project.Models.HostModel;
import com.github.ltat_06_007_project.Models.MessageModel;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

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

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String message = bufferedReader.readLine();
            System.out.println("From: " + socket.getInetAddress().toString() + " Message: " + message);
            messageModel.updateMessages(
                    socket.getInetAddress().toString().replace("/", ""), message);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
