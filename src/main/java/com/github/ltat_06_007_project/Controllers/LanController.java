package com.github.ltat_06_007_project.Controllers;

import com.github.ltat_06_007_project.Models.HostModel;
import com.github.ltat_06_007_project.Objects.HostObject;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;

public class LanController {

    private final HostModel hostModel;

    public LanController(HostModel hostModel){
        this.hostModel = hostModel;

        Runnable listenToConnections = () -> listenToConnections();
        Runnable listenToMessages = () -> listenToMessages();
        new Thread(listenToConnections).start();
        new Thread(listenToMessages).start();
    }

    public  void multicastMessage(String message){
        for (Socket socket : hostModel.getAllSockets()) {
            sendMessage(socket, message);
        }
    }

    public static void sendMessage(Socket socket, String message) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(message.getBytes(Charset.forName("UTF-8")));
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
                HostObject host = new HostObject(reader.readLine(),
                        socket.getInetAddress().toString().replace("/", ""), socket);
                hostModel.updateHost(host);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenToMessages() {
        //TODO
    }
}
