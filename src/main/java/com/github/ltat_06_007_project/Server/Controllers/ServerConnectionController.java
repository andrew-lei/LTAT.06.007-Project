package com.github.ltat_06_007_project.Server.Controllers;

import com.github.ltat_06_007_project.Configuration;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerConnectionController extends Thread{
    private HashMap<String, SocketController> connections;

    @Override
    public void run() {
        while(!Thread.interrupted()){
            try {
                ServerSocket serverSocket = new ServerSocket(Configuration.getServerPort());
                Socket socket = serverSocket.accept();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        super.run();
    }
}
