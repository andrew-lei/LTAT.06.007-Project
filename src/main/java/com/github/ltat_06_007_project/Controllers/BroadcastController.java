package com.github.ltat_06_007_project.Controllers;

import com.github.ltat_06_007_project.Models.HostModel;
import com.github.ltat_06_007_project.Models.MessageModel;
import com.github.ltat_06_007_project.Objects.HostObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

@Component
public class BroadcastController {

    private final HostModel hostModel;
    private final MessageModel messageModel;

    @Autowired
    public BroadcastController (HostModel hostModel, MessageModel messageModel) {
        this.hostModel = hostModel;
        this.messageModel = messageModel;

        Runnable listenToAdvertisements = () -> listenAdvertisements();
        Runnable sendAdvertisements = () -> broadcastAdvertisement();
        new Thread(listenToAdvertisements).start();
        new Thread(sendAdvertisements).start();

    }

    public void listenAdvertisements()  {
        DatagramSocket socket;
        try {
            socket = new DatagramSocket(42069);
        }catch (SocketException e) {
            throw new RuntimeException(e);
        }
        var packet = new DatagramPacket(new byte[1024], 0, 0);

        while (!Thread.interrupted()) {
            try {
                socket.receive(packet);
                String ip = packet.getAddress().getHostAddress();
                String hostname = new String(packet.getData());
                var host = new HostObject(new String(packet.getData()), ip);
                /*
                if (hostModel.getHostName(ip) != hostname){
                    LanController.sendMessage(ip,"HOSTNAME");
                    var host = new HostObject(new String(packet.getData()), ip);
                    hostModel.updateHost(host);
                }
                */
                hostModel.updateHost(host);
            } catch (IOException e) {
                //TODO: handle
            }
        }
    }

    public void broadcastAdvertisement() {
        DatagramSocket socket;
        try {
            socket = new DatagramSocket();
            socket.setBroadcast(true);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        byte[] hostname = "HOSTNAME".getBytes(StandardCharsets.UTF_8);
        var packet = new DatagramPacket(hostname, hostname.length);
        packet.setPort(42069);
        try {
            packet.setAddress(InetAddress.getByName("255.255.255.255"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        while (!Thread.interrupted()) {
            try {
                socket.send(packet);
                Thread.sleep(1000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}