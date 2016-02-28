package com.example.fabian.crazypatterns;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import cn.pedant.SweetAlert.*;

public class Developer extends ActionBarActivity {

    Thread pThread;
    SocketClient channel;
    String pIP = "";
    int pPort = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);

        final Button connectButton = (Button) findViewById(R.id.bConnect);
        connectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText ipText = (EditText)findViewById(R.id.ip_text);
                EditText portText = (EditText)findViewById(R.id.port_text);
                EditText msgText = (EditText)findViewById(R.id.input_text);
                String newIP = ipText.getText().toString();
                String newPortS =portText.getText().toString();
                int newPort= 0;

                if(newIP.length() < 4) {
                    new SweetAlertDialog(Developer.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Warning")
                            .setContentText("Please fill IP and Port fields")
                            .show();
                }
                else{
                    if(newPortS.length() < 1){
                        new SweetAlertDialog(Developer.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Warning")
                                .setContentText("Please use a valid Port")
                                .show();
                    }
                    else{
                        newPort = Integer.parseInt(newPortS);
                        ipText.setEnabled(false);
                        portText.setEnabled(false);
                        pIP = newIP;
                        pPort = newPort;
                        new SocketAsync().execute("init");
                        msgText.requestFocus();
                    }
                }



            }
        });

        final Button resetButton = (Button) findViewById(R.id.bReset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(channel != null) {
                    resetConnection();
                }
                else{
                    new SweetAlertDialog(Developer.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("No connection to reset")
                            .show();
                }
            }
        });

        final Button sendButton = (Button) findViewById(R.id.bSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(channel != null) {
                    EditText msgText = (EditText)findViewById(R.id.input_text);
                    String sendTxt = msgText.getText().toString();
                    channel.sendMessage(sendTxt);
                    msgText.setText("");
                }
                else{
                    new SweetAlertDialog(Developer.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("No connection. Can't send message")
                            .show();
                }
            }
        });
    }

    private void resetConnection(){
        channel.closeConnection();
        EditText ipText = (EditText)findViewById(R.id.ip_text);
        EditText portText = (EditText)findViewById(R.id.port_text);
        ipText.setEnabled(true);
        portText.setEnabled(true);
    }

    private class SocketAsync extends AsyncTask<String, Void, String> {

        ProgressDialog dialog ;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(Developer.this, null, "Inicializando...");
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                pThread = new Thread(channel = new SocketClient(pIP,pPort));
                channel.run();
            } catch (Exception e) {
                Log.e("Socket", "Error in Connecting: " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();
        }
    }
}


