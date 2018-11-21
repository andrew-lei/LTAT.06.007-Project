package com.github.ltat_06_007_project;

import java.util.ArrayList;
import java.util.List;

public class Configuration {

    private static List<String> upstreams = new ArrayList<String>();
    private static boolean server = false;

    public static boolean isServer() {
        return server;
    }
}
