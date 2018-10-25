package com.github.ltat_06_007_project.Objects;

public class ContactObject {

    private String id;
    private String ip;
    private byte[] key;

    public String getId() {
        return id;
    }

    public byte[] getKey() {
        return key;
    }

    public ContactObject(String id, byte[] key) {
        this.id = id;
        this.key = key;
    }
}
