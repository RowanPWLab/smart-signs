package com.example.androidthings.assistant;

import java.lang.Object;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;  removed 9/25/18
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.things.AndroidThings;

import org.json.JSONException;




public class IdleActivity extends /*AppCompatActivity*/ Activity implements DownloadManager {
    private WeatherSync mWeatherSync;
    private android.widget.Button request;

    boolean connected = false;  //true if connected to internet, else false. Initialized to true


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idle);

        //check to see if we have internet (needed for getting weather)
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getActiveNetworkInfo() != null) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;

        //If program is restarting after losing power or crashing, the internet needs a few seconds to reconnect
        //Adding a delay using a for loop
        // (Not the best design, but the wait function did not work)
        if(!connected) {
            for(int i = 0; i < 1000000000; i++);
        }

        mWeatherSync = new WeatherSync(IdleActivity.this, this);
        final String Glassboro = mWeatherSync.getUrl(39.712801299999995, -75.12203119999998);

        mWeatherSync.startForcedDownload(Glassboro);



        request = findViewById(R.id.requestBtn);

        //User can click button to get to google assistant activity. added 9/25/18
        request.setOnClickListener(new View.OnClickListener() {   //start-stop button
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AssistantActivity.class)); //start idle state activity
            }
        });

    }

    @Override
    public void onDownloadSuccess(String response) {
        // Make sure we run on the UI thread when doing UI operations.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DataAnalysis dataAnalysis = new DataAnalysis(IdleActivity.this, mWeatherSync);
                try {
                    dataAnalysis.generate();
                    String message = "HIGH: " +  Math.round(dataAnalysis.getTemperatureAnalysis().getTempHighMax()) + "\u00B0F";
                    ((TextView) findViewById(R.id.textView4)).setText(message);
                }
                catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DataAnalysis dataAnalysis = new DataAnalysis(IdleActivity.this, mWeatherSync);
                try {
                    dataAnalysis.generate();
                    String message2 = " LOW: " +  Math.round(dataAnalysis.getTemperatureAnalysis().getTempLowMax()) + "\u00B0F";
                    ((TextView) findViewById(R.id.textView3)).setText(message2);
                }
                catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DataAnalysis dataAnalysis = new DataAnalysis(IdleActivity.this, mWeatherSync);
                try {
                    dataAnalysis.generate();
                    String message3 = "Wind Direction: " + dataAnalysis.getWindAnalysis().getDir() + " at " + Math.round(dataAnalysis.getWindAnalysis().getWindHigh()) + dataAnalysis.getWindAnalysis().getUnits();
                    ((TextView) findViewById(R.id.textView5)).setText(message3);
                }
                catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });

    }

    @Override
    public void onDownloadFailed(String response) {
        new AlertDialog.Builder(this)
                .setTitle("Download Failed")
                .setMessage(response)
                .show();
    }

    @Override
    public void onNotDownloaded() {
        new AlertDialog.Builder(this)
                .setTitle("Data not downloaded")
                .show();
    }

}

