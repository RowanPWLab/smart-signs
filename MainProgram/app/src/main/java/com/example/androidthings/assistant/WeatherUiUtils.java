package com.example.androidthings.assistant;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by N on 5/27/2015.
 */

public class WeatherUiUtils {
    private static final String TAG = "weatherdelta::UiUtils";

    public static String getPremiumSummary(Context context, WeatherSync mDataSource) {
        //Use mDataSource
        DataAnalysis dataAnalysis3 = mDataSource.generateAnalysis();
        String premium_summary = "";
        if (dataAnalysis3.getTemperatureAnalysis() != null) {
            try {
                if (dataAnalysis3.getTemperatureAnalysis().isItIsHot() && dataAnalysis3.getHumidityAnalysis().isHumidNow()) {
                    premium_summary += context.getString(R.string.premium_summary_very_hot)+"\n";
                } else if (dataAnalysis3.getTemperatureAnalysis().isItGetsHot() && dataAnalysis3.getHumidityAnalysis().isMoreHumidity()) {
                    premium_summary += context.getString(R.string.premium_summary_warm)+"\n";
                }
                if (dataAnalysis3.getTemperatureAnalysis().isItIsCold()) {
                    premium_summary += context.getString(R.string.premium_summary_cold)+"\n";
                } else if (dataAnalysis3.getTemperatureAnalysis().isItGetsCold()) {
                    premium_summary += context.getString(R.string.premium_summary_chilly)+"\n";
                }
            } catch(Exception e){
                Log.d(TAG, "Temp analysis failed");
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "temperatureAnalysis is null, what is might be?");
        }
        if(dataAnalysis3.getPrecipitationAnalysis() != null) {
            if (dataAnalysis3.getPrecipitationAnalysis().isRaining()) {
                premium_summary += context.getString(R.string.rain_report_now,
                        dataAnalysis3.getPrecipitationAnalysis().getType()) + "\n";
            } else if (dataAnalysis3.getPrecipitationAnalysis().isLikelyToRain()) {
                if(dataAnalysis3.getPrecipitationAnalysis().getTimeToRain() != 1)
                    premium_summary += context.getString(R.string.premium_summary_rain_not_soon,
                            dataAnalysis3.getPrecipitationAnalysis().getType(),
                            dataAnalysis3.getPrecipitationAnalysis().getTimeToRain()) + "\n";
                else
                    premium_summary += context.getString(R.string.premium_summary_rain_soon,
                            dataAnalysis3.getPrecipitationAnalysis().getType())+"\n";
            }
        }
        Date mDate = new Date();
        int hDate = mDate.getHours();
        Calendar sr = new GregorianCalendar();
        long ms = 0;
        try {
            ms = mDataSource.getSunrise();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ms = ms*1000;
        sr.setTimeInMillis(ms);
        int hSun = sr.getTime().getHours();
        if(hDate < hSun && hSun - hDate < 2) {
            premium_summary += context.getString(R.string.premium_summary_sunrise_soon)+"\n";
        } else {
            Calendar ss = new GregorianCalendar();
            try {
                ms = mDataSource.getSunset();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ms = ms*1000;
            ss.setTimeInMillis(ms);
            hSun = ss.getTime().getHours();
            if(hDate < hSun && hSun - hDate < 2)
                premium_summary += context.getString(R.string.premium_summary_sunset_soon)+"\n";
            else {
                //Nighttime
                try {
                    premium_summary += String.format(context.getString(R.string.premium_summary_moon), mDataSource.getMoonPhaseName())+"\n";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return premium_summary;
    }

    public static int getMoonPhase(double completed) {
        if(completed < 0.12)
            return R.string.moon_new;
        if(completed < 0.25)
            return R.string.moon_waxing_crescent;
        if(completed < 0.37)
            return R.string.moon_quarter;
        if(completed < 0.50)
            return R.string.moon_waxing_gibbous;
        if(completed < 0.62)
            return R.string.moon_full;
        if(completed < 0.75)
            return R.string.moon_waning_gibbous;
        if(completed < 0.87)
            return R.string.moon_lastq;
        return R.string.moon_waning_crescent;
    }
}