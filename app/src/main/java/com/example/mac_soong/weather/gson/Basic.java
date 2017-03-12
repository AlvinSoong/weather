package com.example.mac_soong.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mac_soong on 2017/3/12.
 *
 * SerializedName 将JSON中字段映射到Java的变量名
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }
}
