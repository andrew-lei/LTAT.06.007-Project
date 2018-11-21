package com.github.ltat_06_007_project.Server.Controllers;

import java.net.Socket;

public class SocketController extends Thread {
    private final Socket socket;

    public SocketController(Socket socket){
        this.socket = socket;

    }

    @Override
    public void run() {
        super.run();
    }
}
