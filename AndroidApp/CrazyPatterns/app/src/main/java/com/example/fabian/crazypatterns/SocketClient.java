package com.example.fabian.crazypatterns;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class SocketClient implements Runnable {
    private boolean mRun = false;
    public PrintWriter mBufferOut;
    private BufferedReader mBufferIn;
    private Socket socket;
    private boolean running;

    public SocketClient(String pAdress, int pPort) {

        try {
            Log.e("TCP Client", "C: Connecting...");
            socket = new Socket(pAdress, pPort);

            try {
                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                Log.e("TCP Client", "Conected");
            } catch (Exception e) {
                Log.e("TCP", "S: Error", e);
            }

        } catch (Exception e) {

            Log.e("TCP", "C: Error", e);

        }
    }

    public void sendMessage(String message) {
        if(mBufferOut != null){
        }
        if (mBufferOut != null && !mBufferOut.checkError()) {
            mBufferOut.println(message);
            //mBufferOut.flush();
        }
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
