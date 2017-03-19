package com.example.mac_soong.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mac_soong on 2017/3/19.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String info;
    }
}
