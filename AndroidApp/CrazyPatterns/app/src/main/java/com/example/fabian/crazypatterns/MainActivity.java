package com.example.fabian.crazypatterns;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private soundManager player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        player = new soundManager();


        final Button goButton = (Button) findViewById(R.id.bAccept);
        goButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GridLayout screen2 = (GridLayout)findViewById(R.id.buttonsLayout);
                LinearLayout screen = (LinearLayout)findViewById(R.id.configLayout);
                EditText puerto = (EditText)findViewById(R.id.porttxt);
                EditText ip = (EditText)findViewById(R.id.iptxt);

                String ppuerto=puerto.getText().toString();
                String phost = ip.getText().toString();
                if(phost.length() < 4) {
                    player.playEffect(2);
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Warning")
                            .setContentText("Please enter IP")
                            .show();
                }
                else{
                    if(ppuerto.length() < 1){
                        player.playEffect(3);
                        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Warning")
                                .setContentText("Please enter a valid port")
                                .show();
                    }
                    else{
                        player.playEffect(1);
                        constants._HOST=phost;
                        constants._PORT=Integer.parseInt(ppuerto);
                        constants.writeToFile(constants._HOST + "#" + constants._PORT + "#"+ "User" + "#", getBaseContext());
                        screen2.setVisibility(View.VISIBLE);
                        screen.setVisibility(View.INVISIBLE);

                    }
                }
            }
        });

        final ImageButton createButton = (ImageButton) findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                player.playEffect(1);
                Intent intent = new Intent(MainActivity.this, crearActivity.class);
                startActivity(intent);
            }
        });
        final ImageButton playButton = (ImageButton) findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                player.playEffect(1);
                Intent intent = new Intent(MainActivity.this, playActivity.class);
                startActivity(intent);
            }
        });
        final ImageButton statsButton = (ImageButton) findViewById(R.id.statsButton);
        statsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                player.playEffect(1);
                Intent intent = new Intent(MainActivity.this, statsActivity.class);
                startActivity(intent);
            }
        });
        final ImageButton profileButton = (ImageButton) findViewById(R.id.profileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                player.playEffect(1);
                Intent intent = new Intent(MainActivity.this, profileActivity.class);
                startActivity(intent);
            }
        });
        byte x = 0;
        while (x < 3) {
            x++;
            SystemClock.sleep(300);
        }
        //player.loadSound(0);

        String loadParams = constants.readFromFile(getBaseContext());
        if(loadParams == "NF"){
            player.loadSound(0);
        }
        if(loadParams != "NF"){
            String[] params = loadParams.split("#");
            GridLayout screen2 = (GridLayout)findViewById(R.id.buttonsLayout);
            LinearLayout screen = (LinearLayout)findViewById(R.id.configLayout);

            try{
                constants._HOST=params[0];
                constants._PORT=Integer.parseInt(params[1].toString());
                constants._USER=params[2];
            }
            catch (Exception e){
                constants._HOST="192.168.1.106";
                constants._PORT=9041;
                constants._USER="User";
            }
            //Log.e("IP:",params[0]);
            //Log.e("PORT:",params[1]);
            screen2.setVisibility(View.VISIBLE);
            screen.setVisibility(View.INVISIBLE);
        }
        //constants.writeToFile(constants._HOST + "#" + constants._PORT, getApplicationContext());

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = "Home";
                break;
            case 2:
                Intent intent = new Intent(MainActivity.this, crearActivity.class);
                startActivity(intent);
                break;
            case 3:
                Intent intent2 = new Intent(MainActivity.this, playActivity.class);
                startActivity(intent2);
                break;
            case 4:
                Intent intent3 = new Intent(MainActivity.this, Developer.class);
                startActivity(intent3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }


    //SOUNDS------------------------------------

    public class soundManager {
        private SoundPool player;
        private AudioManager audioManager;

        private boolean plays = false, loaded = false;
        private float  actVolume, maxVolume, volume;
        private int counter;
        private int currentSound = 2;

        private int welcomeSound;
        private int clickSound;
        private int ipSound;
        private int portSound;

        public soundManager(){
            // AudioManager audio settings for adjusting the volume
            audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            volume = actVolume / maxVolume;

            //Hardware buttons setting to adjust the media sound
            MainActivity.this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

            // the counter will help us recognize the stream id of the sound played  now
            counter = 0;

            // Load the sounds
            player = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

            welcomeSound = player.load(MainActivity.this,R.raw.init, 1);
            clickSound = player.load(MainActivity.this,R.raw.beep, 1);
            ipSound = player.load(MainActivity.this,R.raw.ip, 1);
            portSound = player.load(MainActivity.this,R.raw.port, 1);

            player.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    loaded = true;
                    if(currentSound == 0) {
                        soundManager.this.playSound(welcomeSound);
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

        public void loadSound(int x){
            currentSound = x;
        }
        public void playEffect(int snd){
            if(snd == 0) {
                soundManager.this.playSound(welcomeSound);
            }
            else if (snd == 1){
                soundManager.this.playSound(clickSound);
            }
            else if (snd == 2){
                soundManager.this.playSound(ipSound);
            }
            else if (snd == 3){
                soundManager.this.playSound(portSound);
            }
        }
    }

    @Override
    public void onBackPressed() {
        final SweetAlertDialog dialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);

        dialog.setTitleText("Exit?");
        dialog.setContentText("Do you really want to exit?");
        dialog.setConfirmText("Yes!");
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                dialog.dismiss();
                MainActivity.this.finish();

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
        dialog.setCustomImage(R.drawable.exit);
        dialog.show();

    }
}
