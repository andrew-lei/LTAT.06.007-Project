package com.github.ltat_06_007_project.Controllers;

import com.github.ltat_06_007_project.Models.HostModel;
import com.github.ltat_06_007_project.Objects.HostObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

@Component
public class BroadcastController {

    private final HostModel hostModel;

    @Autowired
    public BroadcastController (HostModel hostModel) {
        this.hostModel = hostModel;
    }

    public void listenAdvertisments() throws SocketException {
        var socket = new DatagramSocket(42069);
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

    public void broadcastAdvertisment() throws SocketException {
        var socket = new DatagramSocket(42069);
        socket.setBroadcast(true);
        byte[] hostname = "HOSTNAME".getBytes(StandardCharsets.UTF_8);
        var packet = new DatagramPacket(hostname, hostname.length);
        while (!Thread.interrupted()) {
            try {
                socket.send(packet);
                System.out.println("sent broadcast");
                Thread.sleep(1000);
            } catch (IOException e) {
                //TODO: handle
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
