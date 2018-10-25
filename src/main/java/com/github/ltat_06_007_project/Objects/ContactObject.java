package com.github.ltat_06_007_project.Objects;

public class ContactObject {

    private String id;
    private byte[] key;
    private String ip;

    public String getId() {
        return id;
    }

    public byte[] getKey() {
        return key;
    }
    public String getIp) {
        return ip;
    }


    public ContactObject(String id, byte[] key, String ip) {
        this.id = id;
        this.key = key;
        this.ip= ip;
    }
}
