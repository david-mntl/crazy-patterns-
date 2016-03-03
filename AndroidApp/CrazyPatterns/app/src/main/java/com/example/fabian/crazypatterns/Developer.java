package com.example.fabian.crazypatterns;

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
    AsyncSocket pchannel;
    boolean listening = false;
    String pIP = "";
    int pPort = 0;
    String currentReceiveData;

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
                        pchannel = new AsyncSocket();
                        pchannel.execute("init");
                        msgText.requestFocus();
                    }
                }



            }
        });

        final Button resetButton = (Button) findViewById(R.id.bReset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (pchannel != null) {
                    resetConnection();
                } else {
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
                if (pchannel != null) {
                    EditText msgText = (EditText) findViewById(R.id.input_text);
                    EditText consoleTxt = (EditText) findViewById(R.id.console_text);

                    String sendTxt = msgText.getText().toString();

                    pchannel.sendMessage(sendTxt);
                    String local = pchannel.getMessage();

                    msgText.setText("");
                    consoleTxt.setText(consoleTxt.getText().toString() + '\n' + local);
                }
                else {
                    new SweetAlertDialog(Developer.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("No connection. Can't send message")
                            .show();
                }
            }
        });
    }

    private void resetConnection(){
        listening = false;
        pchannel.sendMessage("exit");
        EditText ipText = (EditText)findViewById(R.id.ip_text);
        EditText portText = (EditText)findViewById(R.id.port_text);
        EditText consoleText = (EditText)findViewById(R.id.console_text);
        ipText.setEnabled(true);
        portText.setEnabled(true);
        consoleText.setText("");
    }

    private class AsyncSocket extends AsyncTask<String, Void, String> {

        //ProgressDialog dialog ;
        SocketClient channel;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //dialog = ProgressDialog.show(Developer.this, null, "Inicializando...");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                pThread = new Thread(channel = new SocketClient(pIP, pPort));
                //channel.run();
            } catch (Exception e) {
                Log.e("Socket", "Error in Connecting: " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //dialog.dismiss();
        }

        String getMessage(){
            new Thread(new Runnable() {
                public void run() {
                    try {
                        currentReceiveData = channel.getMessage();
                    }
                    catch (Exception e) {
                        currentReceiveData = e.toString();
                    }
                }
            }).start();
            return currentReceiveData;
        }

        void sendMessage(String msg){
            channel.sendMessage(msg);
        }
    }
}


