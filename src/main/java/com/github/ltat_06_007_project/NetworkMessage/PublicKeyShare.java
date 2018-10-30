package com.github.ltat_06_007_project.NetworkMessage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PublicKeyShare {
    private final byte[] publicKey;
    private final String id;

    @JsonCreator
    public PublicKeyShare(@JsonProperty("publicKey")byte[] publicKey, @JsonProperty("id")String id) {
        this.publicKey = publicKey;
        this.id = id;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public String getId() {
        return id;
    }
}
