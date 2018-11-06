package com.example.androidthings.assistant;

import android.app.Activity;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by brady on 3/29/2018.
 */

public class mapnavigation {
    private static final String TAG = mapnavigation.class.getSimpleName();
    private static ImageView mImageView;
    private static NavigationPath Path;
    private static int number;
    private TextToSpeech textToSpeech;
    //private android.widget.Button mButtonWidget;

    public static void navigate(Activity activity, int roomnumber){
        activity.setContentView(R.layout.activity_map_navigation);
        activity.getActionBar().hide();

        mImageView = activity.findViewById(R.id.MapLayer);
        Path = (NavigationPath)activity.findViewById(R.id.NavLayer);
        number = roomnumber;

    }
    public void startnavigation()
    {
        ChangeMap(number);
        Path.SetRoomNumber(number);
    }

    public static boolean ChangeMap(int x) {
        Log.i(TAG, "Entered change map, Room # is " + x);

        if(x < 100 || x > 400) {
            Log.i(TAG, "ERROR FLOOR DOES NOT EXIST");
            return false;
        }
        else if(x < 200) {
            Log.i(TAG, "Toggled to Floor 1");
            mImageView.setImageResource(R.drawable.floor1);
            return true;
        }
        else if(x < 300) {
            Log.i(TAG, "Toggled to Floor 2");
            mImageView.setImageResource(R.drawable.floor2);
            return true;
        }
        else if (x < 400) {
            Log.i(TAG, "Toggled to Floor 3");
            mImageView.setImageResource(R.drawable.floor3);
            return true;
        }
        return false;
    }

}

