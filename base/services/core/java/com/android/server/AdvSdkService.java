/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IAdvSdkService;
import android.net.LocalSocket;
import android.net.LocalServerSocket;
import android.net.LocalSocketAddress;
import android.util.Slog;
import android.os.Handler;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.content.BroadcastReceiver;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.content.Intent;
import android.view.WindowManager;
import android.view.View;
import android.os.SystemProperties;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.provider.Settings;
import android.content.ContentResolver;

import android.os.IPowerManager;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.app.StatusBarManager;
import android.net.wifi.WifiManager;

import android.view.Window;
import com.android.internal.widget.LockPatternUtils;
import android.os.ServiceManager;
import android.os.RemoteException;

public final class AdvSdkService extends IAdvSdkService.Stub {
    private static final String TAG = "AdvSdk";

    private static final boolean LOCAL_DEBUG = true;
    private final Context mContext;
    private final IPowerManager mPowerManager;

    //AdvSdkService init
    public AdvSdkService(Context context) {
	mContext = context;
	mPowerManager = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
	Slog.i(TAG, "AdvSdkService init...");
    }

    //AdvSdkService api implement
    public boolean setWifiTethering(boolean enable) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService("wifi");
	return wifiManager.setWifiApEnabled(null, enable);
    }

    public void setBrightness(int brightness) {
        try {
            mPowerManager.setTemporaryScreenBrightnessSettingOverride(brightness);
        } catch (RemoteException e) {
            Slog.i(TAG, "Failed to set screen brightness", e);
        }
        Settings.System.putIntForUser(mContext.getContentResolver(),"screen_brightness", brightness, -2);
   }

}
