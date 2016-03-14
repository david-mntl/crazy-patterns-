package com.example.fabian.crazypatterns;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class statsActivity extends ActionBarActivity {

    ListView listID;
    ListView listAttempts;
    ListView listRegEx;
    ListView listWinner;
    ImageButton btnGeneral;
    ImageButton btnChallenge;
    ArrayList<String> infoID = new ArrayList<String>();
    ArrayList<String> infoRegEx = new ArrayList<String>();
    ArrayList<String> infoWinner = new ArrayList<String>();

    Thread pThread;
    AsyncSocket pchannel;
    AsyncSocketListener psocketserver;
    SocketClient socketserver;
    boolean listening = false;
    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        btnGeneral = (ImageButton) findViewById(R.id.btnGeneral);
        btnChallenge = (ImageButton) findViewById(R.id.btnChallenge);
        btnGeneral.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(connected == true) {
                    socketserver.sendMessage("stats#");
                }
                else{
                    notConnectionMessage();
                }
            }
        });

        for (int i = 1; i <= 8; i++) {
            infoID.add(Integer.toString(i));
        }

        listID = (ListView) findViewById(R.id.listID);
        listRegEx= (ListView) findViewById(R.id.listRegEx);
        listWinner= (ListView) findViewById(R.id.listWinner);
        listAttempts= (ListView) findViewById(R.id.listAttempts);

        listID.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
            }
        });
        listRegEx.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
            }
        });
        listWinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
            }
        });
        listAttempts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
            }
        });

        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, infoID);
        listID.setAdapter(adaptador);
        ArrayAdapter<String> adaptadorA = new ArrayAdapter<String>(statsActivity.this, android.R.layout.simple_list_item_1, infoRegEx);
        listRegEx.setAdapter(adaptadorA);
        ArrayAdapter<String> adaptadorC = new ArrayAdapter<String>(statsActivity.this, android.R.layout.simple_list_item_1, infoWinner);
        listWinner.setAdapter(adaptadorC);




        try {
            pchannel = new AsyncSocket();
            psocketserver = new AsyncSocketListener();
            pchannel.execute("init");
            psocketserver.execute("init");

        } catch (Exception e) {
            notConnectionMessage();
        }
    }

    private void notConnectionMessage(){
        new SweetAlertDialog(statsActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Oops! Something went wrong")
                .setContentText("Couldn't establish connection")
                .setConfirmText("Go, back!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        statsActivity.this.finish();
                    }
                })
                .setCustomImage(R.drawable.noconnect)
                .show();
    }

    private class AsyncSocket extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                pThread = new Thread(socketserver = new SocketClient(constants._HOST, constants._PORT));
                listening = true;

            } catch (Exception e) {
                Log.e("Socket", "Error in Connecting: " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    private class AsyncSocketListener extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            while (true) {
                while(listening) {
                    String msg = socketserver.getMessage();
                    publishProgress(msg);
                }
                return "";
            }
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            try {
                infoRegEx.clear();
                infoWinner.clear();

                String data = progress[0];
                String[] dato = data.split("#");
                if (progress[0].compareTo("gotconnected") == 0) {
                    new SweetAlertDialog(statsActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Connected")
                            .setContentText("Connection has been established")
                            .show();
                    connected = true;
                    //socketserver.sendMessage("stats#");
                }

                if (dato[0].compareTo("regex") == 0) {
                    ArrayList<String> infoReg = new ArrayList<String>();
                    for (int i = 1; i < dato.length; i++) {
                        infoRegEx.add(dato[i]+"");
                        infoReg.add(dato[i]+"");
                    }
                    ArrayAdapter<String> adaptadorA = new ArrayAdapter<String>(statsActivity.this, android.R.layout.simple_list_item_1, infoReg);
                    listRegEx.setAdapter(adaptadorA);
                }
                else if (dato[0].compareTo("winner") == 0) {
                    //TODO
                    for (int i = 1; i < dato.length; i++) {
                        infoWinner.add(dato[i] + "");
                    }
                    ArrayAdapter<String> adaptadorC = new ArrayAdapter<String>(statsActivity.this, android.R.layout.simple_list_item_1, infoWinner);
                    listWinner.setAdapter(adaptadorC);
                }
                else if (dato[0].compareTo("inten") == 0) {
                    ArrayList<String> inf = new ArrayList<String>();
                    for (int i = 1; i < dato.length; i++) {
                        inf.add(dato[i].toString());
                    }
                    ArrayAdapter<String> newAd = new ArrayAdapter<String>(statsActivity.this, android.R.layout.simple_list_item_1, inf);
                    listAttempts.setAdapter(newAd);
                }
            } catch (NullPointerException e) {
                connected = false;

            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }


    @Override
    public void onBackPressed() {
        final SweetAlertDialog dialog = new SweetAlertDialog(statsActivity.this, SweetAlertDialog.WARNING_TYPE);

        dialog.setTitleText("Exit?");
        dialog.setContentText("Do you really want to exit?");
        dialog.setConfirmText("Yes!");
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                if (connected == true) {
                    connected = false;
                    listening = false;
                    socketserver.sendMessage("exit");
                    socketserver.closeConnection();
                    dialog.dismiss();
                    statsActivity.this.finish();
                } else {
                    connected = false;
                    listening = false;
                    dialog.dismiss();
                    statsActivity.this.finish();
                }
            }
        });
        dialog.setCancelText("No!");
        dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.cancel();
            }
        });
        dialog.setCustomImage(R.drawable.noconnect);
        dialog.show();

    }


}