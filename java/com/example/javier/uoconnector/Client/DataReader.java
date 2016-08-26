package com.example.javier.uoconnector.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Javier on 26/08/2016.
 */

/*
    Class used to read Input from the Server.
 */
public class DataReader
{
    private BufferedReader in = null;
    private Socket socket = null;

    public static final int BUFFER_SIZE = 2048;

    public DataReader(Socket _socket)
    {
        socket = _socket;
        try
        {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
        Reads data from server, asuming there is some data to read.
     */
    public String receiveDataFromServer() {
        try {
            String message = "";
            int charsRead = 0;
            char[] buffer = new char[BUFFER_SIZE];

            while ((in.readLine()) != null) {
                message += new String(buffer).substring(0, charsRead);
            }
            return message;
        } catch (IOException e) {
            return "Error receiving response:  " + e.getMessage();
        }
    }

    /*
        Reads future data from the server, the function makes the app wait until some data arrived
        Maybe I have to find a better way to do so, not hanging the app.
     */
    public String receiveNextDataFromServer() {
        try {
            char[] buffer = new char[BUFFER_SIZE];
            int count = 100;    //Timeout = 'count' times * Thread.sleep(100)
            while (in.read(buffer) == -1) { // Loop making:
                count--;
                if (count <= 0)
                    return "Error receiving response: Timeout"; // A) Stopping it if it 'time out' because of too many iteractions.
                try {
                    Thread.sleep(100);  // B) Making the thread sleep some time to wait for response.
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String message = "";
            int charsRead = 0;

            while ((charsRead = in.read(buffer)) != -1) {
                message += new String(buffer).substring(0, charsRead);
            }
            return message;
        } catch (IOException e) {
            return "Error receiving response:  " + e.getMessage();
        }
    }
}
