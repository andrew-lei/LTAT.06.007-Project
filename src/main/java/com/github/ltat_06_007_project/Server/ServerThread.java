package com.github.ltat_06_007_project.Server;

import javax.crypto.SecretKey;
import java.security.PublicKey;

public class ServerThread extends Thread {
    private int port;
    private PublicKey publicKey;
    private SecretKey ;

    public ServerThread(int port) {
        this.port = port;
    }

    @Override
    public static void run(){
        while (!Thread.interrupted()){

        }
    }
}
