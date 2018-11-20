package de.javawi.jstun.test;

import de.javawi.jstun.attribute.*;
import de.javawi.jstun.header.MessageHeader;
import de.javawi.jstun.header.MessageHeaderParsingException;
import de.javawi.jstun.util.UtilityException;

import java.io.IOException;
import java.net.*;


public class NatInfoTest {
    InetAddress iaddress;
    int port;

    public NatInfoTest(InetAddress iaddress, int port) {
        this.iaddress = iaddress;
        this.port = port;
    }

    public static void main(String args[]){
        int timeout = 300;
        try {
            DatagramSocket sendSocket = new DatagramSocket(new InetSocketAddress(InetAddress.getByName("192.168.1.243"), 0 /*23053*/));
            sendSocket.connect(InetAddress.getByName("jstun.javawi.de"), 3478);
            sendSocket.setSoTimeout(timeout);

            MessageHeader sendMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingRequest);
            sendMH.generateTransactionID();

            ChangeRequest changeRequest = new ChangeRequest();
            sendMH.addMessageAttribute(changeRequest);

            byte[] data = sendMH.getBytes();
            DatagramPacket send = new DatagramPacket(data, data.length);
            sendSocket.send(send);
            System.out.println("Binding Request sent.");

            MessageHeader receiveMH = new MessageHeader();
            while (!(receiveMH.equalTransactionID(sendMH))) {
                DatagramPacket receive = new DatagramPacket(new byte[200], 200);
                sendSocket.receive(receive);
                receiveMH = MessageHeader.parseHeader(receive.getData());
                receiveMH.parseAttributes(receive.getData());
            }

            MappedAddress ma = (MappedAddress) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.MappedAddress);
            ChangedAddress ca = (ChangedAddress) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.ChangedAddress);
            System.out.println("Local address:"+sendSocket.getLocalAddress().toString());
            System.out.println("Local port:"+sendSocket.getLocalPort());
            System.out.println("Mapped address:"+ma.getAddress().getInetAddress().toString());
            System.out.println("Mapped port:"+ma.getPort());
            System.out.println("Changed address:"+ca.getAddress().getInetAddress().toString());
            System.out.println("Changed port:"+ca.getPort());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
