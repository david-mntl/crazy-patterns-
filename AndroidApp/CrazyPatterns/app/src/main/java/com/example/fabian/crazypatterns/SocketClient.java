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
    boolean connected = false;


    public SocketClient(String pAdress, int pPort) {

        try {
            socket = new Socket(pAdress, pPort);

            try {
                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                connected = true;
            }catch (Exception e) {
                return;
            }

        }catch (Exception e) {
            return;
        }
    }

    public boolean verifyConnection(){
        return connected;
    }

    public void sendMessage(String message) {
        if (mBufferOut != null) {
            mBufferOut.println(message);
            mBufferOut.flush();
        }
    }
    public String getMessage(){
        try{
            if(mBufferIn != null) {
                String line = mBufferIn.readLine();
                return line;
            }
            return "";
        }
        catch (java.net.SocketException c){
            Log.e("TCP Client" ,"Socket Close Successfully");
            return "";
        }
        catch(IOException e){
            Log.e("TCP Client", "Error" , e);
            return "";
        }
    }

    public void closeConnection(){
        try {
            socket.close();
        }catch (IOException e){
            Log.e("TCP Client", "Error");
        }
    }
    public boolean isConnectionAvailable(){
        if(mBufferOut == null && mBufferIn == null){
            return false;
        }
        else{
            return true;
        }
    }


    @Override
    public void run(){
    }
}
