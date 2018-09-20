package com.example.darkskyandroid;

import java.util.Date;

/**
 * Created by Nick on 10/21/2015.
 */
public class WeatherAlert {
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