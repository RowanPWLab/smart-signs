package com.example.darkskyandroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Nick on 10/21/2015.
 * This is a new class which should help deal with some of the intricacies of
 * unique data types. In addition to supporting a normal weather card, this will also
 * support premium summaries and alerts out of the box with custom conditions for each of those.
 */
public class DataAnalysis {
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