package com.example.darkskyandroid;

import java.lang.Object;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.things.AndroidThings;

import org.json.JSONException;




public class MainActivity extends AppCompatActivity implements DownloadManager {
    private WeatherSync mWeatherSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWeatherSync = new WeatherSync(MainActivity.this, this);
        final String Glassboro = mWeatherSync.getUrl(39.712801299999995, -75.12203119999998);

        mWeatherSync.startForcedDownload(Glassboro);

    }

    @Override
    public void onDownloadSuccess(String response) {
        // Make sure we run on the UI thread when doing UI operations.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DataAnalysis dataAnalysis = new DataAnalysis(MainActivity.this, mWeatherSync);
                try {
                    dataAnalysis.generate();
                    String message = "HIGH: " +  dataAnalysis.getTemperatureAnalysis().getTempHighMax() + "\u00B0F";
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
                DataAnalysis dataAnalysis = new DataAnalysis(MainActivity.this, mWeatherSync);
                try {
                    dataAnalysis.generate();
                    String message2 = " LOW: " +  dataAnalysis.getTemperatureAnalysis().getTempLowMax() + "\u00B0F";
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
                DataAnalysis dataAnalysis = new DataAnalysis(MainActivity.this, mWeatherSync);
                try {
                    dataAnalysis.generate();
                    String message3 = "Wind Direction: " + dataAnalysis.getWindAnalysis().getDir() + " at " + dataAnalysis.getWindAnalysis().getWindHigh() + dataAnalysis.getWindAnalysis().getUnits();
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

