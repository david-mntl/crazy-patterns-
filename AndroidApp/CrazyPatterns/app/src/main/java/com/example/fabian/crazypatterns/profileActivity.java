package com.example.fabian.crazypatterns;

import android.app.Application;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class profileActivity extends ActionBarActivity {

    EditText portEntry;
    EditText ipEntry;
    EditText nickEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        portEntry = (EditText)findViewById(R.id.portEntry);
        ipEntry = (EditText)findViewById(R.id.ipEntry);
        nickEntry = (EditText)findViewById(R.id.nickEntry);

        String loadParams = constants.readFromFile(getBaseContext());
        if(loadParams != "") {
            String[] params = loadParams.split("#");
            constants._HOST=params[0];
            constants._PORT=Integer.parseInt(params[1].toString());
        }
        ipEntry.setText(constants._HOST);
        portEntry.setText(constants._PORT+"");
        nickEntry.setText(constants._USER);
        Log.e("-->", constants._HOST);
        Log.e("-->", constants._PORT+"");
    }

    public void doneActivity(View view){
        constants._HOST = ipEntry.getText().toString();
        constants._PORT = Integer.parseInt(portEntry.getText().toString());
        constants._USER = nickEntry.getText().toString();
        constants.writeToFile(constants._HOST + "#" + constants._PORT+"#"+constants._USER+"#", getBaseContext());
        this.finish();
    }
}