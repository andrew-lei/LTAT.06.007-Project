package com.github.ltat_06_007_project.Controllers;

import com.github.ltat_06_007_project.Models.HostModel;
import com.github.ltat_06_007_project.Models.MessageModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class MessageListenerController implements Runnable {
    private final Socket socket;
    private final MessageModel messageModel;
    private final String host;


    public MessageListenerController(String host, Socket socket, MessageModel messageModel) {
        this.host = host;
        this.socket = socket;
        this.messageModel = messageModel;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            messageModel.updateMessages(host, bufferedReader.readLine());
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
