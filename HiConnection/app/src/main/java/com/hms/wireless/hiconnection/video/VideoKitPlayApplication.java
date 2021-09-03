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

import android.app.Application;
import android.util.Log;
import com.huawei.hms.videokit.player.InitFactoryCallback;
import com.huawei.hms.videokit.player.WisePlayerFactory;
import com.huawei.hms.videokit.player.WisePlayerFactoryOptions;

public class VideoKitPlayApplication extends Application {
    private static final String TAG = VideoKitPlayApplication.class.getSimpleName();
    private static WisePlayerFactory wisePlayerFactory = null;
    @Override
    public void onCreate() {
        super.onCreate();
        initPlayer();
    }

    private void initPlayer() {
        // DeviceId test is used in the demo.
        WisePlayerFactoryOptions factoryOptions = new WisePlayerFactoryOptions.Builder().setDeviceId("xxx").build();
        WisePlayerFactory.initFactory(this, factoryOptions, initFactoryCallback);
    }

    /**
     * Player initialization callback
     */
    private static InitFactoryCallback initFactoryCallback = new InitFactoryCallback() {
        @Override
        public void onSuccess(WisePlayerFactory wisePlayerFactory) {
            Log.i(TAG, "init player factory success");
            setWisePlayerFactory(wisePlayerFactory);
        }
        @Override
        public void onFailure(int errorCode, String reason) {
            Log.w(TAG, "init player factory failed :" + reason + ", errorCode is " + errorCode);
        }
    };

    /**
     * Get WisePlayer Factory
     *
     * @return WisePlayer Factory
     */
    public static WisePlayerFactory getWisePlayerFactory() {
        return wisePlayerFactory;
    }
    private static void setWisePlayerFactory(WisePlayerFactory wisePlayerFactory) {
        VideoKitPlayApplication.wisePlayerFactory = wisePlayerFactory;
    }
}
