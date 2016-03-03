package com.example.fabian.crazypatterns;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class SocketClient implements Runnable {
    private Socket socket;
    public PrintWriter mBufferOut;
    public BufferedReader mBufferIn;
    String currentline = "";


    public SocketClient(String pAdress, int pPort) {

        try {
            socket = new Socket(pAdress, pPort);

            try {
                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            }catch (Exception e) {
                Log.e("TCP", "S: Error", e);
            }

        }catch (Exception e) {
            Log.e("TCP", "C: Error", e);

        }
    }

    public void sendMessage(String message) {
        if (mBufferOut != null) {
            mBufferOut.println(message);
            mBufferOut.flush();
        }
    }

    public String getMessage(){
        try {
            /*String line;
            while (true) {
                line = mBufferIn.readLine();
                if(line != null) {
                    currentline = line;
                    break;
                }
            }*/
            currentline = mBufferIn.readLine();
        } catch (IOException e) {
            currentline = e.toString();

        }
        return currentline;
    }

    public void closeConnection(){
        try {
            socket.close();
        }catch (IOException e){
            Log.e("TCP Client", "Error");
        }
    }



    @Override
    public void run(){
    }
}
