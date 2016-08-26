package com.example.javier.uoconnector.Client;

import com.example.javier.uoconnector.GUI.MainActivity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Javier on 18/08/2016.
 */
public class ClientThread extends Thread implements Runnable
{
    private ServerInfo serverInfo = null;
    private boolean bRun = false;

    public ClientThread()
    {
        start();
    }
    @Override
    public void run(){
        serverInfo = MainActivity.serverInfo;
        Connector.socket = getNewSocket();
        bRun = true;
    }

    public Socket getNewSocket()
    {
        try {
            InetAddress serverAddr = InetAddress.getByName(serverInfo.getServerIP());
            Socket sockt = new Socket(serverAddr, serverInfo.getServerPort());
            Connector.socket = sockt;
            return sockt;

        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

}
