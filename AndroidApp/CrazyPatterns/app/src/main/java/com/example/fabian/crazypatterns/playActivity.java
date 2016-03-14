package com.example.fabian.crazypatterns;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.*;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class playActivity extends Activity {

    Thread pThread;
    AsyncSocket pchannel;
    AsyncSocketListener psocketserver;
    SocketClient socketserver;
    boolean listening = true;
    boolean connected = false;
    String nombrePartida="";

    String ReGexAnswer = "A|B";

    ArrayList<String> infoPartidasParaLista = new ArrayList<String>();

    ListView listaPartidas;
    public String getExample (String Regex){
        String xeger = Regex;
        String result = "";

        try{
            Xeger generator = new Xeger(xeger);
            result = generator.generate();
            assert result.matches(xeger);

        }
        catch (Exception e) {
            System.out.println(e);
        }
        return result;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        listaPartidas = (ListView) findViewById(R.id.listaPartidas);

        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, infoPartidasParaLista);
        listaPartidas.setAdapter(adaptador);

        listaPartidas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                final int pos = position;
                new SweetAlertDialog(playActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("¿Are you sure?")
                        .setContentText("You will play this game: " + infoPartidasParaLista.get(position))
                        .setConfirmText("Yes,I'm a master!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                //Solicitar el iniciarJuego
                                nombrePartida = infoPartidasParaLista.get(pos);
                                String command = "jugar#" + nombrePartida + "#";
                                socketserver.sendMessage(command);
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        });

        try{
            //TODO DESCOMENTAR PARA CONECTARSE AL SERVIDOR
            pchannel = new AsyncSocket();
            psocketserver = new AsyncSocketListener();
            pchannel.execute("init");
            psocketserver.execute("init");
            //Toast.makeText(getApplicationContext(), "No Conectado", Toast.LENGTH_SHORT).show();

        }
        catch (Exception e){
            new SweetAlertDialog(playActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText("Oops! Something went wrong")
                    .setContentText("Couldn't establish connection")
                    .setConfirmText("Go, back!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            playActivity.this.finish();
                        }
                    })
                    .setCustomImage(R.drawable.noconnect)
                    .show();
        }


        final Button bActualizar = (Button) findViewById(R.id.bActualizar);
        bActualizar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                nombrePartida="";
                if (connected == true) {
                    socketserver.sendMessage("verp#");
                } else {
                    new SweetAlertDialog(playActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                            .setTitleText("Oops! Something went wrong")
                            .setContentText("Couldn't establish connection. Go back and try again")
                            .setConfirmText("Go, back!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    connected = false;
                                    listening = false;
                                    playActivity.this.finish();
                                }
                            })
                            .setCustomImage(R.drawable.noconnect)
                            .show();

                }
            }
        });

        final Button butonVerificar = (Button) findViewById(R.id.bReady);
        butonVerificar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                double porcenAnswer = 0.0;
                EditText inputRegex = (EditText)findViewById(R.id.inputRegex);
                String textoRegex = inputRegex.getText().toString();
                RatingBar vida = (RatingBar)findViewById(R.id.ratingBar);
                TextView txtPorcentaje = (TextView)findViewById(R.id.txtPorcentaje);

                if(textoRegex.compareTo(ReGexAnswer)==0){
                    porcenAnswer = 100;
                    txtPorcentaje.setText(porcenAnswer + "%");
                    socketserver.sendMessage("gano#" + nombrePartida + "#" + constants._USER+"#");
                    new SweetAlertDialog(playActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Congratulations")
                            .setContentText("You are the boss ")
                            .setConfirmText("Go, back!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    connected=false;
                                    listening = false;
                                    nombrePartida = "";
                                    socketserver.sendMessage("exit");
                                    socketserver.closeConnection();
                                    playActivity.this.finish();
                                }
                            })
                            .show();
                }

                else if (textoRegex != ReGexAnswer){
                    for (int i = 0; i < 17; i ++){
                        if (Pattern.matches(textoRegex, getExample(ReGexAnswer)) == true)
                            porcenAnswer += 100/17;
                    }
                    txtPorcentaje.setText(porcenAnswer + "%");
                    if(vida.getRating() == 1){
                        socketserver.sendMessage("perdio#" + nombrePartida + "#");
                        new SweetAlertDialog(playActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                                .setTitleText("Sorry")
                                .setContentText("You've ran out of lives")
                                .setConfirmText("Go, back!")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        connected=false;
                                        listening = false;
                                        nombrePartida = "";
                                        socketserver.sendMessage("exit");
                                        socketserver.closeConnection();
                                        playActivity.this.finish();
                                    }
                                })
                                .setCustomImage(R.drawable.caratriste)
                                .show();

                    }
                    else{
                        vida.setRating(vida.getRating()-1);
                    }

                }
            }
        });
    }

    private void iniciarJuego(String info){
        Button but1 = (Button)findViewById(R.id.bActualizar);
        LinearLayout lay2 = (LinearLayout) findViewById(R.id.secondScreen);
        LinearLayout lay = (LinearLayout) findViewById(R.id.firstScreen);

        String[] outinfo = info.split("#");

        TextView hint1 = (TextView)findViewById(R.id.txtHint1);
        TextView hint2 = (TextView)findViewById(R.id.txtHint2);
        TextView hint3 = (TextView)findViewById(R.id.txtHint3);
        TextView hint4 = (TextView)findViewById(R.id.txtHint4);
        TextView hint5 = (TextView)findViewById(R.id.txtHint5);

        ReGexAnswer=outinfo[1];
        hint1.setText(outinfo[2]);
        hint2.setText(outinfo[3]);
        hint3.setText(outinfo[4]);
        hint4.setText(outinfo[5]);
        hint5.setText(outinfo[6]);

        lay2.setVisibility(View.VISIBLE);
        lay.setVisibility(View.INVISIBLE);
        but1.setVisibility(View.INVISIBLE);
    }


    private class AsyncSocket extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                listening = true;
                pThread = new Thread(socketserver = new SocketClient(constants._HOST, constants._PORT));

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
    private class AsyncSocketListener extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            while(listening){
                String msg = socketserver.getMessage();
                publishProgress(msg);
            }
            return "";
        }
        @Override
        protected void onProgressUpdate(String... progress) {
            if (listening) {
                try {
                    if (progress[0].compareTo("gotconnected") == 0) {
                        new SweetAlertDialog(playActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Connected")
                                .setContentText("Connection has been established")
                                .show();
                        connected = true;
                    }
                    else if (progress[0].split("#")[0].compareTo("ER") == 0) {
                        iniciarJuego(progress[0]);
                    }
                    else {
                        if(progress[0] != "") {
                            infoPartidasParaLista.clear();
                            ArrayList<String> info_try = new ArrayList<String>();
                            String[] listaDePartidas = progress[0].split("%");

                            List<Map<String, String>> data = new ArrayList<Map<String, String>>();

                            for (int i = 0; i < listaDePartidas.length; i++) {
                                String[] partida = listaDePartidas[i].split("#");
                                infoPartidasParaLista.add(partida[0]);

                                Map<String, String> datum = new HashMap<String, String>(2);
                                datum.put("title", "Game: " + partida[0]);
                                datum.put("date", "Tries: "+ partida[3] + "    |    " +"Date: " +partida[2]);
                                data.add(datum);

                            }
                            SimpleAdapter adapter = new SimpleAdapter(playActivity.this, data,
                                    android.R.layout.simple_list_item_2,
                                    new String[]{"title", "date"},
                                    new int[]{android.R.id.text1,
                                            android.R.id.text2});
                            //ArrayAdapter<String> adaptador = new ArrayAdapter<String>(playActivity.this, android.R.layout.simple_list_item_1, infoPartidasParaLista);
                            listaPartidas.setAdapter(adapter);
                        }
                    }
                } catch (NullPointerException e) {
                    connected = false;
                }
            }

        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }


    @Override
    public void onBackPressed() {
        final SweetAlertDialog dialog = new SweetAlertDialog(playActivity.this, SweetAlertDialog.WARNING_TYPE);

        dialog.setTitleText("Exit?");
        dialog.setContentText("Do you really want to exit?");
        dialog.setConfirmText("Yes!");
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                if((socketserver != null && socketserver.isConnectionAvailable() == true)) {
                    listening = false;
                    connected=false;
                    SystemClock.sleep(800);
                    nombrePartida = "";
                    socketserver.sendMessage("exit");
                    socketserver.closeConnection();
                    dialog.dismiss();
                    playActivity.this.finish();
                }
                else{
                    nombrePartida="";
                    connected=false;
                    listening = false;
                    dialog.dismiss();
                    playActivity.this.finish();
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
