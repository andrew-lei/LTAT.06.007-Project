package com.github.ltat_06_007_project.Models;

import com.github.ltat_06_007_project.Objects.HostObject;
import org.springframework.stereotype.Component;

import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

@Component
public class HostModel {

    private final HashMap<String,String> hostIpToHostName = new HashMap<String, String>();
    private final HashMap<String,Socket> hostIpToSocket = new HashMap<String, Socket>();

    public void updateHost(HostObject host) {
        hostIpToHostName.put(host.getAddress(),host.getName());
        hostIpToSocket.put(host.getAddress(), host.getSocket());
    }

    public Set<String> getAllIps() {
        return hostIpToHostName.keySet();
    }

    public Collection<Socket> getAllSockets() {
        return hostIpToSocket.values();
    }

    public String getHostName(String ip) {
        return hostIpToHostName.get(ip);
    }

    public Socket getSocket(String ip) {
        return hostIpToSocket.get(ip);
    }
}
