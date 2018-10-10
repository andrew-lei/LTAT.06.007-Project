package com.github.ltat_06_007_project.Controllers;

import com.github.ltat_06_007_project.Models.HostModel;
import com.github.ltat_06_007_project.Objects.HostObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

@Component
public class BroadcastController {

    private final HostModel hostModel;

    @Autowired
    public BroadcastController (HostModel hostModel) {
        this.hostModel = hostModel;
        Runnable listenToAdvertisments = () -> listenAdvertisments();
        Runnable sendAdvertisments = () -> broadcastAdvertisment();
        new Thread(listenToAdvertisments).start();
        new Thread(sendAdvertisments).start();

    }

    public void listenAdvertisments()  {
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
                System.out.println("received broadcast");
                var host = new HostObject(new String(packet.getData()), packet.getAddress().getHostAddress());
                hostModel.updateHost(host);
            } catch (IOException e) {
                //TODO: handle
            }
        }
    }

    public void broadcastAdvertisment() {
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
                System.out.println("sent broadcast");
                Thread.sleep(1000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
