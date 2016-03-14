package com.example.fabian.crazypatterns;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class constants {
    static String _HOST= "";
    static int _PORT = 0;
    static String _USER = "User";

    public static void writeToFile(String data, Context c) {
        try {
            OutputStreamWriter MyOutputStreamWriter = new OutputStreamWriter(c.openFileOutput("params.txt", Context.MODE_PRIVATE));
            MyOutputStreamWriter.append(data);
            MyOutputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static String readFromFile(Context c) {
        String ret = "";
        try {
            InputStream inputStream = c.openFileInput("params.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString).append("\n");
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            ret= "NF";
        } catch (IOException e) {
            ret= "NF";
        }
        return ret;
    }
}
