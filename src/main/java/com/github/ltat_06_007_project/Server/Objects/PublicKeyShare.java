package com.github.ltat_06_007_project.Server.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

//type 0 - serialized public key
public class PublicKeyShare {
    private final byte[] publicKey;

    @JsonCreator
    public PublicKeyShare(@JsonProperty("publicKey")byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }
}
