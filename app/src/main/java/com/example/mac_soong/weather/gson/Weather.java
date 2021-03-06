package com.example.mac_soong.weather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mac_soong on 2017/3/19.
 *
 */

public class Weather {
    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
