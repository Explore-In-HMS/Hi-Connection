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

package com.hms.wireless.hiconnection.video;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.hms.wireless.hiconnection.service.NetworkQoeService;
import com.huawei.hms.videokit.player.WisePlayer;
import com.hms.wireless.hiconnection.R;

public class PlayActivity extends AppCompatActivity implements OnWisePlayerListener, SurfaceHolder.Callback, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "<!!> PlayActivity";

    private WisePlayer wisePlayer;
    private int currentTime = 0;

    private SurfaceView surfaceView;
    private Boolean videoPlaying = true;
    private NetworkQoeService networkQoeService;

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.play_video);

        networkQoeService = new NetworkQoeService(this);

        surfaceView = findViewById(R.id.surface_view);
        surfaceView.setVisibility(View.VISIBLE);

        TextView dLBandwidthText = findViewById(R.id.dLBandwidthtxt);
        ImageButton backButton = findViewById(R.id.backbtn);
        ImageButton playPauseButton = findViewById(R.id.play_btn);

        backButton.setOnClickListener(v -> {
            wisePlayer.stop();
            finish();
        });

        playPauseButton.setOnClickListener(v -> {
            if (videoPlaying) {
                wisePlayer.pause();
                playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(
                        getResources(),
                        R.drawable.start,
                        null));
            }
            else {
                wisePlayer.start();
                playPauseButton.setImageDrawable(ResourcesCompat.getDrawable(
                        getResources(),
                        R.drawable.pause,
                        null));
            }
            videoPlaying = !videoPlaying;
        });

        OnWisePlayerListener onWisePlayerListener = this;
        if (VideoKitPlayApplication.getWisePlayerFactory() != null) {
            Log.i(TAG, "VideoKitPlayApplication.getWisePlayerFactory() != null");
            //Get wisePlayer from VideoKitPlayApplication
            wisePlayer = VideoKitPlayApplication.getWisePlayerFactory().createWisePlayer();
        }
        else {
            Log.e(TAG, "VideoKitPlayApplication.getWisePlayerFactory() == null");
        }

        if (wisePlayer != null) {
            Log.i(TAG, "wisePlayer != null");
            wisePlayer.setErrorListener(onWisePlayerListener);
            wisePlayer.setEventListener(onWisePlayerListener);
            wisePlayer.setResolutionUpdatedListener(onWisePlayerListener);
            wisePlayer.setReadyListener(onWisePlayerListener);
            wisePlayer.setLoadingListener(onWisePlayerListener);
            wisePlayer.setPlayEndListener(onWisePlayerListener);
            wisePlayer.setSeekEndListener(onWisePlayerListener);

            wisePlayer.setBookmark(currentTime);
            wisePlayer.setVideoType(0);
            wisePlayer.setCycleMode(1);
            wisePlayer.setView(surfaceView);

            Bundle data = networkQoeService.getRealTimeData();
            int dLBandwidth = -1;
            if(data != null)
                dLBandwidth= data.getInt("dLBandwidth" + 0);

            String videoUrl;
            final String dLBandwidthValue = String.valueOf(dLBandwidth);

            //values for test value does not mean anything
            if (dLBandwidth > 15000) {
                videoUrl = getString(R.string.high_quality_url);
                dLBandwidthText.setText(getString(R.string.high_quality_description, dLBandwidthValue));
            }
            else if (dLBandwidth > 10000) {
                videoUrl = getString(R.string.good_quality_url);
                dLBandwidthText.setText(getString(R.string.good_quality_description, dLBandwidthValue));
            }
            else if (dLBandwidth > 5000) {
                videoUrl = getString(R.string.normal_quality_url);
                dLBandwidthText.setText(getString(R.string.normal_quality_description, dLBandwidthValue));
            }
            else { // poor quality as default
                videoUrl = getString(R.string.poor_quality_url);
                dLBandwidthText.setText(getString(R.string.poor_quality_description, dLBandwidthValue));
            }

            wisePlayer.setPlayUrl(videoUrl);
            wisePlayer.ready();
        }
        else {
            Log.e(TAG, "wisePlayer == null");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        wisePlayer.pause();
        currentTime = wisePlayer.getCurrentTime();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkQoeService.destroyService();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        //Do nothing!
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //Do nothing!
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //Do nothing!
    }

    @Override
    public boolean onError(WisePlayer wisePlayer, int i, int i1) {
        return false;
    }

    @Override
    public boolean onEvent(WisePlayer wisePlayer, int i, int i1, Object o) {
        return false;
    }

    @Override
    public void onLoadingUpdate(WisePlayer wisePlayer, int i) {
        //Do nothing!
    }

    @Override
    public void onStartPlaying(WisePlayer wisePlayer) {
        //Do nothing!
    }

    @Override
    public void onPlayEnd(WisePlayer wisePlayer) {
        //Do nothing!
    }

    @Override
    public void onReady(final WisePlayer wisePlayer) {
        Log.d(TAG, "onReady");
        this.wisePlayer = wisePlayer;
        this.wisePlayer.setView(surfaceView);
        this.wisePlayer.start();
    }

    @Override
    public void onResolutionUpdated(WisePlayer wisePlayer, int i, int i1) {
        //Do nothing!
    }

    @Override
    public void onSeekEnd(WisePlayer wisePlayer) {
        //Do nothing!
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        wisePlayer.setView(surfaceView);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        wisePlayer.setSurfaceChange();
        wisePlayer.setView(surfaceView);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        //Do nothing!
    }
}
