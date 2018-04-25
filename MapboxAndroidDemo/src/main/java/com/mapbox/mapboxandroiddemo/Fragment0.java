package com.mapbox.mapboxandroiddemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public class Fragment0 extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(getLogTag(), "onCreateView");
        final View view = inflater.inflate(R.layout.map_fragment, container, false);

        mMapView = view.findViewById(R.id.map_view);

        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap map) {

                mapboxMap = map;

                mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.099, -79.045), 3));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addClusteredGeoJsonSource();
                    }
                }, 1000);

                Toast.makeText(Fragment0.this.getContext(), R.string.zoom_map_in_and_out_instruction,
                        Toast.LENGTH_SHORT).show();
            }
        });

        ((TextView)view.findViewById(R.id.fragment_title)).setText("Fragment 0");

        return view;
    }


    @Override
    String getLogTag() {
        return Fragment0.class.getSimpleName();
    }
}
