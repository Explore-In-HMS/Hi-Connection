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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationResult;
import com.hms.wireless.hiconnection.R;
import com.hms.wireless.hiconnection.video.PlayActivity;
import com.hms.wireless.hiconnection.service.LocationService;
import com.hms.wireless.hiconnection.service.NetworkQoeService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "<!!!> MainActivity";

    private static final String KEY_CHANNEL_NUM = "channelNum";
    private static final String KEY_CHANNEL_INDEX = "channelIndex";
    private static final String KEY_UL_RTT = "uLRtt";
    private static final String KEY_DL_RTT = "dLRtt";
    private static final String KEY_UL_BANDWIDTH = "uLBandwidth";
    private static final String KEY_DL_BANDWIDTH = "dLBandwidth";
    private static final String KEY_UL_RATE = "uLRate";
    private static final String KEY_DL_RATE = "dLRate";
    private static final String KEY_NET_QOE_LEVEL = "netQoeLevel";
    private static final String KEY_UL_PACKAGE_LOSS_RATE = "uLPkgLossRate";

    private TextView resultTextView;
    private int textCounter = 0;

    private NetworkQoeService networkQoeService;
    private LocationService locationService;

    private String networkData = null;
    private String qoeLevel = null;
    private Location locationData = null;

    private AlertDialog progressDialog;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkQoeService.destroyService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkQoeService = new NetworkQoeService(this);
        locationService = new LocationService(this);

        resultTextView = findViewById(R.id.resultext);
        resultTextView.setMovementMethod(new ScrollingMovementMethod());

        Button callbackButton = findViewById(R.id.callbackbtn);
        Button realTimeDataButton = findViewById(R.id.getrealtimebtn);
        Button openMapButton = findViewById(R.id.openMapButton);
        Button videoKitButton = findViewById(R.id.videokitbtn);

        callbackButton.setOnClickListener(v -> getCallBackData());

        realTimeDataButton.setOnClickListener(v -> getNetworkData());

        openMapButton.setOnClickListener(v -> {
            progressDialog = showProgressAlertDialog();
            getNetworkData();
            getLocationData();
        });

        videoKitButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PlayActivity.class);
            MainActivity.this.startActivity(intent);
        });
    }

    private void getCallBackData() {
        Bundle data = NetworkQoeService.getQoeInfoBundle();

        if(data != null) {
            int channelNum = 0;
            if (data.containsKey(KEY_CHANNEL_NUM)) {
                channelNum = data.getInt(KEY_CHANNEL_NUM);
            }
            StringBuilder channelQoe = new StringBuilder("channelNum: " + channelNum);
            for (int i = 0; i < channelNum; i++) {
                // channelQoe can be displayed on the user interface through EditText.
                channelQoe.append(",channelIndex: ")
                        .append(data.getInt(KEY_CHANNEL_INDEX + i))
                        .append(",uLRtt: ")
                        .append(data.getInt(KEY_UL_RTT + i))
                        .append(",dLRtt: ")
                        .append(data.getInt(KEY_DL_RTT + i))
                        .append(",uLBandwidth: ")
                        .append(data.getInt(KEY_UL_BANDWIDTH + i))
                        .append(",dLBandwidth: ")
                        .append(data.getInt(KEY_DL_BANDWIDTH + i))
                        .append(",uLRate: ")
                        .append(data.getInt(KEY_UL_RATE + i))
                        .append(",dLRate: ")
                        .append(data.getInt(KEY_DL_RATE + i))
                        .append(",netQoeLevel: ")
                        .append(data.getInt(KEY_NET_QOE_LEVEL + i))
                        .append(",uLPkgLossRate: ")
                        .append(data.getInt(KEY_UL_PACKAGE_LOSS_RATE + i))
                        .append("\n");
            }
            updateText(channelQoe.toString());

        } else {
            Log.e(TAG, "callbackData else");
        }
    }

    private void getNetworkData() {
        Bundle data = networkQoeService.getRealTimeData();
        if(data != null) {
            int channelNum = 0;
            if (data.containsKey(KEY_CHANNEL_NUM)) {
                channelNum = data.getInt(KEY_CHANNEL_NUM);
            }

            int index = channelNum - 1;

            networkData = "Channel Number : " + channelNum + "\n" +
                    "Channel Index : " + data.getInt(KEY_CHANNEL_INDEX + index) + "\n" +
                    "UpLink Latency : " + data.getInt(KEY_UL_RTT + index) + "\n" +
                    "DownLink Latency : " + data.getInt(KEY_DL_RTT + index) + "\n" +
                    "UpLink Bandwidth : " + data.getInt(KEY_UL_BANDWIDTH + index) + "\n" +
                    "DownLink Bandwidth : " + data.getInt(KEY_DL_BANDWIDTH + index) + "\n" +
                    "UpLink Rate : " + data.getInt(KEY_UL_RATE + index) + "\n" +
                    "DownLink Rate : " + data.getInt(KEY_DL_RATE + index) + "\n" +
                    "QoE Level : " + data.getInt(KEY_NET_QOE_LEVEL + index) + "\n" +
                    "UpLink Package Loss Rate : " + data.getInt(KEY_UL_PACKAGE_LOSS_RATE + index) + "\n" +
                    "Download Speed in Mbps : " + data.getInt(KEY_DL_BANDWIDTH + index) / 1000.0
            ;
            qoeLevel = data.getInt(KEY_NET_QOE_LEVEL + index) + "";

            updateText(networkData);

        } else {
            Log.e(TAG, "realTimeData else");
        }
    }

    private void getLocationData() {
        locationService.getLocationUpdate(new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    locationData = locationResult.getLastLocation();

                    openMapActivity();

                    locationService.stopLocationUpdates(this);

                    progressDialog.cancel();
                }
            }
        });
    }

    private void openMapActivity() {
        this.startActivity(MapActivity.newIntent(this, networkData, qoeLevel, locationData.getLatitude(), locationData.getLongitude()));
    }

    private void updateText(String text) {
        String basket= resultTextView.getText().toString();
        basket += "\n******\nCounter: "+ textCounter +"-!-!-"+text;

        textCounter++;

        resultTextView.setText(basket);
    }

    private AlertDialog showProgressAlertDialog(){
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Loading...");

        LayoutInflater factory = LayoutInflater.from(this);
        View progressBar = factory.inflate(R.layout.progress_bar, null);
        builder.setView(progressBar);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        return alertDialog;
    }

}