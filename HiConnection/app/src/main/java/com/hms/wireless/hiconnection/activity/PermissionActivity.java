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

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class PermissionActivity extends AppCompatActivity {

    private String[] permissionsToBeGrantedAtRuntime = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, permissionsToBeGrantedAtRuntime, 1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int permissionsNeeded = permissionsToBeGrantedAtRuntime.length;
        int permissionsGranted = 0;

        for (int i = 0;  i < permissions.length ; i++){
            if (permissions[i].equals(permissionsToBeGrantedAtRuntime[i]) && grantResults[i] == 0){
                permissionsGranted++;
            }

            if (permissionsGranted == permissionsNeeded){
                Intent intent = new Intent(this, MainActivity.class);
                this.startActivity(intent);
            }
        }
        this.finish();
    }
}
