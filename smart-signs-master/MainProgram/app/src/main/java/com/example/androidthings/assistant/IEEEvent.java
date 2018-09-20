package com.example.androidthings.assistant;

import android.app.Activity;
import android.util.Log;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.util.ICalDate;

import static android.content.ContentValues.TAG;


/**
 * Created by brady on 4/10/2018.
 */
//"https://calendar.google.com/calendar/ical/students.rowan.edu_j5sfe68394fdi538tq9ov146d0%40group.calendar.google.com/public/basic.ics"
public class IEEEvent {

    private String CalenderData;
    private String EventTitle;
    private String EventTime;
    private String EventLocation;
    private String EventDescription;
    private static String URL;
    private static Activity activity;

    SimpleDateFormat Formatter = new SimpleDateFormat("EEEE, MMM dd, at hh:mm:ss");


    public static void IEEEvents(Activity act, String x) {
        URL = x;
        activity = act;
    }

    private void EventRetrieve() {
        try {
            CalenderData = downloadUrl();
            ICalendar ical = Biweekly.parse(CalenderData).first();
            VEvent event = ical.getEvents().get(0);

            EventDescription = event.getDescription().getValue();
            EventLocation = event.getLocation().getValue();
            EventTitle = event.getSummary().getValue();
            EventTime = Formatter.format(event.getDateStart().getValue()); //NOT SURE ABOUT THIS
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getEventTitle() {
        return EventTitle;
    }
    private String getEventTime() {
        return EventTime;
    }
    private String getEventLocation(){
        return EventLocation;
    }
    private String getEventDescription(){
        return EventDescription;
    }

    private void DisplayEventListing() {

    }

    private String downloadUrl() throws IOException {
        InputStream is = null;
        // Only display the first 1000 characters of the retrieved
        // web page content.
        int len = 1000;
        try {
            URL url = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            //set back to 15000, 10000
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1)
            {
                byteArrayOutputStream.write(i);
                i = is.read();
            }
            Log.d(TAG, "Download finished; parse");
            String rawdata = byteArrayOutputStream.toString("UTF-8");

            return rawdata;
            // Convert the InputStream into a string
            //String contentAsString = readIt(is, len);
            //return contentAsString;
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}

