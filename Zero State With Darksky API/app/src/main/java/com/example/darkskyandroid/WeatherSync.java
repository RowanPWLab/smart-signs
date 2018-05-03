package com.example.darkskyandroid;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Nick on 10/20/2015.
 * It's rather frustrating to deal with both a WeatherData and WeatherSync objects for no
 * good reason. Plus all the datatypes are weird and poorly organized.
 *
 * This has to be a single class that does downloading and reading of JSON response.
 * There will be a separate DataAnalysis class that actually takes these abstract data types
 * and makes them meaningful.
 */
public class WeatherSync {
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