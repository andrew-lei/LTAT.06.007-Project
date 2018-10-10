package com.github.ltat_06_007_project.Controllers;

import com.github.ltat_06_007_project.Models.HostModel;
import com.github.ltat_06_007_project.Models.MessageModel;
import com.github.ltat_06_007_project.Objects.HostObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;

@Component
public class LanController {

    private final HostModel hostModel;
    private final MessageModel messageModel;

    @Autowired
    public LanController(HostModel hostModel, MessageModel messageModel){
        this.hostModel = hostModel;
        this.messageModel = messageModel;

        Runnable listenToConnections = () -> listenToConnections();
        new Thread(listenToConnections).start();
    }

    public  void multicastMessage(String message){
        for (Socket socket : hostModel.getAllSockets()) {
            sendMessage(socket, message);
        }
    }

    public static void sendMessage(Socket socket, String message) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream, true);
            printWriter.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Listens connections and receives HOSTNAME and saves it.
    public void listenToConnections() {
        try {
            ServerSocket serverSocket = new ServerSocket(42069);
            while (!Thread.interrupted()) {
                Socket socket = serverSocket.accept();
                System.out.println("Received a new connection");
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String hostName = reader.readLine();
                HostObject host = new HostObject(hostName,
                        socket.getInetAddress().toString().replace("/", ""), socket);
                hostModel.updateHost(host);
                MessageListenerController mlc = new MessageListenerController(hostName, socket, messageModel);
                new Thread(mlc).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
