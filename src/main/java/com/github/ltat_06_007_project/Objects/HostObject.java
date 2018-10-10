package com.github.ltat_06_007_project.Objects;

public class HostObject {
    private String name;

    public String getAddress() {
        return address;
    }

    private String address;

    public HostObject(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
