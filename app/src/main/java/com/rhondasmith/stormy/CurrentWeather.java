package com.rhondasmith.stormy;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static android.content.ContentValues.TAG;

public class CurrentWeather {

    private String locationLabel;
    private String icon;
    private double temperature;
    private double humidity;
    private double precipChance;
    private String summary;
    private double time;
    // Adding timeZone (+it's getter and setter) which we got from the API and logged in
    // Main Activity, to the data model to help with the human readable time:
    private String timeZone;

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getLocationLabel() {
        return locationLabel;
    }

    public void setLocationLabel(String locationLabel) {
        this.locationLabel = locationLabel;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getPrecipChance() {
        return precipChance;
    }

    public void setPrecipChance(double precipChance) {
        this.precipChance = precipChance;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public double getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getFormattedTime() {
        // SimpleDateFormat and TimeZone are built-in Android classes that are imported here:
        // Setting the human readable format for time we want to use:
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");

        // Setting the time zone for the newly formatted time, we take the timezone set when
        // the CurrentWeather object is constructed:
        formatter.setTimeZone(TimeZone.getTimeZone(timeZone));

        // The tutorial says to multiply time * 1000 to get current milliseconds but
        // this Date constructor returns current milliseconds already now:
        Date dateTime = new Date();

        // Return the timezone appropriate human readable formatted time:
        return formatter.format(dateTime);
    }


}
