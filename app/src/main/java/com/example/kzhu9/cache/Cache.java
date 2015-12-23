package com.example.kzhu9.cache;

import android.graphics.Bitmap;

import java.util.HashMap;

/**
 * Created by slgu1 on 12/22/15.
 */
public class Cache {

    private static Cache cache = null;
    private HashMap <String, Bitmap> mp = new HashMap<String, Bitmap>();
    public static synchronized Cache single() {
        if (cache == null) {
            cache = new Cache();
            return cache;
        }
        return cache;
    }
    public static void main() {

    }
}