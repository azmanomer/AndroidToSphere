package com.example.javier.uoconnector.Client;

/**
 * Created by Javier on 26/08/2016.
 */
public class ServerInfo
{
    // TODO: GUI from Android to change connection info.

    private static final int ServerPort = 2593;
    private static final String ServerIP = "192.168.0.13";

    public static int getServerPort() {
        return ServerPort;
    }
    public static String getServerIP() {
        return ServerIP;
    }
}
