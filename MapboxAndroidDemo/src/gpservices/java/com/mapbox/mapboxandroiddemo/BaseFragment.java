package com.mapbox.mapboxandroiddemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.net.MalformedURLException;
import java.net.URL;

import static com.mapbox.mapboxsdk.style.expressions.Expression.all;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.gte;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.lt;
import static com.mapbox.mapboxsdk.style.expressions.Expression.toNumber;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;

abstract class BaseFragment extends Fragment {

    private final String TAG = getLogTag();

    abstract String getLogTag();

    MapView mMapView;
    MapboxMap mapboxMap;

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: " + outState);
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
        // call mapview.onDestroy in onDestroyView for fragments!
        // https://github.com/mapbox/mapbox-gl-native/issues/9793#issuecomment-323282822
        mMapView.onDestroy();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        Log.d(TAG, "onLowMemory");
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    void addClusteredGeoJsonSource() {
        Log.d(getLogTag(), "addClusteredGeoJsonSource");
        // Add a new source from the GeoJSON data and set the 'cluster' option to true.
        try {
            mapboxMap.addSource(
                    // Point to GeoJSON data. This example visualizes all M1.0+ earthquakes from
                    // 12/22/15 to 1/21/16 as logged by USGS' Earthquake hazards program.
                    new GeoJsonSource("earthquakes",
                            new URL("https://www.mapbox.com/mapbox-gl-js/assets/earthquakes.geojson"),
                            new GeoJsonOptions()
                                    .withCluster(true)
                                    .withClusterMaxZoom(14)
                                    .withClusterRadius(50)
                    )
            );
        } catch (MalformedURLException malformedUrlException) {
            Log.e("dataClusterActivity", "Check the URL " + malformedUrlException.getMessage());
        }


        // Use the earthquakes GeoJSON source to create three layers: One layer for each cluster category.
        // Each point range gets a different fill color.
        int[][] layers = new int[][] {
                new int[] {150, ContextCompat.getColor(this.getContext(), R.color.mapboxRed)},
                new int[] {20, ContextCompat.getColor(this.getContext(), R.color.mapboxGreen)},
                new int[] {0, ContextCompat.getColor(this.getContext(), R.color.mapbox_blue)}
        };

        //Creating a marker layer for single data points
        SymbolLayer unclustered = new SymbolLayer("unclustered-points", "earthquakes");
        unclustered.setProperties(iconImage("marker-15"));
        mapboxMap.addLayer(unclustered);

        for (int i = 0; i < layers.length; i++) {
            //Add clusters' circles
            CircleLayer circles = new CircleLayer("cluster-" + i, "earthquakes");
            circles.setProperties(
                    circleColor(layers[i][1]),
                    circleRadius(18f)
            );

            Expression pointCount = toNumber(get("point_count"));

            // Add a filter to the cluster layer that hides the circles based on "point_count"
            circles.setFilter(
                    i == 0
                            ? gte(pointCount, literal(layers[i][0])) :
                            all(
                                    gte(pointCount, literal(layers[i][0])),
                                    lt(pointCount, literal(layers[i - 1][0]))
                            )
            );
            mapboxMap.addLayer(circles);
        }

        //Add the count labels
        SymbolLayer count = new SymbolLayer("count", "earthquakes");
        count.setProperties(
                textField("{point_count}"),
                textSize(12f),
                textColor(Color.WHITE)
        );
        mapboxMap.addLayer(count);

    }

}
