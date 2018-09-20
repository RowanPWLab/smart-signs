package com.example.androidthings.assistant;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by brady on 3/29/2018.
 */

public class NavigationPath extends View {
    //Line and text colors
    private int LineCol;
    private int RoomNumber;
    private int EndCol;
    //paint for drawing custom view
    private Paint LinePaint;
    private Paint CirclePaint;
    private Paint EndCirclePaint;
    float[] Line = new float[]{0,0,0,0, 0,0,0,0};

    private int SignStartx1 = 515;
    private int SignStarty1 = 574;
    private int SignStartx2 = 661;
    private int SignStarty2 = 628;
    private int SignStartx3 = 549;
    private int SignStarty3 = 430;

    private int Endx1;
    private int Endy1;

    private boolean leave = false;





    public NavigationPath(Context context, AttributeSet attrs){
        super(context, attrs);
        LinePaint = new Paint();
        CirclePaint = new Paint();
        EndCirclePaint = new Paint();

        /*mButtonWidgetNav.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
               leave = true;
            }
        });*/
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,R.styleable.NavigationPath,0,0);

        try {
            LineCol = a.getInteger(R.styleable.NavigationPath_PathColor, 0);//0 is default
            RoomNumber = a.getInteger(R.styleable.NavigationPath_RoomNumber, 0);
            EndCol = a.getInteger(R.styleable.NavigationPath_EndColor, 0);
        } finally {
            a.recycle();
        }
    }
    protected void onDraw(Canvas canvas) {
        LinePaint.setStyle(Paint.Style.FILL);
        LinePaint.setAntiAlias(true);
        LinePaint.setColor(LineCol);
        LinePaint.setStrokeWidth(3);

        CirclePaint.setStyle(Paint.Style.FILL);
        CirclePaint.setAntiAlias(true);
        CirclePaint.setColor(LineCol);

        EndCirclePaint.setStyle(Paint.Style.FILL);
        EndCirclePaint.setAntiAlias(true);
        EndCirclePaint.setColor(EndCol);

        DrawCommand(RoomNumber);
        canvas.drawLines(Line, LinePaint);
        canvas.drawCircle(SignStartx1,SignStarty1,5,CirclePaint);
        canvas.drawCircle(Endx1,Endy1,5,EndCirclePaint);
    }
    public void SetRoomNumber(int x) {
        RoomNumber = x;
        invalidate();
        requestLayout();
        Log.i("Nav","Leaving");
    }

    //Ridiculously inefficient since this would need to be redone for any movement of the sign, and any new buildings.
    private void DrawCommand(int RoomNumber){
        switch(RoomNumber) {
            //Floor 1
            case 100:
                Line = new float[]{SignStartx1,SignStarty1,580,574, 580,574,580,640, 580,640,650,640};
                Endx1 = 650;
                Endy1 = 640;
                //"Commons Area";
                break;
            case 105:
                Line = new float[]{SignStartx1,SignStarty1,580,574, 580,574,580,515, 580,515,550,515};
                Endx1 = 550;
                Endy1 = 515;
                //"Men's Bathroom";
                break;
            case 106:
                Line = new float[]{SignStartx1,SignStarty1,580,574, 580,574,580,490, 580,490,550,490};
                Endx1 = 550;
                Endy1 = 490;
                //"Woman's Bathroom";
                break;
            case 107:
                Line = new float[]{SignStartx1,SignStarty1,580,574, 580,574,580,430, 580,430,530,430};
                Endx1 = 530;
                Endy1 = 430;
                break;
            //"Classroom";
            case 116:
                Line = new float[]{SignStartx1,SignStarty1,580,574, 580,574,580,275, 580,275,610,275};
                Endx1 = 610;
                Endy1 = 275;
                //"Automotive shop";
                break;
            case 117:
                Line = new float[]{SignStartx1,SignStarty1,580,574, 580,574,580,319, 580,319,622,319};
                Endx1 = 622;
                Endy1 = 319;
                //"General Engineering Room";
                break;
            case 119:
                Line = new float[]{SignStartx1,SignStarty1,580,574, 580,574,580,440, 580,440,622,440};
                Endx1 = 622;
                Endy1 = 440;
                //"Civil Lab";
                break;
            case 118:
                Line = new float[]{SignStartx1,SignStarty1,580,574, 580,574,580,469, 580,469,622,469};
                Endx1 = 622;
                Endy1 = 469;
                //"Civil Lab";
                break;
            case 122:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,458,567, 458,567,467,550};
                Endx1 = 467;
                Endy1 = 550;
                //"Collab Room";
                break;
            case 123:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,440,558, 440,558,446,539};
                Endx1 = 446;
                Endy1 = 539;
                //"Collab Room";
                break;
            case 124:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,416,545, 416,545,427,525};
                Endx1 = 427;
                Endy1 = 525;
                //"Collab Room";
                break;
            case 125:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,393,533, 393,533,403,513};
                Endx1 = 403;
                Endy1 = 513;
                //"Collab Room";
                break;
            case 126:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,372,521, 372,521,381,502};
                Endx1 = 381;
                Endy1 = 502;
                //"Collab Room";
                break;
            case 127:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,351,510, 351,510,359,492};
                Endx1 = 359;
                Endy1 = 492;
                //"Collab Room";
                break;
            case 128:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,328,498, 328,498,337,482};
                Endx1 = 337;
                Endy1 = 482;
                //"Collab Room";
                break;
            case 144:
                Line = new float[]{SignStartx1,SignStarty1,439,595, 439,595,426,588};
                Endx1 = 426;
                Endy1 = 588;
                //"Collab Room";
                break;
            case 145:
                Line = new float[]{SignStartx1,SignStarty1,436,610, 436,610,416,601};
                Endx1 = 416;
                Endy1 = 601;
                //"Collab Room";
                break;
            case 146:
                Line = new float[]{SignStartx1,SignStarty1,436,610, 436,610,425,627};
                Endx1 = 425;
                Endy1 = 627;
                //"Collab Room";
                break;
            case 147:
                Line = new float[]{SignStartx1,SignStarty1,461,617, 462,617,450,636};
                Endx1 = 550;
                Endy1 = 636;
                //"Collab Room";
                break;
            case 148:
                Line = new float[]{SignStartx1,SignStarty1,483,627, 483,627,479,646}; //check
                Endx1 = 479;
                Endy1 = 646;
                //"Collab Room";
                break;
            case 129:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,307,487, 308,487,315,470};
                Endx1 = 315;
                Endy1 = 470;
                //"Clinic Clerical Waiting";
                break;
            case 130:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,260,462, 260,462,271,445};
                Endx1 = 271;
                Endy1 = 445;
                //"Clinics Faculty Office";
                break;
            case 131:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,240,451, 240,451,245,433};
                Endx1 = 245;
                Endy1 = 433;
                //"Clinics Faculty Office";
                break;
            case 132:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,217,439, 217,439,226,420};
                Endx1 = 226;
                Endy1 = 420;
                //"Clinics Faculty Office";
                break;
            case 133:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,195,427, 195,427,202,409};
                Endx1 = 202;
                Endy1 = 409;
                //"Clinics Faculty Office";
                break;
            case 134:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,172,415, 172,415,175,395};
                Endx1 = 175;
                Endy1 = 395;
                //"Clinics Chair Office";
                break;
            case 140:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,240,451, 240,451,221,490};
                Endx1 = 221;
                Endy1 = 490;
                //"Project Lab";
                break;
            case 141:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,279,472, 279,472,260,506};
                Endx1 = 260;
                Endy1 = 506;
                //"Project Lab";
                break;
            case 143:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,393,533, 393,533,375,571};
                Endx1 = 375;
                Endy1 = 571;
                //"Clinics Faculty Office";
                break;

            //Floor 2
            case 205:
                //"Men's Bathroom";
            case 206:
                //"Woman's Bathroom";
            case 207:
                //"BME Research Lab";
            case 208:
                //"BME Research Lab";
            case 209:
                //"Tissue Culture Suite";
            case 210:
                //"Shared Instrument Lab";
            case 215:
                //"Imaging";
            case 217:
                //"Prep Room";
            case 218:
                //"BME Tech";
            case 219:
                //"BME Teaching Lab";
            case 220:
                //"BME Teaching Lab";
            case 221:
                //"BME Teaching Lab";
            case 223:
                //"Collab room";
            case 224:
                //"Collab room";
                //Faculty Offices: Make easy way to implement names and navigate here by name?
            case 225:
                //"Faculty Office";
            case 226:
                //"Faculty Office";
            case 227:
                //"Faculty Office";
            case 228:
                //"Faculty Office";
            case 229:
                //"Faculty Office";
            case 230:
                //"Faculty Office";
            case 231:
                //"Faculty Office";
            case 232:
                //"Faculty Office";
            case 233:
                //"Faculty Office";
            case 234:
                //"Faculty Office";
            case 235:
                //"Faculty Office";
            case 236:
                //"Faculty Office";
            case 237:
                //"Faculty Office";
                //End of Faculty Offices
            case 238:
                //"Clinics Conference Room";
            case 240:
                //"Project Lab";
            case 241:
                //"Project Lab";
            case 243:
                //"Project Lab";

                //Floor 3
            case 305:
                //"Men's Bathroom";
            case 306:
                //"Woman's Bathroom";
            case 307:
                //"ECE Research Lab";
            case 308:
                //"ECE Research Lab";
            case 309:
                //"ECE Research Lab";
            case 312:
                //"Pick-and-Place Room";
            case 317:
                //"Tech Office";
            case 319:
                //"Classroom";
            case 320:
                //"Classroom";
            case 321:
                //"Classroom";
                //Faculty ECE
            case 323:
                //"Faculty Office";
            case 324:
                //"Faculty Office";
            case 325:
                //"Faculty Office";
            case 326:
                //"Faculty Office";
            case 327:
                //"Faculty Office";
            case 328:
                //"Faculty Office";
            case 329:
                //"Faculty Office";
            case 330:
                //"Faculty Office";
            case 331:
                //"Faculty Office";
            case 332:
                //"Faculty Office";
            case 333:
                //"Faculty Office";
            case 334:
                //"Faculty Office";
            case 335:
                //"Faculty Office";
            case 336:
                //"Faculty Office";
            case 337:
                //"Faculty Office";
                //Faculty Offices end
            case 338:
                //"ECE Teaching";
            case 339:
                //"ECE Teaching";
            case 340:
                //"ECE Teaching";
            case 341:
                //"ECE Teaching";
            case 346:
                //"ECE Office";
            case 350:
                //"COE Dean's Office Waiting room";
            case 352:
                //"COE Dean's Office";

        }
    }

    /**
     * Created by N on 7/26/13.
     */
    public static class Conversions {
        public static Double getCelcius(Double F) {
            double C = F-32;
            return (C*5)/9;
        }
        public static double getFahrenheit(double C) {
            return (C*9/5)+32;
        }

        public static double getCm(double in) {
            return (float) (in*2.54);
        }

        public static Double getKPH(Double mph) {
            return (double) (mph*1.609344);
        }

        public static Double getInHG(double millibars) {
            return (double) (millibars*0.0295299833);
        }

        public static Double getCmHG(double millibars) {
            return millibars*0.0750061561302644;
        }

        public static Double getKm(Double miles) {
            return miles*1.60934;
        }

        private Conversions() {
        }
    }

    /**
     * Created by Nick on 10/21/2015.
     * This is a new class which should help deal with some of the intricacies of
     * unique data types. In addition to supporting a normal weather card, this will also
     * support premium summaries and alerts out of the box with custom conditions for each of those.
     */
    public static class DataAnalysis {
        private static final String TAG = DataAnalysis.class.getSimpleName();
        private static final boolean DEBUG = false;

        private SettingsManager sm;
        private WeatherSync weather;
        private Context mContext;
        private String address;

        private WeatherAlert[] weatherAlerts;
        private TemperatureAnalysis temperatureAnalysis;
        private RainAnalysis precipitationAnalysis;
        private RainIntensityAnalysis precipitationIntensityAnalysis;
        private HumidityAnalysis humidityAnalysis;
        private WindAnalysis windAnalysis;
        private CloudAnalysis cloudAnalysis;
        private DewPointAnalysis dewPointAnalysis;
        private OzoneAnalysis ozoneAnalysis;
        private VisibilityAnalysis visibilityAnalysis;
        private BarometerAnalysis barometerAnalysis;

        public DataAnalysis(Context context, WeatherSync weatherSync) {
            mContext = context;
            weather = weatherSync;
            sm = new SettingsManager(context);
            address = mContext.getString(R.string.based_on_coordinates, weather.getLatitude(), weather.getLongitude());
        }

        public DataAnalysis generate() throws JSONException {
            long currentTime = weather.getCurrentTime();
            sm.setLong(R.string.LAST_CHECKED_TIME, currentTime);

            // If we have any alerts let's put them in
            if (weather.hasAlerts()) {
                weatherAlerts = new WeatherAlert[weather.getAlerts().size()];
                for(int i = 0; i < weather.getAlerts().size(); i++) {
                    WeatherAlert alert = weather.getAlerts().get(i);
                    weatherAlerts[i] = alert;
                }
            }

            // Now let's get the most recent time
            Date d = new Date();
            d.setTime(currentTime);
            String t = new SimpleDateFormat("HH:mm").format(d);

            // Let's determine the icon
    //        int icon = WeatherUiUtils.getIconDrawable(weather.getCurrentIcon());

            // Let's grab the daily summary
            if(!weather.getDailySummary().isEmpty()) {
                /*
                dataSet.put(mContext.getString(R.string.SUMMARY),
                        new WeatherCard(
                                mContext.getString(R.string.title_summary), mContext.getString(R.string.summary_as_of, t),
                                weather.getDailySummary()
                        )
                                .setShowTable(false)
                                .setIcon(getDrawable(icon,mContext))
                );
                */
            }

            try {
                temperatureAnalysis = new TemperatureAnalysis(weather);
                precipitationAnalysis = new RainAnalysis(weather, temperatureAnalysis);
            } catch(Exception e) {
                Log.d(TAG, "An error occurred " + e.getMessage());
                e.printStackTrace();
            }
            try {
                precipitationIntensityAnalysis = new RainIntensityAnalysis(weather);
            } catch (Exception e) {
                Log.d(TAG, "An error occurred " + e.getMessage());
                e.printStackTrace();
            }

            try {
                windAnalysis = new WindAnalysis(weather);
            } catch(Exception ignored) {}

            try {
                barometerAnalysis = new BarometerAnalysis(weather);
            } catch(Exception ignored) {}

            try {
                cloudAnalysis = new CloudAnalysis(weather);
            } catch(Exception ignored) {}

            try {
                visibilityAnalysis = new VisibilityAnalysis(weather);
            } catch(Exception ignored) {}

            try {
                dewPointAnalysis = new DewPointAnalysis(weather);
            } catch(Exception ignored) {}

            try {
                ozoneAnalysis = new OzoneAnalysis(weather);
            } catch(Exception ignored) {}

            try {
                humidityAnalysis = new HumidityAnalysis(weather);
            } catch(Exception ignored) {}

            analyzeGraph(weather);
            if (DEBUG) {
                Log.d(TAG, "Metric mode is " + usingMetric());
                Log.d(TAG, "Just generated");
            }
            return this;
        }

        public boolean usingMetric() {
            return sm.getBoolean(R.string.METRIC);
        }

        private Drawable getDrawable(int dailyIcon, Context cn) {
            try {
                return cn.getResources().getDrawable(dailyIcon);
            } catch(Exception e) {
                return null;
            }
        }

        public class WeatherDatum {
            private int color;
            private Double value;
            public WeatherDatum() {
                this.color = Color.GREEN;
                this.value = Double.valueOf(-1);
            }
            public WeatherDatum(int color, Double value) {
                this.color = color;
                this.value = value;
            }

            public int getColor() {
                return color;
            }

            public Double getValue() {
                return value;
            }
            public String getString() {
                return ""+value;
            }
        }

        public class WeatherAnalysis {
            public ArrayList<WeatherDatum> table;
            public String h1;
            public String h2;
            public String h3;

            public WeatherAnalysis() {
                table = new ArrayList<WeatherDatum>();
            }
            public ArrayList<Double> getValueArray() {
                ArrayList<Double> temp = new ArrayList<Double>();
                for(WeatherDatum i: table) {
                    temp.add(i.getValue().doubleValue());
                }
                return temp;
            }
            public ArrayList<Integer> getColorArray() {
                ArrayList<Integer> temp = new ArrayList<Integer>();
                for(WeatherDatum i: table) {
                    temp.add(i.getColor());
                }
                return temp;
            }
        }

        public class TemperatureAnalysis extends WeatherAnalysis {
            private Double tempHighMax;
            private Double tempLowMax;
            private Double tempMax;
            private double LOW_THRESHOLD = 45;
            private double HIGH_THRESHOLD = 88;

            private boolean tempLow = false;
            private boolean tempHigh = false;
            private boolean itIsCold = false;
            private boolean itIsHot = false;
            private boolean itGetsCold = false;
            private boolean itGetsHot = false;
            private TemperatureAnalysis(WeatherSync w) {
                super();
                //THRESHOLDS
                LOW_THRESHOLD = Float.parseFloat(sm.getString(R.string.TEMP_LOW_THRESHOLD, "45"));
                HIGH_THRESHOLD = Float.parseFloat(sm.getString(R.string.TEMP_HIGH_THRESHOLD, "88"));
                if(LOW_THRESHOLD > HIGH_THRESHOLD) {
                    //Rearrange settings
                    sm.setString(mContext.getString(R.string.TEMP_HIGH_THRESHOLD), ""+LOW_THRESHOLD);
                    sm.setString(mContext.getString(R.string.TEMP_LOW_THRESHOLD), ""+HIGH_THRESHOLD);
                }

                //If you're using metric, we need to temporarily convert these to imperial to check
                if(usingMetric()) {
                    LOW_THRESHOLD = Conversions.getFahrenheit(LOW_THRESHOLD);
                    HIGH_THRESHOLD = Conversions.getFahrenheit(HIGH_THRESHOLD);
                }

                try {
                    Double s = w.getAdjustedTemperatures().get(0);
                    if(s < LOW_THRESHOLD)
                        itIsCold = true;
                    else if(s > HIGH_THRESHOLD)
                        itIsHot = true;
                    tempHighMax = s;
                    tempLowMax = s;
                    int high = 0;
                    int low = 0;
                    int c;
                    ArrayList<Integer> deltacolor = new ArrayList<Integer>(8);
                    for(int i=0;i<w.getAdjustedTemperatures().size();i++) {
                        //90 is a settings
                        if(w.getAdjustedTemperatures().get(i) > HIGH_THRESHOLD) {
                            high++;
                            c = Color.RED;
                            if(tempHighMax < w.getAdjustedTemperatures().get(i))
                                tempHighMax = w.getAdjustedTemperatures().get(i);
                        }
                        else if(w.getAdjustedTemperatures().get(i) - s > 6) {
                            high++;
                            c = Color.YELLOW;
                            if(tempHighMax < w.getAdjustedTemperatures().get(i))
                                tempHighMax = w.getAdjustedTemperatures().get(i);
                        }
                        //blue settings
                        else if(w.getAdjustedTemperatures().get(i) - s < -6 ||
                                w.getAdjustedTemperatures().get(i) < LOW_THRESHOLD) {
                            low++;
                            c = Color.BLUE;
                            if(tempLowMax > w.getAdjustedTemperatures().get(i))
                                tempLowMax = w.getAdjustedTemperatures().get(i);
                        } else
                            c = Color.GREEN;
                        if(usingMetric())
                            table.add(new WeatherDatum(c, (Conversions.getCelcius(w.getAdjustedTemperatures().get(i)))));
                        else
                            table.add(new WeatherDatum(c, w.getAdjustedTemperatures().get(i)));
                        deltacolor.add(c);

                    }
                    if(high > low) {
                        tempHigh = true;
                        tempLow = false;
                        tempMax = tempHighMax;
                    } else if(low > high) {
                        tempHigh = false;
                        tempLow = true;
                        tempMax = tempLowMax;
                    } else {
                        tempHigh = false;
                        tempLow = false;
                        if((w.getAdjustedTemperatures().get(0) - tempLowMax) > (tempHighMax - w.getAdjustedTemperatures().get(0))) {
                            tempMax = tempLowMax;
                        } else {
                            tempMax = tempHighMax;
                        }
                    }

                    itGetsHot = tempHighMax > HIGH_THRESHOLD;
                    itGetsCold = tempLowMax < LOW_THRESHOLD;
                    if(usingMetric()) {
                        tempMax = Conversions.getCelcius(tempMax);
                        tempHighMax = Conversions.getCelcius(tempHighMax);
                        tempLowMax = Conversions.getCelcius(tempLowMax);
                    }

                    h1 = mContext.getString(R.string.temperature_title);
                    h2 = mContext.getResources().getString(R.string.adjusted_temperature);
                    String timeMod = "will";
                    if(itIsCold || itIsHot)
                        timeMod = "is";
                    if(itGetsCold)
                        h3 = mContext.getString(R.string.temperature_gets_cold,
                                ((timeMod.equals("will")) ?
                                        mContext.getString(R.string.temperature_will_get):
                                        mContext.getString(R.string.temperature_is)),
                                Math.round(tempLowMax));
                    else if(tempLow)
                        h3 = String.format(mContext.getString(R.string.temperature_colder),
                                ((timeMod.equals("will")) ?
                                        mContext.getString(R.string.temperature_will_drop):
                                        mContext.getString(R.string.temperature_is_dropping)),
                                Math.round(tempLowMax));
                    else if(itGetsHot)
                        h3 = String.format(mContext.getString(R.string.temperature_gets_hot),
                                ((timeMod.equals("will")) ?
                                        mContext.getString(R.string.temperature_will_get):
                                        mContext.getString(R.string.temperature_is)),
                                Math.round(tempHighMax));
                    else if(tempHigh)
                        h3 = String.format(mContext.getString(R.string.temperature_hotter),
                                ((timeMod.equals("will")) ?
                                        mContext.getString(R.string.temperature_will_get):
                                        mContext.getString(R.string.temperature_is)),
                                Math.round(tempHighMax));
                    else
                        h3 = String.format(mContext.getString(R.string.temperature_moderate),
                                ((timeMod.equals("will"))?mContext.getString(R.string.temperature_will_be):mContext.getString(R.string.temperature_is)));

                } catch(Exception e) {
                    Log.e(TAG, "Adj Error: "+e.getMessage());
                }
            }
            public boolean isItIsCold() {
                return itIsCold;
            }

            public boolean isItIsHot() {
                return itIsHot;
            }

            public boolean isItGetsCold() {
                return itGetsCold;
            }

            public boolean isItGetsHot() {
                return itGetsHot;
            }

            public boolean isTempLow() {
                return tempLow;
            }

            public boolean isTempHigh() {
                return tempHigh;
            }

            public Double getTempHighMax() {
                return tempHighMax;
            }

            public Double getTempLowMax() {
                return tempLowMax;
            }

            public String getUnits() {
                return usingMetric()?"C":"F";
            }
        }

        public class RainAnalysis extends WeatherAnalysis {
            private boolean likelyToRain = false;
            private double rainDepth = 0;
            private boolean rainPercentRise = false;
            private boolean rainPercentDrop = false;
            private double probability = Float.valueOf(0);
            private int probabilityTime = 0;
            private String intensity = "No ";
            private double heaviestTime = 0;
            private String type;
            private boolean isRaining = false;

            private int timeToRain = -1;

            private static final double RAIN_THRESHOLD = 0.7;

            private RainAnalysis(WeatherSync w, TemperatureAnalysis t) throws JSONException {
                super();
                Double s = w.getPrecipitationProbability().get(0);
                Double smax = s;
                int smax_t = 0;
                double smin = s;
                double high = 0;
                double htime = 0;
                int c;
                type = mContext.getString(R.string.rain_rain);

                for(int i = 0;i<w.getPrecipitationProbability().size();i++) {
                    try {
                        if(w.getPrecipitationProbability().get(i) >= .7) {
                            likelyToRain = true;
                            if(timeToRain == -1)
                                timeToRain = i;
                        }
                        if(w.getPrecipitationProbability().get(i) > smax) {
                            rainPercentRise = true;
                            smax = w.getPrecipitationProbability().get(i);
                            smax_t = i;
                        }
                        if(w.getPrecipitationProbability().get(i) < smin) {
                            rainPercentDrop = true;
                            smin = w.getPrecipitationProbability().get(i);
                        }
                        if(w.getPrecipitationIntensity().get(i) > high) {
                            high = w.getPrecipitationIntensity().get(i);
                            htime = w.getTime().get(i);
                        }
                        try {
                            if(w.getPrecipitationProbability().get(i) > 0) {
                                type = w.getPrecipitationType().get(i);
                                if(type.toLowerCase().equals("rain"))
                                    type = mContext.getString(R.string.rain_rain);
                            }
                            if(w.getPrecipitationProbability().get(i) > 0 && w.getTemperatures().get(i) < 32)
                                type = mContext.getString(R.string.rain_snow);
                        } catch (Exception e) {
                            //Config.Cheers("PrecipType: "+e.getMessage());
                            type = mContext.getString(R.string.rain_rain);
                        }

                        if(w.getPrecipitationProbability().get(i) >= RAIN_THRESHOLD)
                            c = Color.RED;
                        else if(w.getPrecipitationProbability().get(i) - .1 > s )
                            c = Color.YELLOW;
                        else
                            c = Color.GREEN;
                        table.add(new WeatherDatum(c, w.getPrecipitationProbability().get(i)*100));
                    } catch(Exception e) {
                        c = Color.GREEN;
                        table.add(new WeatherDatum(c, (double) 0));
                        Log.e(TAG, "No property for RAIN["+i+"]:  "+e.getMessage());
                    }
                }

                probability = smax;
                probabilityTime = smax_t;
                if(usingMetric()) {
                    rainDepth = Conversions.getCm(high);
                } else {
                    rainDepth = high;
                }
                heaviestTime = htime;

                if(s >= RAIN_THRESHOLD)
                    isRaining = true;
                else
                    isRaining = false;

                if(rainDepth <= 0)
                    intensity = mContext.getString(R.string.rain_none);
                else if(rainDepth <= 0.001)
                    intensity = mContext.getString(R.string.rain_drizzle);
                else if(rainDepth <= 0.01)
                    intensity = mContext.getString(R.string.rain_very_light);
                else if(rainDepth <= 0.017)
                    intensity = mContext.getString(R.string.rain_light);
                else if(rainDepth <= 0.1)
                    intensity = mContext.getString(R.string.rain_moderate);
                else if(rainDepth <= 0.25)
                    intensity = mContext.getString(R.string.rain_heavy);
                else
                    intensity = mContext.getString(R.string.rain_very_heavy);

                h1 = mContext.getString(R.string.rain_title);
                if(type != null) {
                    h2 = String.format(mContext.getString(R.string.rain_subtitle), intensity, type);
                } else {
                    h2 = mContext.getString(R.string.rain_no_rain);
                }
                if(w.getPrecipitationIntensity().size() > 0 && w.getPrecipitationProbability().size() > 0) {
                    if(isRaining && rainPercentRise)
                        h3 = String.format(mContext.getString(R.string.rain_report_now_then_stop), type.toLowerCase());
                    else if(isRaining)
                        h3 = String.format(mContext.getString(R.string.rain_report_now), type.toLowerCase());
                    else if(likelyToRain)
                        h3 = String.format(mContext.getString(R.string.rain_report_likely), type.toLowerCase());
                    else if(rainPercentRise)
                        h3 = String.format(mContext.getString(R.string.rain_report_rise), type.toLowerCase(), Math.round(probability * 100));
                    else if(probability > 0.09)
                        h3 = String.format(mContext.getString(R.string.rain_report_highest_chance), type.toLowerCase(), Math.round(probability*100));
                    else
                        h3= String.format(mContext.getString(R.string.rain_none_expected), type.toLowerCase());
                } else {
                    h3 = mContext.getString(R.string.rain_no_data);
                }
            }

            public boolean isLikelyToRain() {
                return likelyToRain;
            }

            public boolean isRaining() {
                return isRaining;
            }

            public int getTimeToRain() {
                return timeToRain;
            }

            public String getType() {
                return type;
            }

            public String getIntensity() {
                return intensity;
            }
        }

        private class RainIntensityAnalysis extends WeatherAnalysis {
            private boolean likelyToRain = false;
            private double rainDepth = 0;
            private boolean rainIntensityRise = false;
            private boolean rainIntensityDrop = false;
            private double probability = Float.valueOf(0);
            private String intensity = "No ";
            private double heaviestTime;
            private String type;
            private boolean isRaining = false;

            private int timeToRain = -1;

            private static final double RAIN_THRESHOLD = 0.7;
            public static final double RAIN_VERY_LIGHT = 0.002;
            public static final double RAIN_LIGHT = 0.017;
            public static final double RAIN_MODERATE = 0.1;
            public static final double RAIN_HEAVY = 0.25;
            public static final double RAIN_VERY_HEAVY = 0.4;

            private RainIntensityAnalysis(WeatherSync w) throws JSONException {
                super();
                Double current = w.getPrecipitationIntensity().get(0);
                Double smax = current;
                int smax_t = 0;
                double smin = current;
                double high = 0;
                double htime = 0;
                int c;
                type = mContext.getString(R.string.rain_rain);

                for (int i = 0; i < w.getPrecipitationIntensity().size(); i++) {
                    try {
                        if (w.getPrecipitationIntensity().get(i) > smax) {
                            rainIntensityRise = true;
                            smax = w.getPrecipitationProbability().get(i);
                            smax_t = i;
                        }
                        if (w.getPrecipitationIntensity().get(i) < smin) {
                            rainIntensityDrop = true;
                            smin = w.getPrecipitationProbability().get(i);
                        }
                        if (w.getPrecipitationIntensity().get(i) > high) {
                            high = w.getPrecipitationIntensity().get(i);
                            htime = w.getTime().get(i);
                        }
                        try {
                            if(w.getPrecipitationProbability().get(i) > 0) {
                                type = w.getPrecipitationType().get(i);
                                if(type.toLowerCase().equals("rain"))
                                    type = mContext.getString(R.string.rain_rain);
                            }
                            if(w.getPrecipitationProbability().get(i) > 0 && w.getTemperatures().get(i) < 32)
                                type = mContext.getString(R.string.rain_snow);
                        } catch (Exception e) {
                            //Config.Cheers("PrecipType: "+e.getMessage());
                            type = mContext.getString(R.string.rain_rain);
                        }

                        Log.d(TAG, "At point " + i + ": " + w.getPrecipitationIntensity().get(i));
                        if (w.getPrecipitationIntensity().get(i) >= RAIN_HEAVY) {
                            c = Color.RED;
                        } else if (w.getPrecipitationIntensity().get(i) - .05 > current ||
                                w.getPrecipitationIntensity().get(i) >= RAIN_MODERATE) {
                            c = Color.YELLOW;
                        } else if (w.getPrecipitationIntensity().get(i) < current - .05) {
                            c = Color.BLUE;
                        } else {
                            c = Color.GREEN;
                        }

                        if (usingMetric()) {
                            rainDepth = Conversions.getCm(w.getPrecipitationIntensity().get(i));
                        } else {
                            rainDepth = w.getPrecipitationIntensity().get(i);
                        }

                        table.add(new WeatherDatum(c, rainDepth));
                    } catch (Exception e) {
                        c = Color.GREEN;
                        table.add(new WeatherDatum(c, (double) 0));
                        Log.e(TAG, "No property for RAIN[" + i + "]:  " + e.getMessage());
                    }
                }

                heaviestTime = htime;

                if (rainDepth <= 0)
                    intensity = mContext.getString(R.string.rain_none);
                else if (rainDepth <= 0.001)
                    intensity = mContext.getString(R.string.rain_drizzle);
                else if (rainDepth <= 0.01)
                    intensity = mContext.getString(R.string.rain_very_light);
                else if (rainDepth <= 0.017)
                    intensity = mContext.getString(R.string.rain_light);
                else if (rainDepth <= 0.1)
                    intensity = mContext.getString(R.string.rain_moderate);
                else if (rainDepth <= 0.25)
                    intensity = mContext.getString(R.string.rain_heavy);
                else
                    intensity = mContext.getString(R.string.rain_very_heavy);

                h1 = mContext.getString(R.string.rain_intensity_title);
                if (type != null) {
                    h2 = String.format(mContext.getString(R.string.rain_intensity_subtitle), (usingMetric()) ?
                            mContext.getString(R.string.rain_intensity_units_metric) :
                            mContext.getString(R.string.rain_intensity_units));
    //                h2 = String.format(mContext.getString(R.string.rain_intensity_subtitle), type, ((htime-new Date().getTime())/(1000*60*60))+"");
                } else {
                    h2 = mContext.getString(R.string.rain_no_rain);
                }

                if (w.getPrecipitationIntensity().size() > 0 && w.getPrecipitationProbability().size() > 0) {
                    if (rainIntensityRise) {
                        h3 = String.format(mContext.getString(R.string.rain_intensity_report_rise), type.toLowerCase());
                    } else if (rainIntensityDrop) {
                        h3 = String.format(mContext.getString(R.string.rain_intensity_report_drop), type.toLowerCase());
                    } else {
                        h3 = mContext.getString(R.string.rain_intensity_report_no_rain);
    //                    h3 = String.format(, type.toLowerCase());
                    }
                } else {
                    h3 = mContext.getString(R.string.rain_no_data);
                }
            }
        }

        public class WindAnalysis extends WeatherAnalysis {
            private boolean isWindyNow;
            private Double windHigh;
            private Double windLow;
            private boolean moreWindy;
            private boolean lessWindy;
            private boolean muchWindier;
            private String windTemp;
            private String dir;
            private String dirFull;
            private String units;
            private WindAnalysis(WeatherSync w) throws JSONException {
                super();
                Double s = w.getWindSpeed().get(0);
                isWindyNow = s > 10;
                Double high = s;
                Double low = s;
                Double bear = Double.valueOf(0);
                int c;
                for(int i=0;i<w.getWindSpeed().size();i++) {
                    if(w.getWindSpeed().get(i) > high) {
                        high = w.getWindSpeed().get(i);
                        if(high > 0)
                            bear = w.getWindBearing().get(i);
                    }
                    else if(w.getWindSpeed().get(i) < low) {
                        low = w.getWindSpeed().get(i);
                    }
                    if(w.getWindSpeed().get(i) - w.getWindSpeed().get(0) > 15)
                        muchWindier = true;

                    if(w.getWindSpeed().get(i) >= 25)
                        c = Color.RED;
                    else if(w.getWindSpeed().get(i) - s > 5)
                        c = Color.YELLOW;
                    else
                        c = Color.GREEN;
                    if(usingMetric())
                        table.add(new WeatherDatum(c, Conversions.getKPH(w.getWindSpeed().get(i))));
                    else
                        table.add(new WeatherDatum(c, w.getWindSpeed().get(i)));
                }
                if(high - s > 5 && (high - s) > (s - low)) {
                    moreWindy = true;
                    lessWindy = false;
                }
                if(s - low > 5 && (s - low) > (high - s)) {
                    moreWindy = false;
                    lessWindy = true;
                }
                if(s - low < 5 || high - s > 5)
                    isWindyNow = true;
                else
                    isWindyNow = false;
                windTemp = getWindTemp(high, mContext);
                if(high > 0) {
                    dir = getWindDir(bear);
                    dirFull = getWindDirFull(dir);
                }
                else {
                    dir = "";
                    dirFull = "";
                }

                if(usingMetric()) {
                    windLow = Conversions.getKPH(low);
                    windHigh = Conversions.getKPH(high);
                    units = mContext.getString(R.string.units_everyone_else_velocity);
                } else {
                    windLow = low;
                    windHigh = high;
                    units = mContext.getString(R.string.units_american_velocity);
                }
                h2 = "("+units+")";

                h1= mContext.getString(R.string.wind_title);
                String windtime = mContext.getString(R.string.wind_will_be);
                if(isWindyNow)
                    windtime = mContext.getString(R.string.wind_is);

                if(moreWindy)
                    h3 = String.format(mContext.getString(R.string.wind_more), windtime, windTemp, dirFull.toLowerCase());
                else if(lessWindy)
                    h3 = String.format(mContext.getString(R.string.wind_report_less), windTemp, dirFull.toLowerCase());
                else if(windHigh > 0)
                    h3 = String.format(mContext.getString(R.string.wind_report_specific), windtime, windTemp, dirFull.toLowerCase());
                else if(isWindyNow)
                    h3 = String.format(mContext.getString(R.string.wind_not_windy_later));
                else
                    h3 = String.format(mContext.getString(R.string.wind_not_windy));

            }
            public String getWindTemp(Double speed, Context mContext) {
                if(speed < 8)
                    return String.format(mContext.getString(R.string.wind_strength_light));
                else if(speed < 18)
                    return mContext.getString(R.string.wind_strength_moderate);
                else if(speed < 30)
                    return mContext.getString(R.string.wind_strength_medium);
                else if(speed < 100)
                    return mContext.getString(R.string.wind_strength_heavy);
                else
                    return mContext.getString(R.string.wind_strength_very_heavy);
            }
            public String getWindDir(double deg) {
                //True north @ 0 and clockwise
                /*
                    E - 90
                    NE - 45
                    NNE - 22.5
                    ENE - 45 + 22.5
                 */
                float d = 90;
                float hd = 45;
                float qd = (float) 22.5;
                float ed = (float) 12.25;
                if(deg >= ed && deg < qd + ed)
                    return mContext.getString(R.string.cardinal_nne);
                else if(deg >= qd + ed && deg < hd + ed)
                    return mContext.getString(R.string.cardinal_ne);
                else if(deg >= hd + ed && deg < hd + qd + ed)
                    return mContext.getString(R.string.cardinal_ene);
                else if(deg >= hd + qd + ed && deg < d + ed)
                    return mContext.getString(R.string.cardinal_e);
                else if(deg >= d + ed && deg < d + qd + ed)
                    return mContext.getString(R.string.cardinal_ese);
                else if(deg >= d + qd + ed && deg < d + hd + ed)
                    return mContext.getString(R.string.cardinal_se);
                else if(deg >= d + hd + ed && deg < d + hd + qd + ed)
                    return mContext.getString(R.string.cardinal_sse);
                else if(deg >= d + hd + qd + ed && deg < d + d + ed)
                    return mContext.getString(R.string.cardinal_s);
                else if(deg >= 2*d + ed && deg < 2*d + qd + ed)
                    return mContext.getString(R.string.cardinal_ssw);
                else if(deg >= 2*d + qd + ed && deg < 2*d + hd + ed)
                    return mContext.getString(R.string.cardinal_sw);
                else if(deg >= 2*d + hd + ed && deg < 2*d + hd + qd + ed)
                    return mContext.getString(R.string.cardinal_wsw);
                else if(deg >= 2*d + hd + qd + ed && deg < 2*d + ed)
                    return mContext.getString(R.string.cardinal_w);
                else if(deg >= 3*d + ed && deg < 3*d + qd + ed)
                    return mContext.getString(R.string.cardinal_wnw);
                else if(deg >= 3*d + qd + ed && deg < 3*d + hd + ed)
                    return mContext.getString(R.string.cardinal_nw);
                else if(deg >= 3*d + hd + ed && deg < 3*d + hd + qd + ed)
                    return mContext.getString(R.string.cardinal_nnw);
                else
                    return mContext.getString(R.string.cardinal_n);
            }
            public String getWindDirFull(String d) {
                if (d.equals("N")) {
                    return mContext.getString(R.string.cardinalf_n);
                } else if (d.equals("NNE")) {
                    return mContext.getString(R.string.cardinalf_n);
                } else if (d.equals("NE")) {
                    return mContext.getString(R.string.cardinalf_ne);
                } else if (d.equals("ENE")) {
                    return mContext.getString(R.string.cardinalf_e);
                } else if (d.equals("E")) {
                    return mContext.getString(R.string.cardinalf_e);
                } else if (d.equals("ESE")) {
                    return mContext.getString(R.string.cardinalf_e);
                } else if (d.equals("SE")) {
                    return mContext.getString(R.string.cardinalf_se);
                } else if (d.equals("SSE")) {
                    return mContext.getString(R.string.cardinalf_s);
                } else if (d.equals("S")) {
                    return mContext.getString(R.string.cardinalf_s);
                } else if (d.equals("SSW")) {
                    return mContext.getString(R.string.cardinalf_s);
                } else if (d.equals("SW")) {
                    return mContext.getString(R.string.cardinalf_sw);
                } else if (d.equals("WSW")) {
                    return mContext.getString(R.string.cardinalf_w);
                } else if (d.equals("W")) {
                    return mContext.getString(R.string.cardinalf_w);
                } else if (d.equals("WNW")) {
                    return mContext.getString(R.string.cardinalf_w);
                } else if (d.equals("NW")) {
                    return mContext.getString(R.string.cardinalf_nw);
                } else if (d.equals("NNW")) {
                    return mContext.getString(R.string.cardinalf_n);
                } else {
                    return mContext.getString(R.string.cardinalf_n);
                }
            }

            public boolean isWindyNow() {
                return isWindyNow;
            }

            public Double getWindHigh() {
                return windHigh;
            }

            public Double getWindLow() {
                return windLow;
            }

            public boolean isMoreWindy() {
                return moreWindy;
            }

            public boolean isLessWindy() {
                return lessWindy;
            }

            public boolean isMuchWindier() {
                return muchWindier;
            }

            public String getWindTemp() {
                return windTemp;
            }

            public String getDir() {
                return dir;
            }

            public String getDirFull() {
                return dirFull;
            }

            public String getUnits() {
                return units;
            }
        }

        public class BarometerAnalysis extends WeatherAnalysis {
            private Double barometer;
            private boolean barometerRises;
            private boolean barometerDrops;
            private Double barometerMax;
            private Double barometerMin;
            private boolean barometricSlight;
            private String units;
            private BarometerAnalysis(WeatherSync w) throws JSONException {
                Double s = w.getPressure().get(0);
                Double high = s;
                Double low = s;
                int c;

                for(int i=0;i<w.getPressure().size();i++) {
                    if(w.getPressure().get(i) > high)
                        high = w.getPressure().get(i);
                    if(w.getPressure().get(i) < low)
                        low = w.getPressure().get(i);

                    if(w.getPressure().get(i) - s > 13)
                        c = Color.RED;
                    else if(w.getPressure().get(i) - s < 13 && s - w.getPressure().get(i) < 13)
                        c = Color.YELLOW;
                    else if(s - w.getPressure().get(i) > 13)
                        c = Color.BLUE;
                    else
                        c = Color.GREEN;
                    if(usingMetric())
                        table.add(new WeatherDatum(c, Conversions.getCmHG(w.getPressure().get(i))));
                    else
                        table.add(new WeatherDatum(c, Conversions.getInHG(w.getPressure().get(i))));
                }
                if(high - s > 13 && (high - s) > (s - low)) {
                    barometerRises = true;
                    barometerDrops = false;
                }
                if(s - low > 13 && (s - low) > (high - s)) {
                    barometerRises = false;
                    barometerDrops = true;
                }
                if(high - w.getPressure().get(0) > 4 || w.getPressure().get(0) - low > 4)
                    barometricSlight = true;

                if(usingMetric()) {
                    units = mContext.getString(R.string.barometer_units_everyone_else);
                    barometerMin = Conversions.getCmHG(low);
                    barometerMax = Conversions.getCmHG(high);
                    barometer = Conversions.getCmHG(s);
                } else {
                    units = mContext.getString(R.string.barometer_units_american);
                    barometerMin = Conversions.getInHG(low);
                    barometerMax = Conversions.getInHG(high);
                    barometer = Conversions.getInHG(s);
                }

                //WEATHERCARD
                h1 = mContext.getString(R.string.barometer_title);
                h2 = "(" + units + ")";

                String bVal;
                if(barometricSlight) {
                    h3 = mContext.getString(R.string.barometer_report_slight_rise);
                    if(barometerDrops) {
                        h3 = mContext.getString(R.string.barometer_report_slight_drop);
                    }
                }
                else if(barometerDrops) {
                    bVal = barometerMin+"";
                    h3 = String.format(mContext.getString(R.string.barometer_report_drop), bVal, units);
                } else if(barometerRises) {
                    bVal = barometerMax+"";
                    h3 = String.format(mContext.getString(R.string.barometer_report_rise), bVal, units);
                } else
                    h3 = mContext.getString(R.string.barometer_report_no_change);
            }
        }

        public class CloudAnalysis extends WeatherAnalysis {
            private Double cloudHigh;
            private Double cloudLow;
            private boolean moreClouds;
            private boolean lessClouds;
            private String cloudCover;
            private CloudAnalysis(WeatherSync w) throws JSONException {
                Double s = w.getCloudCover().get(0);
                Double high = s;
                Double low = s;
                int c;

                for(int i=0;i<w.getCloudCover().size();i++) {
                    if(w.getCloudCover().get(i) > high)
                        high = w.getCloudCover().get(i);
                    if(w.getCloudCover().get(i) < low)
                        low = w.getCloudCover().get(i);

                    if(w.getCloudCover().get(i) > 0.75)
                        c = Color.RED;
                    else if(w.getCloudCover().get(i) - s > 0.1)
                        c = Color.YELLOW;
                    else
                        c = Color.GREEN;

                    table.add(new WeatherDatum(c, w.getCloudCover().get(i) * 100));

                }
                if(high - s > .1 && (high - s) > (s - low)) {
                    moreClouds = true;
                    lessClouds = false;
                }
                if(s - low > .1 && (s - low) > (high - s)) {
                    moreClouds = false;
                    lessClouds = true;
                }
                cloudLow = low;
                cloudHigh = high;
                if(cloudHigh <= 0.1)
                    cloudCover = mContext.getString(R.string.cloud_clear_sky);
                else if(cloudHigh <= 0.4)
                    cloudCover = mContext.getString(R.string.cloud_scattered_clouds);
                else if(cloudHigh <= 0.70)
                    cloudCover = mContext.getString(R.string.cloud_broken_cloud_cover);
                else if(cloudHigh <= 0.90)
                    cloudCover = mContext.getString(R.string.cloud_overcast);
                else if(cloudHigh <= 1)
                    cloudCover = mContext.getString(R.string.cloud_completely_overcast);

                //WEATHERCARD
                h1 = mContext.getString(R.string.cloud_cover);
                h2 = (cloudCover);
                if(lessClouds)
                    h3 = mContext.getString(R.string.cloud_will_dissipate);
                else if(moreClouds)
                    h3 = mContext.getString(R.string.cloud_will_gather);
                else
                    h3 = mContext.getString(R.string.cloud_no_change);
            }

            public Double getCloudHigh() {
                return cloudHigh;
            }

            public Double getCloudLow() {
                return cloudLow;
            }

            public boolean isMoreClouds() {
                return moreClouds;
            }

            public boolean isLessClouds() {
                return lessClouds;
            }

            public String getCloudCover() {
                return cloudCover;
            }
        }

        public class VisibilityAnalysis extends WeatherAnalysis {
            private Double visibilityHigh;
            private Double visibilityLow;
            private boolean moreVisible;
            private boolean lessVisible;
            private boolean littleVisibility;
            private String units;
            private VisibilityAnalysis(WeatherSync w) throws JSONException {
                Double s = w.getVisibility().get(0);
                Double high = s;
                Double low = s;
                int c;
                //w.visibility.size();
                for (int i = 0; i < w.getVisibility().size(); i++) {
                    if (w.getVisibility().get(i) > high)
                        high = w.getVisibility().get(i);
                    if (w.getVisibility().get(i) < low)
                        low = w.getVisibility().get(i);

                    if (s - w.getVisibility().get(i) > 2)
                        c = Color.YELLOW;
                    else if (w.getVisibility().get(i) < 4) {
                        c = Color.RED;
                        if(w.getVisibility().get(0) > 4)
                            littleVisibility = true;
                    } else
                        c = Color.GREEN;

                    if (usingMetric())
                        table.add(new WeatherDatum(c, Conversions.getKm(w.getVisibility().get(i))));
                    else
                        table.add(new WeatherDatum(c, w.getVisibility().get(i)));
                }
                if (high - s > 2 && (high - s) > (s - low)) {
                    moreVisible = true;
                    lessVisible = false;
                }
                if (s - low > 2 && (s - low) > (high - s)) {
                    moreVisible = false;
                    lessVisible = true;
                }
                if(usingMetric()) {
                    visibilityLow = Conversions.getKm(low);
                    visibilityHigh = Conversions.getKm(high);
                    units = mContext.getString(R.string.units_everyone_else_distance).toLowerCase();
                } else {
                    visibilityLow = low;
                    visibilityHigh = high;
                    units = mContext.getString(R.string.units_american_distance).toLowerCase();
                }
                //WEATHERCARD
                if (w.getVisibility().size() > 0) {
                    h1 = mContext.getString(R.string.visibility_title);
                    h2 = "("+units+")";
                    if (visibilityLow < 2)
                        h3 = mContext.getString(R.string.visibility_very_low);
                    else if (lessVisible)
                        h3 = mContext.getString(R.string.visibility_falling);
                    else if (visibilityHigh >= 10)
                        h3 = mContext.getString(R.string.visibility_no_problem);
                    else if (moreVisible)
                        h3 = mContext.getString(R.string.visibility_rising);
                    else
                        h3 = String.format(mContext.getString(R.string.visibility_report_specific), Math.round(visibilityLow)+"", units+"");
                }
            }

            public Double getVisibilityHigh() {
                return visibilityHigh;
            }

            public Double getVisibilityLow() {
                return visibilityLow;
            }

            public boolean isMoreVisible() {
                return moreVisible;
            }

            public boolean isLessVisible() {
                return lessVisible;
            }

            public boolean isLittleVisibility() {
                return littleVisibility;
            }

            public String getUnits() {
                return units;
            }
        }

        public class DewPointAnalysis extends WeatherAnalysis {
            public Double maxDewPoint;
            public Double minDewPoint;
            public boolean moreDewPoint;
            public boolean lessDewPoint;
            public DewPointAnalysis(WeatherSync w) throws JSONException {
                Double s = w.getDewPoint().get(0);
                Double high = s;
                Double low = s;
                int c;
                for(int i=0;i<w.getDewPoint().size();i++) {
                    if(w.getDewPoint().get(i) > high)
                        high = w.getDewPoint().get(i);
                    if(w.getDewPoint().get(i) < low)
                        low = w.getDewPoint().get(i);

                    if(w.getDewPoint().get(i) > 70)
                        c = Color.RED;
                    else if(s - w.getDewPoint().get(i) > 5)
                        c = Color.YELLOW;
                    else
                        c = Color.GREEN;
                    if(usingMetric()) {
                        table.add(new WeatherDatum(c, Conversions.getCelcius(w.getDewPoint().get(i))));
                    } else {
                        table.add(new WeatherDatum(c, w.getDewPoint().get(i)));
                    }
                }
                if(high - s > 2 && (high - s) > (s - low)) {
                    moreDewPoint = true;
                    lessDewPoint = false;
                }
                if(s - low > 2 && (s - low) > (high - s)) {
                    moreDewPoint = false;
                    lessDewPoint = true;
                }
                maxDewPoint = high;
                minDewPoint = low;
                if(usingMetric()) {
                    maxDewPoint = Conversions.getCelcius(maxDewPoint);
                    minDewPoint = Conversions.getCelcius(minDewPoint);
                }

                //WEATHER CARD
                h1 = mContext.getString(R.string.dew_title);
                h2 = mContext.getString(R.string.dew_units);
                if(moreDewPoint)
                    h3 = String.format(mContext.getString(R.string.dew_report_rise),
                            Math.round(maxDewPoint));
                else if(lessDewPoint)
                    h3 = String.format(mContext.getString(R.string.dew_report_drop),
                            Math.round(minDewPoint));
                else
                    h3 = mContext.getString(R.string.dew_report_no_change);
            }
        }

        public class OzoneAnalysis extends WeatherAnalysis {
            //http://www.ozoneservices.com/articles/007.htm
            //http://en.wikipedia.org/wiki/Dobson_unit
            //http://www.temis.nl/data/fortuin.html
            /*
                Convert between DU and mbar to find comfort data
                DU/mbar / 0.78914 should be ppm
             */
            public Double maxOzone;
            public Double minOzone;
            public boolean moreOzone;
            public boolean lessOzone;
            public float OZONE_THRESHOLD = 400;
            public OzoneAnalysis(WeatherSync w) throws JSONException {
                OZONE_THRESHOLD = Float.parseFloat(sm.getString(R.string.OZONE_THRESHOLD, "400"));
                Double s = w.getOzone().get(0);
                Double high = s;
                Double low = s;
                int c;
                for(int i=0;i<w.getOzone().size();i++) {
                    if(w.getOzone().get(i) > high)
                        high = w.getOzone().get(i);
                    if(w.getOzone().get(i) < low)
                        low = w.getOzone().get(i);

                    if(w.getOzone().get(i) > OZONE_THRESHOLD)
                        c = Color.RED;
                    else if(s - w.getOzone().get(i) > 50)
                        c = Color.YELLOW;
                    else
                        c = Color.GREEN;
                    table.add(new WeatherDatum(c, w.getOzone().get(i)));
                }
                if(high - s > 50 && (high - s) > (s - low)) {
                    moreOzone = true;
                    lessOzone = false;
                }
                if(s - low > 50 && (s - low) > (high - s)) {
                    moreOzone = false;
                    lessOzone = true;
                }
                maxOzone = high;
                minOzone = low;

                //WEATHER CARD
                h1 = mContext.getString(R.string.ozone_title);
                h2 = mContext.getString(R.string.ozone_units);
                if(maxOzone > OZONE_THRESHOLD*1.5)
                    h3 = mContext.getString(R.string.ozone_report_omg);
                else if(maxOzone > OZONE_THRESHOLD)
                    h3 = mContext.getString(R.string.ozone_report_wow);
                else if(moreOzone)
                    h3 = String.format(mContext.getString(R.string.ozone_report_rise),
                            Math.floor(maxOzone));
                else if(lessOzone)
                    h3 = String.format(mContext.getString(R.string.ozone_report_drop),
                            Math.floor(minOzone));
                else
                    h3 = mContext.getString(R.string.ozone_report_no_change);
            }
        }

        public class HumidityAnalysis extends WeatherAnalysis {
            private double maxHumidity;
            private double minHumidity;
            private boolean moreHumidity;
            private boolean lessHumidity;
            private boolean isHumidNow = false;
            private double HUMIDITY_THRESHOLD_HIGH = .60;
            private double HUMIDITY_THRESHOLD_LOW = .25;
            @SuppressLint("StringFormatInvalid")
            private HumidityAnalysis(WeatherSync w) throws JSONException {
                HUMIDITY_THRESHOLD_HIGH = Double.parseDouble(sm.getString(R.string.HUMIDITY_HIGH, ".6"));
                HUMIDITY_THRESHOLD_LOW = Double.parseDouble(sm.getString(R.string.HUMIDITY_LOW, ".25"));
                double s = w.getHumidity().get(0);
                double high = s;
                double low = s;
                int c;
                for(int i=0;i<w.getHumidity().size();i++) {
                    if(w.getHumidity().get(i) > high)
                        high = w.getHumidity().get(i);
                    if(w.getHumidity().get(i) < low)
                        low = w.getHumidity().get(i);

                    if(w.getHumidity().get(i) > HUMIDITY_THRESHOLD_HIGH)
                        c = Color.RED;
                    else if(w.getHumidity().get(i) < HUMIDITY_THRESHOLD_LOW)
                        c = Color.BLUE;
                    else if(Math.abs(s - w.getHumidity().get(i)) > .07)
                        c = Color.YELLOW;
                    else
                        c = Color.GREEN;
                    table.add(new WeatherDatum(c, w.getHumidity().get(i)*100));
                }
                if(high - s > .07 && (high - s) > (s - low)) {
                    moreHumidity = true;
                    lessHumidity = false;
                }
                if(s - low > .07 && (s - low) > (high - s)) {
                    moreHumidity = false;
                    lessHumidity = true;
                }
                maxHumidity = high;
                minHumidity = low;

                //WEATHER CARD
                h1 = mContext.getString(R.string.humidity_title);
                h2 = mContext.getString(R.string.humidity_units);
                String timestring = mContext.getString(R.string.humidity_will_be);
                if(maxHumidity > HUMIDITY_THRESHOLD_HIGH || minHumidity < HUMIDITY_THRESHOLD_LOW)
                    isHumidNow = true;
                if(isHumidNow) {
                    timestring = mContext.getString(R.string.humidity_is);
                }
                if(maxHumidity > HUMIDITY_THRESHOLD_HIGH)
                    h3 = mContext.getString(R.string.humidity_report_very_high, timestring);
                else if(minHumidity < HUMIDITY_THRESHOLD_LOW) {
                    h3 = String.format(mContext.getString(R.string.humidity_report_very_dry), timestring);
                }
                else if(moreHumidity)
                    h3 = String.format(mContext.getString(R.string.humidity_report_rise),
                            Math.round(maxHumidity * 100));
                else if(lessHumidity) {
                    h3 = String.format(mContext.getString(R.string.humidity_report_drop),
                            Math.round(minHumidity * 100));
                }
                else
                    h3 = mContext.getString(R.string.humidity_report_no_change);
            }

            public boolean isHumidNow() {
                return isHumidNow;
            }

            public boolean isLessHumidity() {
                return lessHumidity;
            }

            public boolean isMoreHumidity() {
                return moreHumidity;
            }
        }

        public void analyzeGraph(WeatherSync w) throws JSONException {
            String h3 = "";
            Calendar here = new GregorianCalendar();
            long ms = w.getSunrise();
            ms = ms*1000;
            here.setTimeInMillis(ms);
            DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
            df.setTimeZone(TimeZone.getDefault());
            String sunriseText = df.format(here.getTime());

            Calendar cal = new GregorianCalendar();
            ms = w.getSunset();
            ms = ms*1000;
            cal.setTimeInMillis(ms);
            df = DateFormat.getTimeInstance(DateFormat.SHORT);
            df.setTimeZone(TimeZone.getDefault());
            String sunsetText = df.format(cal.getTime());
            if(w.getSunrise() > 0 && w.getSunset() > 0 && w.getMoonPhase() > -1)
                h3 = (String.format(mContext.getString(R.string.graph_sunrise), sunriseText)+"\n" +
                        String.format(mContext.getString(R.string.graph_sunset), sunsetText)+"\n" +
                        String.format(mContext.getString(R.string.graph_moon), w.getMoonPhaseName())+"\n\n" +
                        address);
        }

        public void updateAddress(String add, WeatherSync ws) throws JSONException {
            // INSERT ANALYZE GRAPH
            String h3 = "";
            Calendar here = new GregorianCalendar();
            long ms = ws.getSunrise();
            ms = ms*1000;
            here.setTimeInMillis(ms);
            DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
            df.setTimeZone(TimeZone.getDefault());
            String sunriseText = df.format(here.getTime());

            Calendar cal = new GregorianCalendar();
            ms = ws.getSunset();
            ms = ms*1000;
            cal.setTimeInMillis(ms);
            df = DateFormat.getTimeInstance(DateFormat.SHORT);
            df.setTimeZone(TimeZone.getDefault());
            String sunsetText = df.format(cal.getTime());
            if(ws.getSunrise() > 0 && ws.getSunset() > 0 && ws.getMoonPhase() > -1)
                h3 = (String.format(mContext.getString(R.string.graph_sunrise), sunriseText)+"\n" +
                        String.format(mContext.getString(R.string.graph_sunset), sunsetText)+"\n" +
                        String.format(mContext.getString(R.string.graph_moon), ws.getMoonPhaseName())+"\n\n" +
                        String.format(mContext.getString(R.string.graph_location), add));
        }

        public RainAnalysis getPrecipitationAnalysis() {
            return precipitationAnalysis;
        }

        public TemperatureAnalysis getTemperatureAnalysis() {
            return temperatureAnalysis;
        }

        public HumidityAnalysis getHumidityAnalysis() {
            return humidityAnalysis;
        }

        public WindAnalysis getWindAnalysis() {
            return windAnalysis;
        }

        public CloudAnalysis getCloudAnalysis() {
            return cloudAnalysis;
        }

        public DewPointAnalysis getDewPointAnalysis() {
            return dewPointAnalysis;
        }

        public OzoneAnalysis getOzoneAnalysis() {
            return ozoneAnalysis;
        }

        public VisibilityAnalysis getVisibilityAnalysis() {
            return visibilityAnalysis;
        }
    }

    /**
     * Created by Nick on 10/20/2015.
     */
    public static interface DownloadManager {
        void onDownloadSuccess(String response);
        void onDownloadFailed(String response);
        void onNotDownloaded();
    }

    public static class MainActivity extends AppCompatActivity implements DownloadManager {
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

    /**
     * Version 1.1
     * Created by N on 14/9/2014.
     * Last Edited 25/3/2015
     *   * Support for floats
     * Last Edited 13/5/2015
     *   * Support for syncing data to wearables
     */
    public static class SettingsManager {
        private static final String TAG = SettingsManager.class.getSimpleName();
        private static final boolean DEBUG = false;

        private SharedPreferences sharedPreferences;
        private SharedPreferences.Editor editor;
        private Context mContext;

        public SettingsManager(Context context) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            mContext = context;
            editor = sharedPreferences.edit();
        }

        public boolean has(String key) {
            return sharedPreferences.contains(key);
        }

        public boolean has(int resId) {
            return sharedPreferences.contains(mContext.getString(resId));
        }

        public String getString(int resId) {
            return getString(mContext.getString(resId));
        }
        public String getString(String key) {
            return getString(key, "NULL", "");
        }
        public String getString(int resId, String def) {
            return getString(mContext.getString(resId), def);
        }
        public String getString(String key, String def) {
            return getString(key, "NULL", def);
        }
        public String getString(String key, String val, String def) {
            String result = sharedPreferences.getString(key, val);
            if(result == "NULL") {
                editor.putString(key, def);
                if (DEBUG) {
                    Log.d(TAG, key + ", " + def);
                }
                editor.commit();
                result = def;
            }
            return result;
        }

        public String setString(int resId, String val) {
            return setString(mContext.getString(resId), val);
        }

        public String setString(String key, String val) {
            editor.putString(key, val);
            editor.commit();
            return val;
        }

        public boolean getBoolean(int resId) {
            return getBoolean(mContext.getString(resId));
        }

        public boolean getBoolean(String key) {
            return getBoolean(key, false);
        }

        public boolean getBoolean(String key, boolean def) {
            boolean result = sharedPreferences.getBoolean(key, def);
            editor.putBoolean(key, result);
            editor.commit();
            return result;
        }

        public boolean setBoolean(int resId, boolean val) {
            return setBoolean(mContext.getString(resId), val);
        }

        public boolean setBoolean(String key, boolean val) {
            editor.putBoolean(key, val);
            editor.commit();
            return val;
        }

        public float getFloat(int resId, float def) {
            return sharedPreferences.getFloat(mContext.getString(resId), def);
        }

        public float getFloat(String key, float def) {
            return sharedPreferences.getFloat(key, def);
        }

        public int getInt(int resId) {
            return sharedPreferences.getInt(mContext.getString(resId), 0);
        }

        public int setInt(int resId, int val) {
            editor.putInt(mContext.getString(resId), val);
            editor.commit();
            return val;
        }

        public long getLong(int resId) {
            return getLong(resId, 0);
        }

        public long getLong(int resId, long def) {
            return sharedPreferences.getLong(mContext.getString(resId), def);
        }

        public long setLong(int resId, long val) {
            editor.putLong(mContext.getString(resId), val);
            editor.commit();
            return val;
        }

        //Default Stuff
        public static SharedPreferences getDefaultSharedPreferences(Context context) {
            return context.getSharedPreferences(getDefaultSharedPreferencesName(context),
                    getDefaultSharedPreferencesMode());
        }

        private static String getDefaultSharedPreferencesName(Context context) {
            return context.getPackageName() + "_preferences";
        }

        private static int getDefaultSharedPreferencesMode() {
            return Context.MODE_PRIVATE;
        }
    }

    /**
     * Created by Nick on 10/21/2015.
     */
    public static class WeatherAlert {
        private String title;
        private String description;
        private Date expires;

        public WeatherAlert(String title, String description, long expires) {
            this.title = title;
            this.description = description;
            this.expires = new Date(expires);
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public Date getExpires() {
            return expires;
        }

        public String getExpiryString() {
            return expires.getHours() + ":" + ((expires.getMinutes() < 10) ? "0" : "") +
                    expires.getMinutes();
        }
    }

    /**
     * Created by Nick on 10/20/2015.
     * It's rather frustrating to deal with both a WeatherData and WeatherSync objects for no
     * good reason. Plus all the datatypes are weird and poorly organized.
     *
     * This has to be a single class that does downloading and reading of JSON response.
     * There will be a separate DataAnalysis class that actually takes these abstract data types
     * and makes them meaningful.
     */
    public static class WeatherSync {
        private static final String TAG = WeatherSync.class.getSimpleName();
        private static final boolean DEBUG = false;

        private Context mContext;
        private DownloadManager downloadManager;
        private SettingsManager sm;
        private final String DARKSKY_API_KEY = "1ac4d9dcd61cf73be2c203ce575c83cb";
        private boolean liveWeather; // Whether this is from the time machine or not

        private DataAnalysis dataAnalysis3;

        public WeatherSync(Context context) {
            mContext = context;
            downloadManager = (DownloadManager) context;
            sm = new SettingsManager(context);
        }

        public WeatherSync(Context context, DownloadManager downloadManager) {
            mContext = context;
            this.downloadManager = downloadManager;
            sm = new SettingsManager(context);
        }

        /* URL Getters */
        public String getUrl(double latitude, double longitude) {
            Log.d(TAG, "Using a VPN? "+sm.getBoolean(R.string.pref_vpn_key));
            if(sm.getBoolean(R.string.pref_vpn_key)) {
                latitude = Double.parseDouble(sm.getString(R.string.LAST_CACHED_LAT));
                longitude = Double.parseDouble(sm.getString(R.string.LAST_CACHED_LONG));
            } else {
                sm.setString(R.string.LAST_CACHED_LAT, latitude + "");
                sm.setString(R.string.LAST_CACHED_LONG, longitude + "");
                sm.setLong(R.string.LAST_CACHED_TIME, new Date().getTime());
            }
            liveWeather = true;
            return "https://api.forecast.io/forecast/"+DARKSKY_API_KEY+"/"+latitude+","+longitude
                    +"?exclude=minutely,flags&lang="+mContext.getString(R.string.lang_code);
        }

        public String getUrl(double latitude, double longitude, Calendar lookupDate) {
            Log.d(TAG, "Using a VPN? "+sm.getBoolean(R.string.pref_vpn_key));
            if(sm.getBoolean(R.string.pref_vpn_key)) {
                latitude = Double.parseDouble(sm.getString(R.string.LAST_CACHED_LAT));
                longitude = Double.parseDouble(sm.getString(R.string.LAST_CACHED_LONG));
            } else {
                sm.setString(R.string.LAST_CACHED_LAT, latitude + "");
                sm.setString(R.string.LAST_CACHED_LONG, longitude + "");
                sm.setLong(R.string.LAST_CHECKED_TIME, lookupDate.getTime().getTime());
                Log.d(TAG, "https://api.forecast.io/forecast/" + DARKSKY_API_KEY + "/" + latitude + "," + longitude
                        + "," + (lookupDate.getTime().getTime() / 1000)+ "?exclude=minutely,flags&lang="
                        + mContext.getString(R.string.lang_code));
            }
            liveWeather = false;
            return "https://api.forecast.io/forecast/"+DARKSKY_API_KEY+"/"+latitude+","+longitude+","+
                    (lookupDate.getTime().getTime()/1000)+"?exclude=minutely,flags&lang="+mContext.getString(R.string.lang_code);
        }

        /* Connectivity */
        private boolean isOnline() {
            try {
                ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                boolean wifi_only = sm.getBoolean(R.string.sm_sync_wifi);
                Log.d(TAG, "Connection to " + cm.getActiveNetworkInfo().getTypeName() + " is " +
                        cm.getActiveNetworkInfo().isConnectedOrConnecting());
                if(cm.getActiveNetworkInfo().getType() != ConnectivityManager.TYPE_WIFI && wifi_only) {
                    Log.d(TAG, "Allowed on wi-fi only");
                    return false;
                }

                return cm.getActiveNetworkInfo().isConnectedOrConnecting();
            } catch (Exception e) {
                Log.d(TAG, e.getMessage()+"; false");
                return false;
            }
        }

        /**
         * Start to download the weather data regardless of user settings
         */
        public void startForcedDownload(String url) {
            if(isOnline()) {
                new DownloadWeatherAsyncTask().execute(url);
            } else {
                try {
                    postData("null"); //Will use last recorded data
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Downloads if data is too old or based on other user settings
         */
        public void startLazyDownload(String url) {
            long lastTime = 0;
            try {
                lastTime = sm.getLong(R.string.LAST_CACHED_TIME);
            } catch(Exception e) {
                //Catch my mistakes
            }
            long now = System.currentTimeMillis();
            Log.d(TAG, "Time now is " + now + "\n    Last time checked is " + lastTime + "\n    Diff: " +
                    (now - lastTime));

            Log.d(TAG, isOnline() + " && " + ((now-lastTime) > 1000 * 60 * 30) + " || " +
                    sm.getString(R.string.LAST_CACHED_WEATHER).isEmpty());
            if(isOnline() && ((now - lastTime) > 1000*60*30 ||
                    sm.getString(R.string.LAST_CACHED_WEATHER).isEmpty())) {
                new DownloadWeatherAsyncTask().execute(url);
            } else {
                // No mas
                Log.d(TAG, "No mas");
                Log.d(TAG, sm.getString(R.string.LAST_CACHED_WEATHER)+".");
                try {
                    postData(sm.getString(R.string.LAST_CACHED_WEATHER));
                    downloadManager.onNotDownloaded();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Directly gets from cache always
         */
        public void startCachedDownload() {
            try {
                postData(sm.getString(R.string.LAST_CACHED_WEATHER));
                downloadManager.onNotDownloaded();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private class DownloadWeatherAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... urls) {

                // params comes from the execute() call: params[0] is the url.
                Log.d(TAG, "Download from "+urls[0]);
                try {
                    return downloadUrl(urls[0]);
                } catch (IOException e) {
                    return "Unable to retrieve web page. URL may be invalid. "+e.getMessage();
                }
            }
            // onPostExecute displays the results of the AsyncTask.
            @Override
            protected void onPostExecute(String result) {
                Log.d(TAG, "Posting execution");
                // This has been successful, so now let's reanalyze
                dataAnalysis3 = null;
                if (liveWeather) {
                    sm.setLong(R.string.LAST_CACHED_TIME, System.currentTimeMillis());
                } else {
                    sm.setLong(R.string.LAST_CHECKED_TIME, System.currentTimeMillis());
                }
            }

            /**
             * Given a URL, establishes an HttpUrlConnection and retrieves
             * the web page content as a InputStream, which it returns as
             * a string.
             *
             * @param myurl
             * @return
             * @throws IOException
             */
            private String downloadUrl(String myurl) throws IOException {
                InputStream is = null;
                // Only display the first 1000 characters of the retrieved
                // web page content.
                int len = 1000;
                try {
                    URL url = new URL(myurl);
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
                    try {
                        postData(rawdata);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

        public Double getLatitude() {
            return Double.parseDouble(sm.getString(R.string.LAST_CACHED_LAT));
        }

        public Double getLongitude() {
            return Double.parseDouble(sm.getString(R.string.LAST_CACHED_LONG));
        }

        /**
         * Data Handler
         *
         * @param dataRaw
         * @throws JSONException
         */
        public void postData(String dataRaw) throws JSONException {
            Log.d(TAG, "Received data");
            Log.d(TAG, ":"+dataRaw);
            if (dataRaw.length() < 32) {
                //Then we need to return to last cached value
    //            dataRaw = sm.getString(R.string.LAST_CACHED_WEATHER, "null");
                Log.d(TAG, ":"+dataRaw.length()+":"+dataRaw);
                Log.d(TAG, "Recycle");
                downloadManager.onDownloadFailed(dataRaw);
            } else {
                if (liveWeather) {
                    sm.setString(R.string.LAST_CACHED_WEATHER, dataRaw);
                } else {
                    sm.setString(R.string.LAST_CHECKED_WEATHER, dataRaw);
                }
                Log.d(TAG, "Success");
                resetAllData();
                downloadManager.onDownloadSuccess(dataRaw);
            }
            if (dataRaw.equals("null")) { //Still...
                Toast.makeText(mContext, "Sorry. We cannot retrieve weather for you right now.", Toast.LENGTH_SHORT).show();
                downloadManager.onDownloadFailed("");
                return;
            }
        }

        /* This section is for all the JSON parsing. */
        public JSONObject getForecastObject() {
            String data = liveWeather ? sm.getString(R.string.LAST_CACHED_WEATHER) :
                    sm.getString(R.string.LAST_CHECKED_WEATHER);
            try {
                if(data.isEmpty()) {
                    return new JSONObject();
                } else {
                    return new JSONObject(data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return new JSONObject();
            }
        }

        public ArrayList<? extends Object> getArray(String key, Class convert) throws JSONException {
            int MAX_ITEMS = 15;
            JSONArray mHour = (JSONArray) getForecastObject().getJSONObject("hourly").get("data");
            ArrayList<Object> items = new ArrayList<>(MAX_ITEMS);
            if(getForecastObject().getJSONObject("currently").has(key)) {
                if(convert.equals(Double.class))
                    items.add(getForecastObject().getJSONObject("currently").getDouble(key));
                else if(convert.equals(Integer.class))
                    items.add((double) getForecastObject().getJSONObject("currently").getInt(key));
                else
                    items.add(getForecastObject().getJSONObject("currently").get(key));
            } else {
                items.add(null);
            }
            int iterations = Math.min(mHour.length(), MAX_ITEMS - 1);
            // It was like 90+. That's TOO much data.
            if (DEBUG) {
                Log.d(TAG, "Getting " + (iterations + 1) + " iterations of " + key);
            }
            for(int i=0;i<iterations;i++) {
                if(mHour.getJSONObject(i).has(key)) {
                    if(convert.equals(Double.class))
                        items.add(mHour.getJSONObject(i).getDouble(key));
                    else if(convert.equals(Integer.class))
                        items.add((double) mHour.getJSONObject(i).getInt(key));
                    else
                        items.add(mHour.getJSONObject(i).get(key));
                } else {
                    // Add a null
                    if (convert.isAssignableFrom(Double.class)) {
                        items.add(0);
                    } else {
                        items.add(null);
                    }
                }
            }
            return items;
        }
        public ArrayList<Double> getDoubleArray(String key) throws JSONException {
            return (ArrayList<Double>) getArray(key, Double.class);
        }
        public ArrayList<String> getStringArray(String key) throws JSONException {
            return (ArrayList<String>) getArray(key, String.class);
        }
        public ArrayList<Double> getConvertedDoubleArray(String key) throws JSONException {
            return (ArrayList<Double>) getArray(key, Integer.class);
        }

        private void resetAllData() {
            adjustedTemperatures = null;
            temperatures = null;
            time = null;
            precipitationIntensity = null;
            precipitationProbability = null;
            dewPoint = null;
            cloudCover = null;
            humidity = null;
            ozone = null;
            windSpeed = null;
            windBearing = null;
            pressure = null;
            visibility = null;
            dataAnalysis3 = null;
        }

        private ArrayList<Double> adjustedTemperatures;
        protected ArrayList<Double> getAdjustedTemperatures() throws JSONException {
            if(adjustedTemperatures == null)
                adjustedTemperatures = getDoubleArray("apparentTemperature");
            return adjustedTemperatures;
        }

        private ArrayList<Double> temperatures;
        protected ArrayList<Double> getTemperatures() throws JSONException {
            if(temperatures == null)
                temperatures = getDoubleArray("apparentTemperature");
            return temperatures;
        }

        private ArrayList<Double> time;
        protected ArrayList<Double> getTime() throws JSONException {
            if(time == null)
                time = getDoubleArray("time");
            return time;
        }

        private ArrayList<Double> precipitationIntensity;
        protected ArrayList<Double> getPrecipitationIntensity() throws JSONException {
            if(precipitationIntensity == null) {
                precipitationIntensity = getDoubleArray("precipIntensity");
            }
            return precipitationIntensity;
        }

        private ArrayList<Double> precipitationProbability;
        protected ArrayList<Double> getPrecipitationProbability() throws JSONException {
            if(precipitationProbability == null)
                precipitationProbability = getDoubleArray("precipProbability");
            return precipitationProbability;
        }

        private ArrayList<Double> dewPoint;
        protected ArrayList<Double> getDewPoint() throws JSONException {
            if(dewPoint == null)
                dewPoint = getDoubleArray("dewPoint");
            return dewPoint;
        }

        private ArrayList<Double> windSpeed;
        protected ArrayList<Double> getWindSpeed() throws JSONException {
            if(windSpeed == null)
                windSpeed = getDoubleArray("windSpeed");
            return windSpeed;
        }

        private ArrayList<Double> windBearing;
        protected ArrayList<Double> getWindBearing() throws JSONException {
            if (windBearing == null) {
                windBearing = getDoubleArray("windBearing");
            }
            return windBearing;
        }

        private ArrayList<Double> cloudCover;
        protected ArrayList<Double> getCloudCover() throws JSONException {
            if(cloudCover == null)
                cloudCover = getDoubleArray("cloudCover");
            return cloudCover;
        }

        private ArrayList<Double> humidity;
        protected ArrayList<Double> getHumidity() throws JSONException {
            if(humidity == null)
                humidity = getDoubleArray("humidity");
            return humidity;
        }

        private ArrayList<Double> pressure;
        protected ArrayList<Double> getPressure() throws JSONException {
            if(pressure == null)
                pressure = getDoubleArray("pressure");
            return pressure;
        }

        private ArrayList<Double> visibility;
        public ArrayList<Double> getVisibility() throws JSONException {
            if(visibility == null)
                visibility = getDoubleArray("visibility");
            return visibility;
        }

        private ArrayList<Double> ozone;
        public ArrayList<Double> getOzone() throws JSONException {
            if(ozone == null)
                ozone = getDoubleArray("ozone");
            return ozone;
        }

        private ArrayList<String> precipitationType;
        protected ArrayList<String> getPrecipitationType() throws JSONException {
            if (precipitationType == null) {
                precipitationType = getStringArray("precipType");
            }
            return precipitationType;
        }

        public ArrayList<String> getSummary() throws JSONException {
            return getStringArray("summary");
        }

        public ArrayList<String> getIcon() throws JSONException {
            return getStringArray("icon");
        }

        public long getSunrise() throws JSONException {
            JSONObject daily = getForecastObject().getJSONObject("daily");
            JSONArray data = daily.getJSONArray("data");
            return data.getJSONObject(0).getLong("sunriseTime");
        }

        public long getSunset() throws JSONException {
            JSONObject daily = getForecastObject().getJSONObject("daily");
            JSONArray data = daily.getJSONArray("data");
            return data.getJSONObject(0).getLong("sunsetTime");
        }

        public double getMoonPhase() throws JSONException {
            JSONObject daily = getForecastObject().getJSONObject("daily");
            JSONArray data = daily.getJSONArray("data");
            return data.getJSONObject(0).getDouble("moonPhase");
        }

        public String getMoonPhaseName() throws JSONException {
            return mContext.getString(WeatherUiUtils.getMoonPhase(getMoonPhase()));
        }

        public String getWeeklySummary() throws JSONException {
            JSONObject daily = getForecastObject().getJSONObject("daily");
            return daily.getString("summary");
        }

        public String getWeeklyIcon() throws JSONException {
            JSONObject daily = getForecastObject().getJSONObject("daily");
            return daily.getString("icon");
        }

        public String getDailySummary() throws JSONException {
            JSONObject daily = getForecastObject().getJSONObject("hourly");
            return daily.getString("summary");
        }

        public String getDailyIcon() throws JSONException {
            JSONObject daily = getForecastObject().getJSONObject("hourly");
            return daily.getString("icon");
        }

        public String getHourlySummary() throws JSONException {
            JSONObject daily = getForecastObject().getJSONObject("minutely");
            return daily.getString("summary");
        }

        public String getHourlyIcon() throws JSONException {
            JSONObject daily = getForecastObject().getJSONObject("minutely");
            return daily.getString("icon");
        }

        public String getCurrentSummary() throws JSONException {
            JSONObject daily = getForecastObject().getJSONObject("currently");
            return daily.getString("summary");
        }

        public String getCurrentIcon() throws JSONException {
            JSONObject daily = getForecastObject().getJSONObject("currently");
            return daily.getString("icon");
        }

        public long getCurrentTime() throws JSONException {
            JSONObject daily = getForecastObject().getJSONObject("currently");
            return daily.getLong("time")*1000;
        }

        public boolean hasLocatedNearbyStorm() throws JSONException {
            return getNearestStormDistance() >= 0;
        }

        public int getNearestStormDistance() throws JSONException {
            JSONObject daily = getForecastObject().getJSONObject("currently");
            if(daily.has("nearestStormDistance"))
                return daily.getInt("nearestStormDistance");
            else
                return -1;
        }

        public int getNearestStormBearing() throws JSONException {
            JSONObject daily = getForecastObject().getJSONObject("currently");
            if(daily.has("nearestStormBearing"))
                return daily.getInt("nearestStormBearing");
            else
                return 0;
        }

        public boolean hasAlerts() {
            if(getForecastObject().has("alerts"))
                return true;
            return false;
        }

        public ArrayList<WeatherAlert> getAlerts() throws JSONException {
            ArrayList<WeatherAlert> alerts = new ArrayList<>();
            if(hasAlerts()) {
                JSONArray alertArray = getForecastObject().getJSONArray("alerts");
                for(int i=0;i<alertArray.length();i++) {
                    JSONObject a = alertArray.getJSONObject(i);
                    WeatherAlert alert = new WeatherAlert(a.getString("title"), a.getString("description"), a.getLong("expires"));
                    alerts.add(alert);
                }
            }
            return alerts;
        }

        public DataAnalysis generateAnalysis() {
            try {
                if(dataAnalysis3 == null || dataAnalysis3.getTemperatureAnalysis() == null) {
                    dataAnalysis3 = new DataAnalysis(mContext, this);
                    long timeStart = System.currentTimeMillis();
                    dataAnalysis3.generate();
                    Log.d(TAG, "Generation took " + (System.currentTimeMillis() - timeStart) + " ms.");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return dataAnalysis3;
        }
    }

    /**
     * Created by N on 5/27/2015.
     */
    public static class WeatherUiUtils {
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
}

