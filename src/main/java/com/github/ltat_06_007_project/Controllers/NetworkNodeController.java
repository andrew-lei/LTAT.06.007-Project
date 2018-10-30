package com.github.ltat_06_007_project.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.ltat_06_007_project.MainApplication;
import com.github.ltat_06_007_project.Models.ContactModel;
import com.github.ltat_06_007_project.NetworkMessage.ContactRequest;
import com.github.ltat_06_007_project.NetworkMessage.NetworkMessageWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class NetworkNodeController {

    private static final Logger log = LoggerFactory.getLogger(NetworkNodeController.class);

    private final LinkedBlockingQueue<NetworkMessageWrapper> outbox = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<DatagramPacket> inbox = new LinkedBlockingQueue<>();
    private final CopyOnWriteArraySet<String> networkNodeAddressSet = new CopyOnWriteArraySet<>();
    private final ContactModel contactModel;

    @Autowired
    public NetworkNodeController(ContactModel contactModel) {
        this.contactModel = contactModel;

        new Thread(this::advertiseSelf).start();
        new Thread(this::listenNetworkMessages).start();
        new Thread(this::sendNetworkMessages).start();
        new Thread(this::handleInbox).start();

    }


    private void advertiseSelf() {
        try (DatagramSocket socket = new DatagramSocket()) {
            byte[] packetBytes = MainApplication.mapper.writeValueAsBytes(new NetworkMessageWrapper(0,""));
            var packet = new DatagramPacket(packetBytes, packetBytes.length);
            packet.setPort(42069);
            while (!Thread.interrupted()) {

                socket.setBroadcast(true);
                try {
                    packet.setAddress(InetAddress.getByName("255.255.255.255"));
                    socket.send(packet);
                    //log.info("sent out broadcast");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                socket.setBroadcast(false);
                for (String address: networkNodeAddressSet) {
                    try {
                        packet.setAddress(InetAddress.getByName(address));
                        socket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    break;
                }

            }
        } catch(SocketException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    private void listenNetworkMessages()  {
        try (  DatagramSocket socket  = new DatagramSocket(42069)) {
            while(!Thread.interrupted()) {
                try {
                    var packet = new DatagramPacket(new byte[1024], 0, 1024);
                    socket.receive(packet);

                    //log.info("received packet from {}", packet.getAddress().getHostAddress());
                    inbox.add(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendNetworkMessages()  {
        try ( DatagramSocket socket  = new DatagramSocket()) {
            while(!Thread.interrupted()) {
                try {
                    NetworkMessageWrapper message = outbox.take();
                    byte[] messageBytes = MainApplication.mapper.writeValueAsBytes(message);
                    var packet = new DatagramPacket(messageBytes, 0, messageBytes.length);
                    for (String address: networkNodeAddressSet) {
                        try {
                            packet.setPort(42069);
                            packet.setAddress(InetAddress.getByName(address));
                            socket.send(packet);
                            //log.info("sent packet to {}", packet.getAddress().getHostAddress());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    break;
                }
            }
        }catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleInbox() {
        while (!Thread.interrupted()) {
            try {
                DatagramPacket packet = inbox.take();
                byte[] packetBytes = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
                NetworkMessageWrapper networkMessageWrapper = MainApplication.mapper.readValue(packetBytes , NetworkMessageWrapper.class);

                if (networkMessageWrapper.getMessageType() == 0) {
                    networkNodeAddressSet.add(packet.getAddress().getHostAddress());
                } else if (networkMessageWrapper.getMessageType() == 1) {
                    String contactRequestSerialized = networkMessageWrapper.getSerializedMessage();
                    ContactRequest contactRequest = MainApplication.mapper.readValue(contactRequestSerialized, ContactRequest.class);
                    if (contactRequest.getRequesteeIp().equals(MainApplication.userIdCode)) {
                        contactModel.updateIp(contactRequest.getRequesterIp(),packet.getAddress().getHostAddress());
                    }
                }
            } catch (InterruptedException e) {
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void addToOutbox(NetworkMessageWrapper networkMessageWrapper) throws InterruptedException {
        outbox.put(networkMessageWrapper);
    }
}
