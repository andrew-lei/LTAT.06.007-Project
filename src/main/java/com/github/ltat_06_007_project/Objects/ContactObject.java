package com.github.ltat_06_007_project.Objects;

public class ContactObject {

    private String id;
    private String name;
    private byte[] symmetricKey;
    private byte[] publicKey;
    private String ip;
    private boolean allowed;

    public String getId() {
        return id;
    }

    public byte[] getKey() {
        return symmetricKey;
    }
    public String getIp() {
        return ip;
    }

    public boolean getAllowed() {
        return allowed;
    }


    public ContactObject(String id, byte[] key, String ip) {
        this.id = id;
        this.symmetricKey = key;
        this.ip = ip;
        this.allowed = true;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }
}
