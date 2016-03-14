package com.example.fabian.crazypatterns;

import android.app.AlertDialog;
import android.media.AudioManager;
import android.media.Image;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import java.util.regex.*;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class crearActivity extends ActionBarActivity {
    String patron = Pattern.matches("A|G*+J", "GGJ")+"";
    String[] params = {"Enter RegEx game name:","Select Your Dictionary","Enter Regular Expresion ","Enter RegEx Expresion"};
    String[] values = {"","","",""};
    ArrayList<String> diccionarioSeleccionado = new ArrayList<String>();

    Thread pThread;
    AsyncSocket pchannel;
    SocketClient socketserver;
    boolean listening = true;
    boolean connected = false;
    soundManager player;

    EditText mainText ;

    TextView opcion1;
    TextView opcion2;
    TextView opcion3;
    TextView opcion4;
    TextView opcion5;

    ImageButton iButton1;
    ImageButton iButton2;
    ImageButton iButton3;
    ImageButton iButton4;
    ImageButton iButton5;

    LinearLayout boxesLayout;
    LinearLayout optionsLayout;
    LinearLayout labelsLayout;

    //ImageButton[] listaBotonesImagenes = {iButton1,iButton2,iButton3,iButton4,iButton5};
    //TextView[] listaOpcionesTexto = {opcion1,opcion2,opcion3,opcion4,opcion5};

    int option_id = 0;

    public String getExample (String Regex){
        String xeger = Regex;
        String result = "";

        try{
            Xeger generator = new Xeger(xeger);
            result = generator.generate();
            assert result.matches(xeger);

        }
        catch (Exception e) {
            new SweetAlertDialog(crearActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Warning")
                    .setContentText("Syntax Error")
                    .setConfirmText("Retry")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        }
        return result;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear);

        mainText = (EditText) findViewById(R.id.labelTexto);

        boxesLayout = (LinearLayout) findViewById(R.id.linearLayout3);
        optionsLayout = (LinearLayout) findViewById(R.id.layOutOpcion);
        labelsLayout = (LinearLayout) findViewById(R.id.layLabels);
        player = new soundManager();
        opcion1 = (TextView)findViewById(R.id.labelOpcion5);
        opcion2 = (TextView)findViewById(R.id.labelOpcion1);
        opcion3 = (TextView)findViewById(R.id.labelOpcion2);
        opcion4 = (TextView)findViewById(R.id.labelOpcion3);
        opcion5 = (TextView)findViewById(R.id.labelOpcion4);

        iButton1 = (ImageButton)findViewById(R.id.imageButton1);
        iButton2 = (ImageButton)findViewById(R.id.imageButton2);
        iButton3 = (ImageButton)findViewById(R.id.imageButton3);
        iButton4 = (ImageButton)findViewById(R.id.imageButton4);
        iButton5 = (ImageButton)findViewById(R.id.imageButton5);

        diccionarioSeleccionado.add("|");
        diccionarioSeleccionado.add("*");
        diccionarioSeleccionado.add("+");
        diccionarioSeleccionado.add("(");
        diccionarioSeleccionado.add(")");


        final RadioButton rStep1 = (RadioButton) findViewById(R.id.step1);
        final RadioButton rStep2 = (RadioButton) findViewById(R.id.step2);
        final RadioButton rStep3 = (RadioButton) findViewById(R.id.step3);
        final RadioButton rStep4 = (RadioButton) findViewById(R.id.step4);

        boxesLayout.setVisibility(View.INVISIBLE);
        optionsLayout.setVisibility(View.INVISIBLE);
        labelsLayout.setVisibility(View.INVISIBLE);


        try{
            pchannel = new AsyncSocket();
            pchannel.execute("init");
        }
        catch (Exception e){
            new SweetAlertDialog(crearActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText("Oops! Something went wrong")
                    .setContentText("Couldn't establish connection")
                    .setConfirmText("Go, back!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            crearActivity.this.finish();
                        }
                    })
                    .setCustomImage(R.drawable.noconnect)
                    .show();
        }


        final RadioButton[] progress = {rStep1,rStep2,rStep3,rStep4};
        progress[option_id].setChecked(true);

        final ImageButton nextButton = (ImageButton) findViewById(R.id.nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                player.playEffect(0);
                TextView txtLabel = (TextView) findViewById(R.id.instruc1);

                if (option_id < 4) {
                    values[option_id] = mainText.getText().toString();
                    option_id++;
                    mainText.setVisibility(View.VISIBLE);

                    boxesLayout.setVisibility(View.INVISIBLE);
                    optionsLayout.setVisibility(View.INVISIBLE);

                    if (option_id == 1) {
                        progress[option_id].setChecked(true);
                        //values[option_id] = mainText.getText().toString();
                        txtLabel.setText(params[option_id]);
                        mainText.setVisibility(View.INVISIBLE);
                        boxesLayout.setVisibility(View.VISIBLE);
                        mainText.setText("");

                    }
                    if (option_id == 3) {
                        progress[option_id].setChecked(true);
                        opcion1.setVisibility(View.VISIBLE);
                        if (verificar()) {
                            //values[option_id] = mainText.getText().toString();
                            txtLabel.setText(params[option_id]);
                            boxesLayout.setVisibility(View.INVISIBLE);
                            optionsLayout.setVisibility(View.VISIBLE);
                            labelsLayout.setVisibility(View.VISIBLE);
                            TextView[] textViews = {opcion1,opcion2,opcion3,opcion4,opcion5};

                            for(int i = 0; i < 5;i++){

                                String texto = getExample(mainText.getText().toString());

                                if(texto.length() < 9)
                                    textViews[i].setText(texto);
                                else
                                    textViews[i].setText(texto.substring(0, 9));
                            }
                        }
                        else {
                            option_id--;
                            showAlert();
                        }

                    }
                    else{
                        if (option_id< 4)
                        {
                            progress[option_id].setChecked(true);
                            //values[option_id] = mainText.getText().toString();
                            txtLabel.setText(params[option_id]);
                        }
                        else{
                            if(socketserver != null && socketserver.isConnectionAvailable() == true) {
                                String crearMsjAenviar = "crear#" + values[0] + "#" + values[2] + "#FECHA" + "#0#" + opcion1.getText() + "#" + opcion2.getText() + "#" + opcion3.getText() + "#" + opcion4.getText() + "#" + opcion5.getText() + "#";
                                //txtLabel.setText(crearMsjAenviar);
                                socketserver.sendMessage(crearMsjAenviar);
                                new SweetAlertDialog(crearActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Success")
                                        .setContentText("RegEx sent to the server")
                                        .setConfirmText("Ok")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                connected = false;
                                                listening = false;
                                                socketserver.sendMessage("exit");
                                                socketserver.closeConnection();
                                                sDialog.dismissWithAnimation();
                                                crearActivity.this.finish();
                                            }
                                        })
                                        .setCustomImage(R.drawable.noconnect)
                                        .show();
                            }
                            else{
                                new SweetAlertDialog(crearActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                                        .setTitleText("Oops! Something went wrong")
                                        .setContentText("Couldn't establish connection")
                                        .setConfirmText("Go, back!")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                connected = false;
                                                listening = false;
                                                sDialog.dismissWithAnimation();
                                                crearActivity.this.finish();
                                            }
                                        })
                                        .setCustomImage(R.drawable.noconnect)
                                        .show();
                            }
                        }
                    }


                }

            }
        });



        final ImageButton prevButton = (ImageButton) findViewById(R.id.prevButton);

        prevButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                player.playEffect(0);
                TextView txtLabel = (TextView) findViewById(R.id.instruc1);
                if (option_id > 0) {
                    if(option_id <= 4) {
                        progress[option_id].setChecked(false);
                    }
                    option_id--;

                    optionsLayout.setVisibility(View.INVISIBLE);


                    if (option_id ==1)
                    {
                        values[option_id] = txtLabel.getText().toString();
                        txtLabel.setText(params[option_id]);

                        mainText.setVisibility(View.INVISIBLE);

                        boxesLayout.setVisibility(View.VISIBLE);


                    }
                    else
                    {
                        values[option_id] = txtLabel.getText().toString();

                        txtLabel.setText(params[option_id]);
                        mainText.setVisibility(View.VISIBLE);

                        boxesLayout.setVisibility(View.INVISIBLE);


                    }

                }
            }
        });
    }


    /**
     * Verifica que lo que este escrito en el cuadro de texto sean letras del diccionario
     * @param
     */
    public boolean verificar()
    {
        String[]textoValidar =(String[]) ((EditText) mainText).getText().toString().split("(?!^)");

        boolean inDictionary= false;

        for(String item: textoValidar)
        {
            inDictionary= false;
            for(String item2 : diccionarioSeleccionado)
            {
                if(item.compareTo(item2) == 0)
                {
                    inDictionary = true;
                    break;
                }
            }

            if (inDictionary == false)
            {
                return false;
            }
        }

        for(int i=0 ;i < textoValidar.length-1;i++)
        {
            if (textoValidar[i].compareTo("*")==0 & textoValidar[i+1].compareTo("+")==0)
            {
                return false;
            }
            if (textoValidar[i].compareTo("+")==0 & textoValidar[i+1].compareTo("*")==0)
            {
                return false;
            }
            if(textoValidar[i].compareTo(textoValidar[i+1])==0 && textoValidar[i].compareTo("*")==0)
            {
                return false;
            }
            if(textoValidar[i].compareTo(textoValidar[i+1])==0 && textoValidar[i].compareTo("+")==0)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Ingresa en un ArrayList los items seleccionados para el diccionario
     * @param pSeleccionado: items Seleccionados
     */
    public void selectItem(View pSeleccionado)
    {
        boolean seleccionado = ((CheckBox) pSeleccionado).isChecked();

        if(seleccionado)
            diccionarioSeleccionado.add((String) ((CheckBox) pSeleccionado).getText());

        else
            diccionarioSeleccionado.remove((String) ((CheckBox) pSeleccionado).getText());
    }

    /**
     * Despliega el error "Not in Dictionary"
     */
    public void showAlert()
    {
        AlertDialog.Builder error1 = new AlertDialog.Builder(this);
        error1.setMessage("RegEx not in Dictionary!").create();
        error1.show();
    }


    public void changeOption1(View view){
        player.playEffect(1);
        String texto = getExample(mainText.getText().toString());
        if(texto.length() < 9)
            opcion1.setText(texto);
        else
            opcion1.setText(texto.substring(0,9));
    }
    public void changeOption2(View view){
        player.playEffect(1);
        String texto = getExample(mainText.getText().toString());
        if(texto.length() < 9)
            opcion2.setText(texto);
        else
            opcion2.setText(texto.substring(0,9));
    }
    public void changeOption3(View view){
        player.playEffect(1);
        String texto = getExample(mainText.getText().toString());
        if(texto.length() < 9)
            opcion3.setText(texto);
        else
            opcion3.setText(texto.substring(0,9));
    }
    public void changeOption4(View view){
        player.playEffect(1);
        String texto = getExample(mainText.getText().toString());
        if(texto.length() < 9)
            opcion4.setText(texto);
        else
            opcion4.setText(texto.substring(0,9));
    }
    public void changeOption5(View view){
        player.playEffect(1);
        String texto = getExample(mainText.getText().toString());
        if(texto.length() < 9)
            opcion5.setText(texto);
        else
            opcion5.setText(texto.substring(0,9));
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


    @Override
    public void onBackPressed() {
        final SweetAlertDialog dialog = new SweetAlertDialog(crearActivity.this, SweetAlertDialog.WARNING_TYPE);

        dialog.setTitleText("Exit?");
        dialog.setContentText("Do you really want to exit?");
        dialog.setConfirmText("Yes!");
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                if (socketserver != null && socketserver.isConnectionAvailable() == true) {
                    connected = false;
                    listening = false;
                    socketserver.sendMessage("exit");
                    socketserver.closeConnection();
                    dialog.dismiss();
                    crearActivity.this.finish();
                } else {
                    connected = false;
                    listening = false;
                    dialog.dismiss();
                    crearActivity.this.finish();
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


    //SOUNDS------------------------------------

    public class soundManager {
        private SoundPool player;
        private AudioManager audioManager;

        private boolean plays = false, loaded = false;
        private float  actVolume, maxVolume, volume;
        private int counter;
        private int currentSound = 0;

        private int deleteSound;
        private int clickSound;

        public soundManager(){
            // AudioManager audio settings for adjusting the volume
            audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            volume = actVolume / maxVolume;

            //Hardware buttons setting to adjust the media sound
            crearActivity.this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

            // the counter will help us recognize the stream id of the sound played  now
            counter = 0;

            // Load the sounds
            player = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

            clickSound = player.load(crearActivity.this,R.raw.beep, 1);
            deleteSound = player.load(crearActivity.this,R.raw.delete, 1);

            player.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    loaded = true;
                    if(currentSound == 2) {
                        soundManager.this.playSound(clickSound);
                    }
                }
            });


        }
        public void playSound(int soundID) {
            if(loaded == true) {
                player.play(soundID, volume, volume, 1, 0, 1f);
                counter = counter++;
            }
        }
        public void playEffect(int snd){
            if(snd == 0) {
                soundManager.this.playSound(clickSound);
            }
            else if (snd == 1){
                soundManager.this.playSound(deleteSound);
            }
        }
    }
}
