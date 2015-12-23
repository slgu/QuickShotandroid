package com.example.kzhu9.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.kzhu9.fragments.MapViewFragment;

public class MapActivity extends AppCompatActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_map);
       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       setSupportActionBar(toolbar);
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();


       Intent intent = getIntent();
       intent.getParcelableArrayListExtra("123");
       Fragment fragment = new MapViewFragment();
       Bundle bundle = new Bundle();
       bundle.putParcelableArrayList("123",intent.getParcelableArrayListExtra("123"));
       fragment.setArguments(bundle);
       FragmentManager fm = getSupportFragmentManager();
       fm.beginTransaction().add(R.id.container, fragment, null).commit();

   }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
