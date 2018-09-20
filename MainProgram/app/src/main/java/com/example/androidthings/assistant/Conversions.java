package com.example.androidthings.assistant;

/**
 * Created by N on 7/26/13.
 */
public class Conversions {
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