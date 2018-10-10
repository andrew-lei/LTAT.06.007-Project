package com.github.ltat_06_007_project;

import java.io.*;
import java.net.Socket;

public class LocalConnection {

    public Socket socket;
    public InputStream inputStream;
    public OutputStream outputStream;
    public String host;
    public int port;

    public LocalConnection(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            this.socket = new Socket(host, port);
            this.inputStream = this.socket.getInputStream();
            this.outputStream = this.socket.getOutputStream();
        } catch (Exception e) {
            // Should log error
        }
    }

    public LocalConnection(Socket socket, int port) {
        this.socket = socket;
        this.host = socket.getInetAddress().getHostAddress();
        this.port = port;
        try {
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
        } catch (Exception e) {
            // Should log error
        }
    }

    // Sends message that ends with \n.
    public boolean sendMessage(String message) {
        try {
            PrintWriter out = new PrintWriter(this.outputStream, true);
            out.println(message);
            return true;
        } catch (Exception e) {
            // Should log error
            return false;
        }
    }

    // Checks if other host has sent a message
    public boolean hasMessage() throws Exception {
        if (this.inputStream.available() > 0) {
            return true;
        } else return false;
    }

    // Reads received message.
    public String readMessage() throws Exception {
        String message = "";
        while (this.inputStream.available() > 0) {
            BufferedReader in = new BufferedReader(new InputStreamReader(this.inputStream));
            message += in.readLine();
        }
        this.socket.close();
        return message;
    }

    // Closes the connection.
    public void close() {
        try {
            this.socket.close();
        } catch (IOException e) {
            // log error
        }
    }
}
