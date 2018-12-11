package com.github.ltat_06_007_project.Server;

import com.github.ltat_06_007_project.Configuration;
import com.github.ltat_06_007_project.Server.Controllers.SocketController;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;

public class ServerThread extends Thread {
    private int port;
    private PublicKey publicKey;
    private SecretKey secretKey;

    public ServerThread(int port, PublicKey publicKey, SecretKey secretKey) {
        this.port = port;
        this.publicKey = publicKey;
        this.secretKey = secretKey;
    }

    @Override
    public void run(){
        while (!Thread.interrupted()){
            try {
                ServerSocket serverSocket = new ServerSocket(Configuration.getServerPort());
                Socket socket = serverSocket.accept();
                SocketController socketController = new SocketController("",socket);
                socketController.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
