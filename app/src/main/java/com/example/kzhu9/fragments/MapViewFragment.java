package com.example.kzhu9.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kzhu9.myapplication.R;
import com.example.kzhu9.myapplication.TopicItems;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonPointStyle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jinliang on 12/10/15.
 */
public class MapViewFragment extends Fragment {
    MapView mapView;
    GoogleMap map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map_view, container, false);

        mapView = (MapView) v.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);
        MapsInitializer.initialize(this.getActivity());
        CameraUpdate cameraUpdate = CameraUpdateFactory
                .newLatLngZoom(new LatLng(40.808226, -73.961845), 12);
        map.animateCamera(cameraUpdate);
        Bundle args = getArguments();
        ArrayList<TopicItems> t = new ArrayList<>();
        t = args.getParcelableArrayList("123");

        JSONObject json = arrayListToGeoJson(t);


        GeoJsonLayer layer = null;
        layer = new GeoJsonLayer(map, json);

        GeoJsonPointStyle pointStyle = new GeoJsonPointStyle();
        pointStyle.setTitle("Marker at Columbia University");
        pointStyle.setIcon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        pointStyle.setAnchor(0.1f, 0.1f);

        for (GeoJsonFeature feature : layer.getFeatures()) {
            feature.setPointStyle(pointStyle);
        }
        layer.addLayerToMap();

        return v;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public JSONObject arrayListToGeoJson(ArrayList<TopicItems> arrayList) {
        JSONObject featureCollection = new JSONObject();
        try {
            featureCollection.put("type", "FeatureCollection");
            JSONArray featureList = new JSONArray();
            // iterate through your list
            for (TopicItems obj : arrayList) {
                JSONObject point = new JSONObject();
                point.put("type", "Point");
                // construct a JSONArray from a string; can also use an array or list
                JSONArray coord = new JSONArray("[" + obj.getLongitude() + "," + obj.getLatitude() + "]");
                point.put("coordinates", coord);
                JSONObject feature = new JSONObject();
                feature.put("geometry", point);
                feature.put("type", "Feature");

                featureList.put(feature);
                featureCollection.put("features", featureList);

            }
        } catch (JSONException e) {
            //Log.i("can't save json object: "+e.toString());
        }
        // output the result
        System.out.println("featureCollection=" + featureCollection.toString());
        return featureCollection;
    }

}


