/*
 * Copyright 2020. Explore in HMS. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hms.wireless.hiconnection.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.maps.CameraUpdate;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.MapsInitializer;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.CameraUpdateParam;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.MarkerOptions;
import com.hms.wireless.hiconnection.R;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String GOOD_QOE_LEVEL = "4";
    private static final String BAD_QOE_LEVEL = "5";

    private static final String KEY_NETWORK_DATA = "KEY_NETWORK_DATA";
    private static final String KEY_QOE_LEVEL = "KEY_QOE_LEVEL";
    private static final String KEY_LATITUDE = "KEY_LATITUDE";
    private static final String KEY_LONGITUDE = "KEY_LONGITUDE";

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final String API_KEY = "CgB6e3x9IyYx+tkWhuTGvt3CN7l5AGysMmfHzPiFwvyoFXFnSSM0GSmLG/WNMEs56L4zb1JgxHmuihSSxNVW0Bu0";

    private MapView mMapView;
    private LatLng latLng;
    private String networkData;
    private String qoeLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        networkData = getIntent().getStringExtra(KEY_NETWORK_DATA);
        qoeLevel = getIntent().getStringExtra(KEY_QOE_LEVEL);
        double latitude = getIntent().getDoubleExtra(KEY_LATITUDE, 0);
        double longitude = getIntent().getDoubleExtra(KEY_LONGITUDE, 0);

        latLng = new LatLng(latitude, longitude);

        mMapView = findViewById(R.id.mapView);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        MapsInitializer.setApiKey(API_KEY);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
        //update camera view
        CameraUpdateParam cameraUpdateParam = new CameraUpdateParam();
        cameraUpdateParam.setLatLng(latLng);
        CameraUpdate cameraUpdate = new CameraUpdate(cameraUpdateParam);
        huaweiMap.moveCamera(cameraUpdate);

        //add marker
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .snippet(networkData);

        if (GOOD_QOE_LEVEL.equals(qoeLevel)){
            markerOptions.title("GOOD NETWORK CONNECTION");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }
        else if (BAD_QOE_LEVEL.equals(qoeLevel)) {
            markerOptions.title("BAD NETWORK CONNECTION");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        huaweiMap.addMarker(markerOptions);

        huaweiMap.setInfoWindowAdapter(new HuaweiMap.InfoWindowAdapter() {
            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }

            @Override
            public View getInfoWindow(Marker marker) {
                LinearLayout info = new LinearLayout(MapActivity.this);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(MapActivity.this);
                title.setTextColor(Color.BLACK);
                title.setBackgroundColor(Color.LTGRAY);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(MapActivity.this);
                snippet.setTextColor(Color.BLACK);
                snippet.setBackgroundColor(Color.LTGRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
    }

    public static Intent newIntent(
            Context context,
            String networkData,
            String qoeLevel,
            Double latitude,
            Double longitude) {
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra(KEY_NETWORK_DATA, networkData);
        intent.putExtra(KEY_QOE_LEVEL, qoeLevel);
        intent.putExtra(KEY_LATITUDE, latitude);
        intent.putExtra(KEY_LONGITUDE, longitude);

        return intent;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
}
