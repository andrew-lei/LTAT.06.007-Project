package com.github.ltat_06_007_project;

public class Configuration {
    private static String keyPath = ".";

    private static int port = 42069;
    private static String mainServerIp = "0.0.0.0";

    private static boolean isServer = false;
    private static int serverPort = 23053;

    public static boolean isServer() {
        return isServer;
    }
    public static int getPort() {
        return port;
    }
    public static int getServerPort() {
        return serverPort;
    }
    public static String getKeyPath() {
        return keyPath;
    }
    public static String getMainServerIp() {
        return mainServerIp;
    }
}
