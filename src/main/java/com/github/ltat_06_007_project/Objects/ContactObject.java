package com.github.ltat_06_007_project.Objects;

public class ContactObject {

    private final String idCode;
    private final byte[] symmetricKey;
    private final byte[] publicKey;
    private final String ipAddress;
    private final boolean allowed;

    public ContactObject(String idCode, byte[] symmetricKey, byte[] publicKey, String ipAddress, boolean allowed) {
        this.idCode = idCode;
        this.symmetricKey = symmetricKey;
        this.publicKey = publicKey;
        this.ipAddress = ipAddress;
        this.allowed = allowed;
    }

    public String getIdCode() {
        return idCode;
    }

    public byte[] getSymmetricKey() {
        return symmetricKey;
    }
    public String getIpAddress() {
        return ipAddress;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public boolean getAllowed() {
        return allowed;
    }
}
