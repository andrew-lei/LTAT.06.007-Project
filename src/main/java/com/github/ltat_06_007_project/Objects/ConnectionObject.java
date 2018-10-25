package com.github.ltat_06_007_project.Objects;

public class ConnectionObject {
    private String name;
    private String address;


    public ConnectionObject(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }
    public String getAddress() {
        return address;
    }

    public void setName(String name) {
        this.name = name;
    }
}
