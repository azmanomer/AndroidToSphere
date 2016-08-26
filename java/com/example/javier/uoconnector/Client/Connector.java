package com.example.javier.uoconnector.Client;

import com.example.javier.uoconnector.GUI.MainActivity;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Intermediate class between GUI and the Core
 * Handling the creation of sockets, connect them, log in...
 * Maybe I should rename it to ClientEngine or smth similar.
 */
public class Connector extends MainActivity
{
    public static Socket socket = null;
    private DataReader datareader = null;
    private DataWriter datawriter = null;
    private UserInfo userInfo = null;

    public Connector()
    {
        userInfo = MainActivity.userInfo;
        do {
            socketStart();
            sleep(1000);
        } while (socket == null);

        printText("Inicializando");
        openWriter();
        openReader();

        printText("Enviando datos");
        openMobileConnection();
        sendAccount();

        printConsoleOutput();
    }

    /*
     The byte sent here is what Sphere reads first,
      so Sphere can recognize this app on it's CClient::OnRxPing() method
      I send the letter 'm', readed as 0x6D hex value on Sphere's side.
     */
    private void openMobileConnection()
    {
        datawriter.sendText("m");
    }

    /*
        Sending account User and Password to the server
        UserID must be sent first,
        then we muse wait until Sphere receives and handle it
        then Password can be sent

        Sending them together makes sphere mix them and somehow giving problems to read them,
        so found it better to sent them separately.
     */
    private void sendAccount()
    {
        datawriter.sendText(userInfo.getUser());    // Sending UserID
        sleep(100);                                 // Making it wait
        datawriter.sendText(userInfo.getPassword());// Sending Password
    }

    /*
        Sending text to the App's window
     */
    private void printText(String txt)
    {
        MainActivity.tv.append("\n"+txt);
    }

    /*
        Calling the constructor of the data's reader.
     */
    public void openReader()
    {
        datareader = new DataReader(socket);
    }

    /*
        Calling the constructor for the data's sender.
     */
    public void openWriter()
    {
        datawriter = new DataWriter(socket);
    }

    /*
        Main loop, caring take of reading anything the server has to tell to the app.
        TODO: This will be used as 'Packet Reader' to translate what the Server sends: Char list, Page list, etc
        as for now it is just outputting anything the Sphere tells.
     */
    public void printConsoleOutput()
    {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while (MainActivity._RunMode == MainActivity.eRunMode.RM_Running )
        {
            System.out.println(datareader.receiveDataFromServer());
        }
        sleep(10000);
    }

    /*
        Creates the connection between the App and the Server
     */
    private void socketStart()
    {
        printText("Starting Sockets");
        while (socket == null) {
            MainActivity.thread = new ClientThread();
            printText("Connecting ...");
            sleep(1500);
        }
    }

    /*
        Finishes the connection.
     */
    public void socketClose()
    {
        try {
            printText("Closing Sockets");
            socket.close();
            socket = null;
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /*
        Shortcut for Thread.sleep, to avoid using the 'try - catch' everywhere.
     */
    private void sleep(int time)
    {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
