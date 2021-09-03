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

package com.hms.wireless.hiconnection.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.huawei.hms.common.ApiException;
import com.huawei.hms.wireless.IQoeCallBack;
import com.huawei.hms.wireless.IQoeService;
import com.huawei.hms.wireless.NetworkQoeClient;
import com.huawei.hms.wireless.WirelessClient;

public class NetworkQoeService {

    private static final int NETWORK_QOE_INFO_TYPE = 0;
    private static final String PACKAGE_IDENTIFIER = "com.hms.wireless.hiconnection";

    private static Bundle qoeInfoBundle = null; // this is for getting data after callback.

    private static final String TAG = "<!!!> networkQoeService";
    private Activity activity;
    private NetworkQoeClient networkQoeClient;
    private IQoeService qoeService;

    private ServiceConnection mSrcConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            qoeService = IQoeService.Stub.asInterface(service);
            Log.i(TAG, "onServiceConnected.");
            registerNetQoeCallBack();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            qoeService = null;
            Log.i(TAG, "onService DisConnected.  !!!!");
        }
    };

    private IQoeCallBack callBack = new IQoeCallBack.Stub() {
        @Override
        public void callBack(int type, Bundle qoeInfo) throws RemoteException {
            if (qoeInfo == null || type != NETWORK_QOE_INFO_TYPE) {//NETWORK_QOE_INFO_TYPE is 0
                Log.e(TAG, "callback failed.type:" + type);
                return;
            }
            Log.i(TAG, "callback not null");
            qoeInfoBundle = qoeInfo;
        }
    };

    public NetworkQoeService(Activity activity) {
        Log.i(TAG, "Constructor beginning");
        this.activity = activity;
        networkQoeClient = WirelessClient.getNetworkQoeClient(activity);
        getQoeClient();
    }

    public void destroyService() {
        unRegisterNetQoeCallBack();

        activity.unbindService(mSrcConn);
    }

    private void unRegisterNetQoeCallBack() {
        try {
            int ret =0;
            ret = qoeService.unRegisterNetQoeCallBack(PACKAGE_IDENTIFIER ,callBack);
            Log.i(TAG, "Register registerNetQoeCallBack ret: "+ret);
        } catch (Exception e) {
            Log.e(TAG, "Register registerNetQoeCallBack Exception: "+e.getMessage());
        }
    }

    private void registerNetQoeCallBack() {
        Log.i(TAG, "Register");
        if(qoeService != null) {
            Log.i(TAG, "Register qoeService != null");
            int ret = 0;
            try {
                ret = qoeService.registerNetQoeCallBack(PACKAGE_IDENTIFIER ,callBack);
                Log.i(TAG, "Register registerNetQoeCallBack ret: "+ret);
            } catch (Exception e) {
                Log.e(TAG, "Register registerNetQoeCallBack Exception: "+e.getMessage());
            }
        }
        else {
            Log.e(TAG, "Register qoeService null");
        }
    }

    private void getQoeClient() {
        Log.i(TAG, "getQoeClient beginning");
        if (networkQoeClient != null) {
            networkQoeClient.getNetworkQoeServiceIntent()
                    .addOnSuccessListener(wirelessResult -> {
                        Intent intent = wirelessResult.getIntent();
                        if (intent == null) {
                            Log.i(TAG, "getQoeClient intent is null.");
                            return;
                        }
                        Log.i(TAG, "getQoeClient intent is success.");
                        activity.bindService(intent, mSrcConn, Context.BIND_AUTO_CREATE);
                    })
                    .addOnFailureListener(exception -> {
                        if (exception instanceof ApiException) {
                            ApiException ex = (ApiException) exception;
                            int errCode = ex.getStatusCode();
                            Log.i(TAG, "getQoeClient intent failed:" + errCode);
                        }
                    });
        }
        else {
            Log.e(TAG, "getQoeClient else");
        }
    }

    public Bundle getRealTimeData() {
        Log.i(TAG, "GetRealTimeData beginning");
        if (qoeService != null) {
            try {
                Bundle qoeInfo = qoeService.queryRealTimeQoe(PACKAGE_IDENTIFIER);
                if (qoeInfo == null) {
                    Log.i(TAG, "queryRealTimeQoe is empty.");
                    return null;
                }
                Log.i(TAG, "GetRealTimeData not null returning data");
                return qoeInfo;

            } catch (RemoteException exception) {
                Log.e(TAG, "no unregisterNetQoeCallback api");
                return null;
            }
        }
        Log.i(TAG, "GetRealTimeData else");
        return null;
    }

    public static Bundle getQoeInfoBundle() {
        return qoeInfoBundle;
    }
}
