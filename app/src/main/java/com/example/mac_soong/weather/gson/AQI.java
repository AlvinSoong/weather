package com.example.mac_soong.weather.gson;

/**
 * Created by mac_soong on 2017/3/19.
 */

public class AQI {
    public AQICity city;
    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
