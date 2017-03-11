package com.example.mac_soong.weather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by mac_soong on 2017/3/11.
 */

public class City extends DataSupport {
    private int id;
    private String cityName;
    private int cityCode;
    private int provinceID;

    public void setId(int id) {
        this.id = id;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public void setProvinceID(int provinceID) {
        this.provinceID = provinceID;
    }

    public int getId() {
        return id;
    }

    public String getCityName() {
        return cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public int getProvinceID() {
        return provinceID;
    }
}
