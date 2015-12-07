package com.example.kzhu9.myapplication;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

/**
 * Created by kzhu9 on 12/4/15.
 */
public class OkHttpSingleton {
    private static OkHttpSingleton instance;
    private static OkHttpClient client;

    private OkHttpSingleton() {
    }

    public static synchronized OkHttpSingleton getInstance() {
        if (instance == null) {
            instance = new OkHttpSingleton();
        }
        return instance;
    }

    public synchronized OkHttpClient getClient(Context context) {
        if (client == null) {
            client = new OkHttpClient();
            client.setConnectTimeout(30, TimeUnit.MINUTES);
            client.setWriteTimeout(30, TimeUnit.MINUTES);
            client.setReadTimeout(30, TimeUnit.MINUTES);
            client.setCookieHandler(new CookieManager(
                    new PersistentCookieStore(context),
                    CookiePolicy.ACCEPT_ALL));
        }
        return client;
    }
}
