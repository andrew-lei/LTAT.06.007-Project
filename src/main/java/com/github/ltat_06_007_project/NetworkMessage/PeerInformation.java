package com.github.ltat_06_007_project.NetworkMessage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class PeerInformation {
    private final String[] peers;

    @JsonCreator
    public PeerInformation(@JsonProperty("peers")Collection<String> peerList) {
        peers = peerList.stream().toArray(String[]::new);
    }

    public String[] getPeers() {
        return peers;
    }
}
