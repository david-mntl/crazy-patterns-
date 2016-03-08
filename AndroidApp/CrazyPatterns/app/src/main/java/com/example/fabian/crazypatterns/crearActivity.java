package com.example.fabian.crazypatterns;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class crearActivity extends ActionBarActivity {

    String[] params = {"Enter RegEx game name:","Enter RegEx difficulty level","Enter RegEx Description","Enter Regular Expression"};
    String[] values = {"","","",""};

    int option_id = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear);

        final RadioButton rStep1 = (RadioButton) findViewById(R.id.step1);
        final RadioButton rStep2 = (RadioButton) findViewById(R.id.step2);
        final RadioButton rStep3 = (RadioButton) findViewById(R.id.step3);
        final RadioButton rStep4 = (RadioButton) findViewById(R.id.step4);
        final RadioButton[] progress = {rStep1,rStep2,rStep3,rStep4};

        final ImageButton nextButton = (ImageButton) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView txtLabel = (TextView)findViewById(R.id.instruc1);
                if(option_id<3){
                    values[option_id] = txtLabel.getText().toString();
                    progress[option_id].setChecked(true);
                    option_id++;
                    txtLabel.setText(params[option_id]);

                }
            }
        });

        final ImageButton prevButton = (ImageButton) findViewById(R.id.prevButton);
        prevButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView txtLabel = (TextView)findViewById(R.id.instruc1);
                if(option_id>0){
                    option_id--;
                    values[option_id] = txtLabel.getText().toString();
                    progress[option_id].setChecked(false);
                    txtLabel.setText(params[option_id]);

                }
            }
        });
    }
}
