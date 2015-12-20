package com.example.kzhu9.myapplication;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.kzhu9.fragments.MapViewFragment;

public class MapActivity extends AppCompatActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_map);

       FragmentManager fm = getSupportFragmentManager();
       fm.beginTransaction().add(R.id.container, new MapViewFragment(), null).commit();
   }
}
