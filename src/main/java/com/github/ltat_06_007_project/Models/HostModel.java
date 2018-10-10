package com.github.ltat_06_007_project.Models;

import com.github.ltat_06_007_project.Objects.HostObject;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Set;

@Component
public class HostModel {

    private final HashMap<String,String> hostIpToHostName = new HashMap<String, String>();

    public void updateHost(HostObject host) {
        hostIpToHostName.put(host.getAddress(),host.getName());
    }

    public Set<String> getAllIps() {
        return hostIpToHostName.keySet();
    }

    public String getHostName(String ip) {
        return hostIpToHostName.get(ip);
    }
    
}
