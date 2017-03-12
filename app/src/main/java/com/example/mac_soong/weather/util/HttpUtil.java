package com.example.mac_soong.weather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by mac_soong on 2017/3/11.
 *
 * 发起HTTP请求 与服务器交互  并注册一个回调处理服务器的响应
 */

public class HttpUtil {
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
