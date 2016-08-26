package com.example.javier.uoconnector.Client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/*
    Class used to send data to the server.
 */
public class DataWriter
{
    private PrintWriter out;
    private Socket socket;

    public DataWriter(Socket socket)
    {
        try {
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
        Send text out, then flush out the output to the server.
     */
    public void sendText(String txt)
    {
        out.print(txt);
        out.flush();
    }
}
