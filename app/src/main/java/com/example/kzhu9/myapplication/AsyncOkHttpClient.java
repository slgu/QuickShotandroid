package com.example.kzhu9.myapplication;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class AsyncOkHttpClient {

    private static final OkHttpClient SINGLETON = new OkHttpClient();

    private AsyncOkHttpClient() {
    }

    public static void get(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        execute(request, callback);
    }

    private static void execute(final Request request, final Callback callback) {
        SINGLETON.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
            final Handler mainHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(final Request request, final IOException e) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(null, e);
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!response.isSuccessful()) {
                            callback.onFailure(response, null);
                            return;
                        }
                        try {
                            callback.onSuccess(response, response.body().string());
                        } catch (final IOException e) {
                            Log.d(e.getMessage(), e.toString());
                        }
                    }
                });
            }
        });
    }

    public interface Callback {

        public void onFailure(Response response, Throwable throwable);

        public void onSuccess(Response response, String content);
    }

}