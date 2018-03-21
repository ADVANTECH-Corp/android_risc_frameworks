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
import android.os.Bundle;
import android.os.IAdvSdkService;
import android.net.LocalSocket;
import android.net.LocalServerSocket;
import android.net.LocalSocketAddress;
import android.os.Handler;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;

import android.os.PowerManager;
import android.os.SystemClock;

import android.view.Window;
import com.android.internal.widget.LockPatternUtils;
import android.os.ServiceManager;
import android.os.RemoteException;

import android.view.WindowManager;
import android.graphics.Color;
import android.provider.Settings;
import android.content.res.Configuration;
import android.app.ActivityManagerNative;
import android.app.StatusBarManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.IPackageDeleteObserver;
import android.app.PackageInstallObserver;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.content.ContentResolver;
import android.net.wifi.ScanResult;
import java.util.List;
import java.util.Set;
import android.bluetooth.BluetoothAdapter;  
import android.bluetooth.BluetoothDevice;  
import android.content.BroadcastReceiver;
import android.content.IntentFilter; 
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import java.io.File;
import android.net.Uri;
import android.os.BatteryManager;
import android.location.GpsSatellite;  
import android.location.GpsStatus;  
import android.location.Location;  
import android.location.LocationListener;  
import android.location.LocationManager;
import java.util.ArrayList;  
import java.util.Iterator;
import android.os.UserHandle;
import java.util.Locale;
import android.util.DisplayMetrics;
import android.hardware.input.InputManager;
import android.app.AlarmManager;
import java.util.TimeZone;
import android.text.format.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import android.text.format.Time;
import java.text.ParseException;
import com.android.internal.view.RotationPolicy;
import android.hardware.usb.UsbManager;
import android.os.UserManager;
import android.content.pm.UserInfo;
import android.app.INotificationManager;
import android.os.SystemProperties;
import android.app.ActivityManager;  
import android.app.ActivityManager.MemoryInfo; 
import android.view.View;
import android.util.Slog;
import android.os.IPowerManager;
import android.view.IWindowManager;
import android.util.Log;
import android.os.Binder;
import android.os.IBinder;

import com.android.internal.widget.LockPatternUtils;
import com.android.internal.app.LocalePicker;
import com.android.internal.statusbar.IStatusBarService;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DEFAULT;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
public final class AdvSdkService extends IAdvSdkService.Stub {
    private static final String TAG = "AdvSdk";
    private static final boolean LOCAL_DEBUG = true;
    
    
	private final Context mContext;	
	private final StatusBarManager mStatusBarManager;
	private final Object mUserLock = new Object();
	
	
    //AdvSdkService init
    public AdvSdkService(Context context) {
	mContext = context;
	mStatusBarManager = (StatusBarManager)mContext.getSystemService("statusbar");
	Slog.i(TAG, "AdvSdkService init...");
    }

    //AdvSdkService api implement
 /********************************************** UI ************************************************************/

/********************************** UI ---- Navigation Bar ********************************************/

    /**
     * Show or hide back button on navigation bar 
     * @param show Flag to show / hide back icon
     */
    public void showBackButton(boolean show){
        int flags = getButtonAndIconStatus();
	showButtonAndIcon(flags);
	SystemProperties.set("persist.navbar.back", "" + show);
	showButtonAndIcon(show ? flags & (~StatusBarManager.DISABLE_BACK) : flags | StatusBarManager.DISABLE_BACK);
    }

    public boolean isBackButtonShow() {
        return SystemProperties.getBoolean("persist.navbar.back", true);
    }


    /**
     * Show or hide home button on navigation bar 
     * @param show Flag to show / hide home icon
     */
    public void showHomeButton(boolean show){
        int flags = getButtonAndIconStatus();
	showButtonAndIcon(flags);
        SystemProperties.set("persist.navbar.home", "" + show);
        showButtonAndIcon(show ? flags & (~StatusBarManager.DISABLE_HOME) : flags | StatusBarManager.DISABLE_HOME);
    }

    public boolean isHomeButtonShow() {
        return SystemProperties.getBoolean("persist.navbar.home", true);
    }


    /**
     * Show or hide recent button on navigation bar 
     * @param show Flag to show / hide recent icon
     */
    public void showRecentButton(boolean show){
        int flags = getButtonAndIconStatus();
	showButtonAndIcon(flags);
        SystemProperties.set("persist.navbar.recent", "" + show);
        showButtonAndIcon(show ? flags & (~StatusBarManager.DISABLE_RECENT) : flags | StatusBarManager.DISABLE_RECENT);
    }

    public boolean isRecentButtonShow() {
        return SystemProperties.getBoolean("persist.navbar.recent", true);
    }


    /**
     * Set navigation bar color
     * @param color navigation bar color
     */
    public void setNavigationBarColor(int color){
        int flags = getButtonAndIconStatus();
	showButtonAndIcon(flags);
        SystemProperties.set("persist.navbar.color", "" + color);
        flags = (flags & (~StatusBarManager.DISABLE_SEARCH)) | (flags ^ StatusBarManager.DISABLE_SEARCH);
	showButtonAndIcon(flags); 
        if((flags & StatusBarManager.DISABLE_SEARCH)  != 0)
            SystemProperties.set("persist.navbar.search", "1");
        else
            SystemProperties.set("persist.navbar.search", "0");
    }

    public int getNavigationBarColor(){
        return SystemProperties.getInt("persist.navbar.color", 0);
    }
/******************************************************************************************************/

/************************************** UI ---- Status Bar ********************************************/

    /**
     * Show or hide quick settings menu on status bar 
     * @param show Flag to show / hide quick settings menu
     */
    public void showQuickSettingMenu(boolean show) {
        //int flags = getButtonAndIconStatus();
	//showButtonAndIcon(flags);
        //SystemProperties.set("persist.sb.ic.qsm", "" + show);
        //showButtonAndIcon(show ? flags & (~StatusBarManager.DISABLE_EXPAND) : flags | StatusBarManager.DISABLE_EXPAND);
        SystemProperties.set("persist.qsm", "" + show);
    }

    public boolean isQuickSettingMenuShow() {
        //return SystemProperties.getBoolean("persist.sb.ic.qsm", true);
        return SystemProperties.getBoolean("persist.qsm", true);
    }


    /**
     * Show or hide time icon on status bar 
     * @param show Flag to show / hide time icon
     */
    public void showTimeIcon(boolean show) {
        int flags = getButtonAndIconStatus();
	showButtonAndIcon(flags);
        SystemProperties.set("persist.sb.ic.time", "" + show);
        showButtonAndIcon(show ? flags & (~StatusBarManager.DISABLE_CLOCK) : flags | StatusBarManager.DISABLE_CLOCK);
    }

    public boolean isTimeIconShow() {
        return SystemProperties.getBoolean("persist.sb.ic.time", true);
    }


    /**
     * Show or hide battery icon on status bar 
     * @param show Flag to show / hide battery icon
     */
    public void showBatteryIcon(boolean show) {
        int flags = getButtonAndIconStatus();
	showButtonAndIcon(flags);
        SystemProperties.set("persist.sb.ic.battery", "" + show);
	flags = (flags & (~StatusBarManager.DISABLE_SEARCH)) | (flags ^ StatusBarManager.DISABLE_SEARCH);
        showButtonAndIcon(flags); 
        if((flags & StatusBarManager.DISABLE_SEARCH)  != 0)
            SystemProperties.set("persist.navbar.search", "1");
        else
            SystemProperties.set("persist.navbar.search", "0");

    }

    public boolean isBatteryIconShow() {
        return SystemProperties.getBoolean("persist.sb.ic.battery", true);
    }


    /**
     * Set status bar color
     * @param color status bar color
     */
    public void setStatusBarColor(int color) {
        int flags = getButtonAndIconStatus();
	showButtonAndIcon(flags);
        SystemProperties.set("persist.statusbar.color", "" + color);
        flags = (flags & (~StatusBarManager.DISABLE_SEARCH)) | (flags ^ StatusBarManager.DISABLE_SEARCH);
        showButtonAndIcon(flags); 
        if((flags & StatusBarManager.DISABLE_SEARCH)  != 0)
            SystemProperties.set("persist.navbar.search", "1");
        else
            SystemProperties.set("persist.navbar.search", "0");
    }

    public int getStatusBarColor(){
        return SystemProperties.getInt("persist.statusbar.color", 0);
    }

/******************************************************************************************************/

/************************************** UI ---- More **************************************************/

/******************************************************************************************************/

/************************************** UI ---- private functions *************************************/

    /**
     * Show or hide button on navigation bar / quick settings menu or time icon on status bar
     * @param what Flag to show / hide icon type
     */
    private void showButtonAndIcon(int what){
        mStatusBarManager.disable(what);
    }

    private int getButtonAndIconStatus() {
	int flags = StatusBarManager.DISABLE_NONE;
        if (!SystemProperties.getBoolean("persist.navbar.back", true)) {
                flags |= StatusBarManager.DISABLE_BACK;
        }
        if (!SystemProperties.getBoolean("persist.navbar.home", true)) {
                flags |= StatusBarManager.DISABLE_HOME;
        }
        if (!SystemProperties.getBoolean("persist.navbar.recent", true)) {
                flags |= StatusBarManager.DISABLE_RECENT;
        }
        //if (!SystemProperties.getBoolean("persist.sb.ic.qsm", true)) {
        //        flags |= StatusBarManager.DISABLE_EXPAND;
        //}
        if (!SystemProperties.getBoolean("persist.sb.ic.time", true)) {
                flags |= StatusBarManager.DISABLE_CLOCK;
        }
	if (SystemProperties.getInt("persist.navbar.search", 0) == 1) {
                flags |= StatusBarManager.DISABLE_SEARCH;
        }

	return flags;
    }

/******************************************************************************************************/

/*********************************************** UI End *****************************************************/

/*************************************** Wireless&Networks Start ********************************************/

/***************************** Wireless&Networks ---- Wifi ********************************************/

    /**
     * enable or disable wifi.
     * @param enabling enable or disable wifi
     */
    public void enableWifi(boolean enabling) {
        WifiManager mWm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mWm.setWifiEnabled(enabling);
    }

    public boolean isWifiEnabled() {
        WifiManager mWm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        return mWm.isWifiEnabled();
    }


/*****************************************************************************************************/

/***************************** Wireless&Networks ---- Bluetooth **************************************/

    /**
     * enable or disable bluetooth.
     * @param enabling enable or disable bluetooth
     */
    public void enableBluetooth(boolean enabling) {
        BluetoothAdapter  mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();        
        if(mBluetoothAdapter != null) {
			if(enabling)
				mBluetoothAdapter.enable();
			else
				mBluetoothAdapter.disable();
		}
    }

    public boolean isBluetoothEnabled() {
        BluetoothAdapter  mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter != null) {
             return mBluetoothAdapter.isEnabled();
        }
        return false;
    }

    /**
     * Start discovery bluetooth device nearby.
     */
    public void startBluetoothDiscovery() {
        BluetoothAdapter  mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter != null) {
			mBluetoothAdapter.startDiscovery();
		}
    }


/*****************************************************************************************************/

/******************************** Wireless&Networks ---- More ****************************************/

    /**
     * enable or disable airplane mode.
     * @param enabling enable or disable airplane mode
     */
    public void enableAirplaneMode(boolean enabling) {
        Settings.Global.putInt(mContext.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON,
                                enabling ? 1 : 0);
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", enabling);
        mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    public boolean isAirplaneModeEnabled() {
        return Settings.Global.getInt(mContext.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }


    /**
     * enable or disable wifi tethering.
     * @param enabling enable or disable wifi tethering
     */
    public void enableWifiTethering(boolean enabling) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService("wifi");
        wifiManager.setWifiApEnabled(null, enabling);
    }

    public boolean isWifiTetheringEnabled() {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService("wifi");
            return wifiManager.isWifiApEnabled();
    }

    /**
     * Set default softap account information including user name.
     * and password 
     * @param username softap user name 
     * @param password softap user password 
     */
    public void setSsidDefaultAccount(String username, String password) {
		SystemProperties.set("persist.setting.wifi.apname", username);
		SystemProperties.set("persist.setting.wifi.appassword", password);
		WifiManager mWifiManager = (WifiManager) mContext.getSystemService("wifi");
		WifiConfiguration mWifiConfig = new WifiConfiguration();
		mWifiConfig.SSID = username;
		mWifiConfig.allowedKeyManagement.set(KeyMgmt.WPA2_PSK);
		mWifiConfig.apBand = 0;
		mWifiConfig.preSharedKey = password;
		if(mWifiManager.getWifiApState() == WifiManager.WIFI_AP_STATE_ENABLED) {
				mWifiManager.setWifiApEnabled(null, false);
				mWifiManager.setWifiApEnabled(mWifiConfig, true);
		} else {
			mWifiManager.setWifiApConfiguration(mWifiConfig);
		}
	}


    /**
     * enable or disable mobile data.
     * @param enabling enable or disable mobile data
     */
    public void enableMobileData(boolean enabling) {
        TelephonyManager mTelephonyManager =
            (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.setDataEnabled(enabling);
    }

    public boolean isMobileDataEnabled() {
        TelephonyManager mTelephonyManager =
            (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyManager.getDataEnabled();
    }


    /**
     * enable or disable data roaming.
     * @param enabling enable or disable data roaming
     */
    public void enableDataRoaming(boolean enabling) {
        Settings.Global.putInt(mContext.getContentResolver(), Settings.Global.DATA_ROAMING, enabling ? 1 : 0);
    }

    public boolean isDataRoamingEnabled() {
        return Settings.Global.getInt(mContext.getContentResolver(), Settings.Global.DATA_ROAMING, 0) != 0;
    }

    
    /**
     * Set mobile network mode.
     * @param mode 4G/3G/2G
     */
    public void setMobileNetworkMode(int mode) {
        Settings.Global.putInt(mContext.getContentResolver(),
                android.provider.Settings.Global.PREFERRED_NETWORK_MODE,
                mode);
    }

    public int getMobileNetworkMode() {
        return Settings.Global.getInt(mContext.getContentResolver(),
                android.provider.Settings.Global.PREFERRED_NETWORK_MODE,
                0);
    }

/*****************************************************************************************************/

/****************************************** Wireless&Networks End *****************************************/

/****************************************** Device Start ****************************************************/

/****************************************** Device ---- Display ***************************************/

    /**
     * Set brightness.
     * @param brightness 0-255
     */
    public void setBrightness(int brightness) {
        if (brightness < 0 || brightness > 255) {
	    Slog.e(TAG, "Failed to set screen brightness, illegal argument");
	    return;
	}
        IPowerManager mPowerManager = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
	try {
            mPowerManager.setTemporaryScreenBrightnessSettingOverride(brightness);
        } catch (RemoteException e) {
            Slog.i(TAG, "Failed to set screen brightness", e);
        }
        Settings.System.putIntForUser(mContext.getContentResolver(),"screen_brightness", brightness, -2);
    }

    public int getBrightness() {
        try {
           return Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
           e.printStackTrace();
        }
        return 0;
    }

    /**
     * Set screen timeout.
     * @param timeout screen timeout
     */
    public void setScreenTimeout(int timeout) {
	if (timeout < 15000 || timeout > 1800000) {
            Slog.e(TAG, "Failed to set screen timeout, illegal argument");
            return;
        }
        Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout);
    }

    public int getScreenTimeout() {
	return Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 30000);
    }


    /**
     * Change system font size.
     * @param fontsize 4 level totally(0.85, 1.0, 1.15 and 1.30) 
     */
    public void setFontSize(float fontsize) {
	if (fontsize < 0.85 || fontsize > 1.30) {
            Slog.e(TAG, "Failed to set font size, illegal argument");
            return;
        }
	Configuration mCurConfig = new Configuration();
	try {
	    mCurConfig.updateFrom(ActivityManagerNative.getDefault().getConfiguration());
	    mCurConfig.fontScale = fontsize;
	    ActivityManagerNative.getDefault().updatePersistentConfiguration(mCurConfig);
        } catch (RemoteException e) {
            Log.w(TAG, "Unable to set font size");
        }
    }

    public float getFontSize() {
        float fontsize = 0;
	Configuration mCurConfig = new Configuration();
        try {
            mCurConfig.updateFrom(ActivityManagerNative.getDefault().getConfiguration());
            fontsize = mCurConfig.fontScale;
        } catch (RemoteException e) {
            Log.w(TAG, "Unable to get font size");
        }
	return fontsize;
    }

/*****************************************************************************************************/

/************************************** Device ---- Sound&notification *******************************/

    /**
     * Set media volume.
     * @param volume 0-15
     */
    public void setMediaVolume(int volume) {
        if (volume < 0 || volume > 15) {
            Slog.e(TAG, "Failed to set media volume, illegal argument");
            return;
        }
        AudioManager mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_PLAY_SOUND);
}

    public int getMediaVolume() {
        AudioManager mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
}


    /**
     * Set alarm volume.
     * @param volume 0-7
     */
    public void setAlarmVolume(int volume) {
        if (volume < 0 || volume > 7) {
            Slog.e(TAG, "Failed to set alarm volume, illegal argument");
            return;
        }
        AudioManager mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume, AudioManager.FLAG_PLAY_SOUND);
}

    public int getAlarmVolume() {
        AudioManager mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        return mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
}


    /**
     * Set notification volume.
     * @param volume 0-7
     */
    public void setNotificationVolume(int volume) {
        if (volume < 0 || volume > 7) {
            Slog.e(TAG, "Failed to set notification volume, illegal argument");
            return;
        }
        AudioManager mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, volume, AudioManager.FLAG_PLAY_SOUND);
}

    public int getNotificationVolume() {
        AudioManager mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        return mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
}


    /**
     * enable or disable lock screen notification .
     * @param enabling enable or disable lock screen notification
     */
    public void enableLockScreenNotification(boolean enabling) {
        Settings.Secure.putInt(mContext.getContentResolver(),
            Settings.Secure.LOCK_SCREEN_SHOW_NOTIFICATIONS, enabling ? 1 : 0);
    }

    public boolean isLockScreenNotificationEnabled() {
        return Settings.Secure.getInt(mContext.getContentResolver(), Settings.Secure.LOCK_SCREEN_SHOW_NOTIFICATIONS, 0) != 0;
    }


    /**
     * enable or disable application notification.
     * @param pkgname application package name
     * @param enabling enable or disable application notification
     */
    public void enableApplicationNotification(String pkgname, boolean enabling) {
        int mUserId = UserHandle.myUserId();
        INotificationManager sINM = INotificationManager.Stub.asInterface(
            ServiceManager.getService(Context.NOTIFICATION_SERVICE));
        try {
            sINM.setNotificationsEnabledForPackage(pkgname, mUserId, enabling);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public boolean isApplicationNotificationEnabled(String pkgname) {
        int mUserId = UserHandle.myUserId();
        INotificationManager sINM = INotificationManager.Stub.asInterface(
            ServiceManager.getService(Context.NOTIFICATION_SERVICE));
        try {
            return sINM.areNotificationsEnabledForPackage(pkgname, mUserId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;

    }

/*****************************************************************************************************/

/***************************************** Device ---- Apps ******************************************/

    /**
     * Enable or disable to enter app info in Settings application.
     * @param enabling enable or disable enter app info 
     */
    public void enableManagedAppInfoPage(boolean enabling) {
        SystemProperties.set("persist.setting.manageapp", "" + enabling);
    }

    public boolean isManagedAppInfoPageEnabled() {
        return SystemProperties.getBoolean("persist.setting.manageapp", true);
    }

    /**
     * Disable application.
     * @param pkgname the application package name.
     */
    public void disableApplication(String pkgname) {
	if((pkgname == null)||(pkgname.equals(""))){
		Slog.e(TAG, "Failed to disable application, empty argument");
		return ;
	}
	try{
                PackageManager mPackageManager = this.mContext.getPackageManager();
                PackageInfo mPackageInfo = mPackageManager.getPackageInfo( pkgname,
				PackageManager.GET_UNINSTALLED_PACKAGES );
		if (mPackageInfo != null ){
			if (mPackageManager.getApplicationEnabledSetting(pkgname) == PackageManager.COMPONENT_ENABLED_STATE_DISABLED){
				Slog.e(TAG, "application is disable");
				return ;
			}
			mPackageManager.setApplicationEnabledSetting(pkgname, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
		}
	}
	catch (NameNotFoundException e)	{
		Slog.e(TAG, "Failed to disable application, illegal argument");
		e.printStackTrace();
	}

    }

    /**
     * Enable application.
     * @param pkgname the application package name.
     */
    public void enableApplication(String pkgname) {
	if((pkgname == null)||(pkgname.equals(""))){
		Slog.e(TAG, "Failed to enable application, empty argument");
		return ;
	}
	try{
                PackageManager mPackageManager = this.mContext.getPackageManager();
                PackageInfo mPackageInfo = mPackageManager.getPackageInfo( pkgname,
				PackageManager.GET_UNINSTALLED_PACKAGES );
		if (mPackageInfo != null ){
			if (mPackageManager.getApplicationEnabledSetting(pkgname) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED){
				Slog.e(TAG, "application is enable");
				return ;
			}
			mPackageManager.setApplicationEnabledSetting(pkgname, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
		}
	}
	catch (NameNotFoundException e)	{
		Slog.e(TAG, "Failed to enable application, illegal argument");
		e.printStackTrace();
	}
    }


    /**
     * Update or install application from file.
     * @param path the updated file full path.
     */
    public void updateApplication(String path) {
/*
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW"); 
        intent.setDataAndType(Uri.fromFile(new File(path)),"application/vnd.android.package-archive");
        mContext.startActivity(intent);
*/
	if((path == null)||(path.equals(""))){
		Slog.e(TAG, "Failed to update/install application, empty argument");
		return ;
	}

	int installFlags = 0;
	PackageManager mPackageManager = this.mContext.getPackageManager();
	PackageInstallObserver observer = new PackageInstallObserver();
	mPackageManager.installPackage(Uri.fromFile(new File(path)), observer, installFlags, null);
    }

    /**
     * Remove application.
     * @param pkgname the application package name.
     */
    public void removeApplication(String pkgname) {
/*
        Uri uri = Uri.parse("package:" + pkgname);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        mContext.startActivity(intent);
*/
	if((pkgname == null)||(pkgname.equals(""))){
		Slog.e(TAG, "Failed to remove application, empty argument");
		return ;
	}
	try{
                PackageManager mPackageManager = this.mContext.getPackageManager();
                PackageInfo mPackageInfo = mPackageManager.getPackageInfo( pkgname,
                                PackageManager.GET_UNINSTALLED_PACKAGES );
                if (mPackageInfo != null ){
			mPackageManager.deletePackage(pkgname, null, 0);
		}
	}
        catch (NameNotFoundException e) {
                Slog.e(TAG, "Failed to remove application, illegal argument");
                e.printStackTrace();
        }

    }

    /**
     * Start application.
     * @param pkgname the application package name.
     */
    public void startApplication(String pkgname) {
	if((pkgname == null)||(pkgname.equals(""))){
		Slog.e(TAG, "Failed to start application, empty argument");
		return ;
	}
	try{
		PackageManager mPackageManager = this.mContext.getPackageManager();
		PackageInfo mPackageInfo = mPackageManager.getPackageInfo( pkgname,
				PackageManager.GET_UNINSTALLED_PACKAGES );
		if (mPackageInfo != null ){
			Slog.i(TAG, "Application = " + pkgname + "    ApplicationEnabledSetting = " + mPackageManager.getApplicationEnabledSetting(pkgname));
			if (mPackageManager.getApplicationEnabledSetting(pkgname) == PackageManager.COMPONENT_ENABLED_STATE_DISABLED || mPackageManager.getApplicationEnabledSetting(pkgname) == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER){
				Slog.e(TAG, "Failed to start application, application is disabled");
				return ;
			}
		
			Intent intent = new Intent();
			intent = mPackageManager.getLaunchIntentForPackage(pkgname);
			if(intent == null) { 
				Slog.e(TAG, "Failed to start application, application does not contain the main activity");
				return ;
			}
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			mContext.startActivity(intent);
		}
	}
	catch (NameNotFoundException e)	{
		Slog.e(TAG, "Failed to start application, illegal argument");
		e.printStackTrace();
	}

    }


    /**
     * set auto start application when Starting up.
     * @param pkgname the application package name.
     */
    public void setAutoStartApplication(String pkgname) {
	SystemProperties.set("persist.app.autostartapp", pkgname);
/*
    	if( pkgname != null)
        {
            Intent newintent = new Intent();
            PackageManager mPackageManager = this.mContext.getPackageManager();
            newintent = mPackageManager.getLaunchIntentForPackage(pkgname);
            if(newintent != null) {
	        newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(newintent);
	    }
        }
*/
    }

    public String getAutoStartApplication() {
        return SystemProperties.get("persist.app.autostartapp", "");
    }

/*****************************************************************************************************/

/***************************************** Device ---- Battery ***************************************/

/*****************************************************************************************************/

/***************************************** Device ---- Memory ****************************************/

/*****************************************************************************************************/

/***************************************** Device ---- Users *****************************************/

    /**
     * Enable or disable allow add user.
     * @param enabling enable or disable allow add user
     */
    public void enableAllowAddUser(boolean enabling) {
        UserManager um = (UserManager) mContext.getSystemService(Context.USER_SERVICE);
        um.setUserRestriction(UserManager.DISALLOW_ADD_USER, !enabling);
    }

    public boolean isAllowAddUserEnabled() {
        UserManager um = (UserManager) mContext.getSystemService(Context.USER_SERVICE);
        return !um.hasUserRestriction(UserManager.DISALLOW_ADD_USER);
    }


    /**
     * Enable or disable allow remove user.
     * @param enabling enable or disable allow remove user
     */
    public void enableAllowRemoveUser(boolean enabling) {
        UserManager um = (UserManager) mContext.getSystemService(Context.USER_SERVICE);
        um.setUserRestriction(UserManager.DISALLOW_REMOVE_USER, !enabling);
    }

    public boolean isAllowRemoveUserEnabled() {
        UserManager um = (UserManager) mContext.getSystemService(Context.USER_SERVICE);
        return !um.hasUserRestriction(UserManager.DISALLOW_REMOVE_USER);
    }


    /**
     * Get all users id.
     * @return String[] get all users id
     */
    public String[] getUsersID() {
        UserManager um = (UserManager) mContext.getSystemService(Context.USER_SERVICE);
        List<UserInfo> users = um.getUsers();
        String mUsersID[] = new String[users.size()];
        int i = 0;
        for (UserInfo info : users) {
			mUsersID[i] = String.valueOf(info.id);
            i++;
        }
        return mUsersID;
    }

    /**
     * Get all users name.
     * @return String[] get all users name
     */
    public String[] getUsersName() {
        UserManager um = (UserManager) mContext.getSystemService(Context.USER_SERVICE);
        List<UserInfo> users = um.getUsers();
        String mUsersName[] = new String[users.size()];
        int i = 0;
        for (UserInfo info : users) {
			mUsersName[i] = info.name;
            i++;
        }
        return mUsersName;
    }

    /**
     * remove user.
     * @param userid user id
     */
    public void removeUser(final int userid) {
        final UserManager um = (UserManager) mContext.getSystemService(Context.USER_SERVICE);
        if (userid == UserHandle.myUserId()) {
            try {
                ActivityManagerNative.getDefault().switchUser(UserHandle.USER_OWNER);
                um.removeUser(userid);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            new Thread() {
                public void run() {
                    synchronized (mUserLock) {
                        um.removeUser(userid);
                    }
                }
            }.start();
        }
    }

/*****************************************************************************************************/

/***************************************** Device End *******************************************************/

/******************************************** Personal Start ************************************************/

/*************************************** Personal ---- Location **************************************/


    /**
     * Enable or disable location.
     * @param enabling enable or disable location
     */
    public void enableLocation(boolean enabling) {
        if(!enabling) {
	    Settings.Secure.putInt(mContext.getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
	}
	else {
	    int mode = Settings.Secure.getInt(mContext.getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
	    if(mode == Settings.Secure.LOCATION_MODE_OFF) {
                Settings.Secure.putInt(mContext.getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
            }
	    else { 
		Settings.Secure.putInt(mContext.getContentResolver(), Settings.Secure.LOCATION_MODE, mode);
	    }
	}
    }

    public boolean isLocationEnabled() {
	int mode = Settings.Secure.getInt(mContext.getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
	if(mode == Settings.Secure.LOCATION_MODE_OFF) {
	    return false;
	} 
	else {
	    return true;
	}	
    }


    /**
     * Set location mode.
     * @param mode set location mode.
     */
    public void setLocationMode(int mode) {
        if (mode < 1 || mode > 3) {
            Slog.e(TAG, "Failed to set location mode, illegal argument");
            return;
        }
        Settings.Secure.putInt(mContext.getContentResolver(), Settings.Secure.LOCATION_MODE, mode);
    }

    public int getLocationMode() {
        return  Settings.Secure.getInt(mContext.getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
    }


/*****************************************************************************************************/

/*************************************** Personal ---- Security **************************************/

    /**
     * Enable or disable lock screen.
     * @param enabling enable or disable lock screen
     */  
    public void enableLockScreen(boolean enabling) {
	LockPatternUtils mLockPatternUtils = new LockPatternUtils(this.mContext);
	if(enabling) {
	    mLockPatternUtils.setLockScreenDisabled(false, UserHandle.myUserId());
	}else {
            mLockPatternUtils.clearLock(UserHandle.myUserId());
            mLockPatternUtils.setLockScreenDisabled(true, UserHandle.myUserId());
	}
    }

    public boolean isLockScreenEnabled() {
        LockPatternUtils mLockPatternUtils = new LockPatternUtils(this.mContext);
	return !mLockPatternUtils.isLockScreenDisabled(UserHandle.myUserId());
    }


    /**
     * Set screen lock info.
     * @param info set screen lock info.
     */
    public void setScreenLockInfo(String info) {
        LockPatternUtils mLockPatternUtils = new LockPatternUtils(mContext);
        int mUserId = UserHandle.myUserId();
        mLockPatternUtils.setOwnerInfoEnabled(!info.isEmpty(), mUserId);
        mLockPatternUtils.setOwnerInfo(info, mUserId);

    }

    public String getScreenLockInfo() {
        LockPatternUtils mLockPatternUtils = new LockPatternUtils(mContext);
        int mUserId = UserHandle.myUserId();
        return mLockPatternUtils.getOwnerInfo(mUserId);
    }


    /**
     * Enable or disable visible pattern when lock screen.
     * @param enabling enable or disable visible pattern
     */
    public void enableVisiblePattern(boolean enabling) {
        LockPatternUtils mLockPatternUtils = new LockPatternUtils(mContext);
        int mUserId = UserHandle.myUserId();
        mLockPatternUtils.setVisiblePatternEnabled(enabling, mUserId);
    }
    
    public boolean isVisiblePatternEnabled() {
        LockPatternUtils mLockPatternUtils = new LockPatternUtils(mContext);
        int mUserId = UserHandle.myUserId();
        return mLockPatternUtils.isVisiblePatternEnabled(mUserId);
    }


    /**
     * Set screen lock timeout.
     * @param timeout set screen lock info timeout.
     */
    public void setScreenLockTimeout(int timeout) {
        if (timeout < 0 || timeout > 1800000) {
            Slog.e(TAG, "Failed to set screen lock timeout, illegal argument");
            return;
        }
        Settings.Secure.putInt(mContext.getContentResolver(),
                Settings.Secure.LOCK_SCREEN_LOCK_AFTER_TIMEOUT, timeout);


    }

    public int getScreenLockTimeout() {
        return Settings.Secure.getInt(mContext.getContentResolver(),
                Settings.Secure.LOCK_SCREEN_LOCK_AFTER_TIMEOUT, 5000);
    }


    /**
     * Enable or disable power button lock screen instantly.
     * @param enabling enable or disable power button lock screen instantly
     */
    public void enablePowerInstantlyLock(boolean enabling) {
        LockPatternUtils mLockPatternUtils = new LockPatternUtils(mContext);
        int mUserId = UserHandle.myUserId();
        mLockPatternUtils.setPowerButtonInstantlyLocks(enabling, mUserId);
    }

    public boolean isPowerInstantlyLockEnabled() {
        LockPatternUtils mLockPatternUtils = new LockPatternUtils(mContext);
        int mUserId = UserHandle.myUserId();
        return mLockPatternUtils.getPowerButtonInstantlyLocks(mUserId);
    }


    /**
     * Enable or disable show password when entering password.
     * @param enabling enable or disable show password when entering password
     */
    public void enableShowPassword(boolean enabling) {
        LockPatternUtils mLockPatternUtils = new LockPatternUtils(mContext);
        int mUserId = UserHandle.myUserId();
        Settings.System.putInt(mContext.getContentResolver(), Settings.System.TEXT_SHOW_PASSWORD,(enabling ? 1 : 0));
        mLockPatternUtils.setVisiblePasswordEnabled(enabling, mUserId);

    }

    public boolean isShowPasswordEnabled() {
        return Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.TEXT_SHOW_PASSWORD, 1) != 0;
    }

    
    /**
     * Allow or disallow non-market application install.
     * @param enabling allow or disallow non-marke application install
     */
    public void allowNonMarketAppsInstall(boolean enabling) {
        final UserManager um = (UserManager)mContext.getSystemService(Context.USER_SERVICE);
        if (um.hasUserRestriction(UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES)) {
            return;
        }
        Settings.Global.putInt(mContext.getContentResolver(), Settings.Global.INSTALL_NON_MARKET_APPS,enabling ? 1 : 0);
    }

    public boolean isNonMarketAppsInstallAllowed() {
        return Settings.Global.getInt(mContext.getContentResolver(),
            Settings.Global.INSTALL_NON_MARKET_APPS, 0) > 0;
    }

/*****************************************************************************************************/

/*********************************** Personal ---- Language&input ************************************/

    /**
     * Set language.
     * @param languageTag set language.
     */
    public void setLanguage(String languageTag) {
        Locale mLanguage = Locale.forLanguageTag(languageTag);
        LocalePicker.updateLocale(mLanguage);
    }

    public String getLanguage() {
	Locale currentLocale = Locale.getDefault();
	return currentLocale.toLanguageTag();
    }

    /**
     * Get all languages name.
     * @return String[] all languages name list.
     */
    public String[] getSystemAllLanguageName() {
	List<LocalePicker.LocaleInfo> locales = LocalePicker.getAllAssetLocales(mContext, true);
	String mLanguageName[] = new String[locales.size()];
	int i = 0;
	for (LocalePicker.LocaleInfo locale : locales) {
	    mLanguageName[i] = locale.toString();
	    i++;
        }
	return mLanguageName;
    }

    /**
     * Get all languages tag.
     * @return String[] all languages tag list.
     */
    public String[] getSystemAllLanguageTag() {
        List<LocalePicker.LocaleInfo> locales = LocalePicker.getAllAssetLocales(mContext, true);
        String mLanguageTag[] = new String[locales.size()];
        int i = 0;
        for (LocalePicker.LocaleInfo locale : locales) {
            mLanguageTag[i] = locale.getLocale().toLanguageTag();
            i++;
        }
        return mLanguageTag;
    }

    /**
     * Set pointer speed.
     * @param speed set pointer speed.
     */
    public void setPointerSpeed(int speed) {
        if (speed < -7 || speed > 7) {
            Slog.e(TAG, "Failed to set pointer speed, illegal argument");
            return;
        }
	InputManager mIm = (InputManager) mContext.getSystemService(Context.INPUT_SERVICE);
	mIm.tryPointerSpeed(speed);
	mIm.setPointerSpeed(mContext,speed);
    }
   
    public int getPointerSpeed() {
	InputManager mIm = (InputManager) mContext.getSystemService(Context.INPUT_SERVICE);
	return mIm.getPointerSpeed(mContext);
    }

/*****************************************************************************************************/

/*********************************** Personal ---- Backup&reset **************************************/

    /**
     * Enable or disable factory reset on Settings application.
     * @param enabling enable or disable factory reset
     */
    public void enableFactoryReset(boolean enabling) {
       UserManager.get(this.mContext).setUserRestriction("no_factory_reset",!enabling);
    }

    public boolean isFactoryResetEnabled() {
	return !UserManager.get(this.mContext).hasUserRestriction("no_factory_reset");
    }
/*****************************************************************************************************/

/******************************************Personal End ***************************************************/

/******************************************System Start ***************************************************/

/*************************************** System ---- Date&time ***************************************/

    /**
     * Enable or disable auto time.
     * @param enabling enable or disable auto time
     */
    public void enableAutoTime(boolean enabling) {
        Settings.Global.putInt(mContext.getContentResolver(), Settings.Global.AUTO_TIME, enabling ? 1 : 0);
    }

    public boolean isAutoTimeEnabled() {
	return Settings.Global.getInt(mContext.getContentResolver(), Settings.Global.AUTO_TIME, 0) > 0;
    }


    /**
     * Enable or disable auto time zone.
     * @param enabling enable or disable auto time zone
     */
    public void enableAutoTimeZone(boolean enabling) {
        Settings.Global.putInt(mContext.getContentResolver(), Settings.Global.AUTO_TIME_ZONE, enabling ? 1 : 0);
    }

    public boolean isAutoTimeZoneEnabled() {
	return Settings.Global.getInt(mContext.getContentResolver(), Settings.Global.AUTO_TIME_ZONE, 0) > 0;
    }


    /**
     * Set system date and time.
     * @param year Date of year
     * @param month Date of month
     * @param day Day of day
     * @param hour Time of hour
     * @param minute Time of minute
     * @param second Time of second
     */
    public void setDateTime(int year, int month, int day, int hour, int minute, int second) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day, hour, minute, second);
        AlarmManager am = (AlarmManager) this.mContext.getSystemService("alarm");
        am.setTime(c.getTimeInMillis());
    }

    public String getDateTime() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }

    
    /**
     * Set time zone.
     * @param id time zone id.
     */
    public void setTimeZone(String id) {
        AlarmManager alarm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarm.setTimeZone(id);
    }

    public String getTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        return tz.getID();
    }

    /**
     * Get all time zone ids.
     * @return String[] time zone ids.
     */
    public String[] getAllTimeZoneIDs() {
        return TimeZone.getAvailableIDs();
    }

    /**
     * Get all time zone names.
     * @return String[] all time zone names.
     */
    public String[] getAllTimeZoneNames() {
        String IDs[] = TimeZone.getAvailableIDs();
        String Names[] = new String[IDs.length];
        for( int i = 0; i < IDs.length; i++) {
            TimeZone tz = TimeZone.getTimeZone(IDs[i]);
            Names[i] = IDs[i] + " (" + tz.getDisplayName(false, TimeZone.SHORT) + ")";
        }
        return Names;
    }
    
    
    /**
     * Set 12/24 hour format.
     * @param is24Hour 12/24 hour format.
     */
    public void set24HourFormat(boolean is24Hour) {
        Settings.System.putString(mContext.getContentResolver(), Settings.System.TIME_12_24, is24Hour? "24" : "12");
		Intent timeChanged = new Intent(Intent.ACTION_TIME_CHANGED);
		timeChanged.putExtra(Intent.EXTRA_TIME_PREF_24_HOUR_FORMAT, is24Hour);
		mContext.sendBroadcast(timeChanged);
    }

    public boolean is24HourFormat() {
        return DateFormat.is24HourFormat(mContext);
    }

/*****************************************************************************************************/

/*********************************** System ---- Accessibility ***************************************/

    /**
     * Enable or disable large text.
     * @param enabling enable or disable large text
     */
    public void enableLargeText(boolean enabling) {
        Configuration mCurConfig = new Configuration();
        try {
            mCurConfig.updateFrom(ActivityManagerNative.getDefault().getConfiguration());
            mCurConfig.fontScale = enabling ? 1.3f : 1;
            ActivityManagerNative.getDefault().updatePersistentConfiguration(mCurConfig);
        } catch (RemoteException re) {
            /* ignore */
        }
    }

    public boolean isLargeTextEnabled() {
        Configuration mCurConfig = new Configuration();
        try {
            mCurConfig.updateFrom(ActivityManagerNative.getDefault().getConfiguration());
            return mCurConfig.fontScale == 1.3f;
        } catch (RemoteException re) {
            /* ignore */
        }
        return false;
    }


    /**
     * Enable or disable high contrast text.
     * @param enabling enable or disable high contrast text
     */
    public void enableTextHighContrast(boolean enabling) {
        Settings.Secure.putInt(mContext.getContentResolver(),
                Settings.Secure.ACCESSIBILITY_HIGH_TEXT_CONTRAST_ENABLED,
                (enabling ? 1 : 0));
    }

    public boolean isTextHighContrastEnabled() {
        return Settings.Secure.getInt(mContext.getContentResolver(),Settings.Secure.ACCESSIBILITY_HIGH_TEXT_CONTRAST_ENABLED, 0) == 1;
    }


    /**
     * Enable or disable auto screen rotation.
     * @param enabling enable or disable auto screen rotation
     */
    public void enableAutoScreenRotation(boolean enabling) {
        //RotationPolicy.setRotationLockForAccessibility(mContext,
        //        !enabling);
	Settings.System.putInt(mContext.getContentResolver(),Settings.System.ACCELEROMETER_ROTATION,enabling ? 1 : 0);
	//RotationPolicy.setRotationLock(false, 0);
    }

    public boolean isAutoScreenRotationEnabled() {
        return !RotationPolicy.isRotationLocked(mContext);
	//return !Settings.System.getIntForUser(mContext.getContentResolver(),
        //        Settings.System.ACCELEROMETER_ROTATION, 0, UserHandle.USER_CURRENT) == 0;
    }


    /**
     * Set screen rotation.
     * @param rotation set screen rotation
     */
    public void setScreenRotation(int rotation){
        if (rotation < 0 || rotation > 3) {
            Slog.e(TAG, "Failed to set screen rotation, illegal argument");
            return;
        }
	//Settings.System.putInt(mContext.getContentResolver(),Settings.System.ACCELEROMETER_ROTATION,0);
        //Settings.System.putInt(mContext.getContentResolver(),Settings.System.USER_ROTATION,rotation);
	//RotationPolicy.setRotationLock(true, rotation);
	Settings.System.putInt(mContext.getContentResolver(),Settings.System.USER_ROTATION,rotation);

    }
    
    public int getScreenRotation() {
        return  Settings.System.getInt(mContext.getContentResolver(), Settings.System.USER_ROTATION, 0);
    }

    /**
     * Enable or disable screen color inversion.
     * @param enabling enable or disable screen color inversion
     */
    public void enableColorInversion(boolean enabling) {
        Settings.Secure.putInt(mContext.getContentResolver(),
                Settings.Secure.ACCESSIBILITY_DISPLAY_INVERSION_ENABLED, (enabling ? 1 : 0));
    }

    public boolean isColorInversionEnabled() {
        return Settings.Secure.getInt(mContext.getContentResolver(),
                Settings.Secure.ACCESSIBILITY_DISPLAY_INVERSION_ENABLED, 0) == 1;
    }

/*****************************************************************************************************/

/******************************** System ---- Developer options **************************************/

    /**
     * Enable or disable developer options.
     * @param enabling enable or disable developer options
     */
    public void enableDeveloperOptions(boolean enabling) {
        Settings.Global.putInt(mContext.getContentResolver(),
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, enabling ? 1 : 0);
    }

    public boolean isDeveloperOptionsEnabled() {
	return Settings.Global.getInt(mContext.getContentResolver(),
	    Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) != 0;
    }

    /**
     * Enable or disable stay awake when charging.
     * @param enabling enable or disable stay awake when charging
     */
    public void enableStayAwakeForCharging(boolean enabling) {
	Settings.Global.putInt(mContext.getContentResolver(),
	    Settings.Global.STAY_ON_WHILE_PLUGGED_IN, enabling ?
            (BatteryManager.BATTERY_PLUGGED_AC | BatteryManager.BATTERY_PLUGGED_USB) : 0);
    }
    
    public boolean isStayAwakeForChargingEnabled() {
	return Settings.Global.getInt(mContext.getContentResolver(),
	    Settings.Global.STAY_ON_WHILE_PLUGGED_IN, 0) != 0;
    }

    /**
     * Enable or disable usb debug.
     * @param enabling enable or disable usb debug
     */
    public void enableAdb(boolean enabling) {
	Settings.Global.putInt(mContext.getContentResolver(),
	    Settings.Global.ADB_ENABLED, enabling ? 1 : 0);
    }

    public boolean isAdbEnabled() {
	return Settings.Global.getInt(mContext.getContentResolver(), Settings.Global.ADB_ENABLED, 0) != 0;
    }

    /**
     * Set usb configuration option.
     * @param option set usb configuration option 
     */
    public void setUsbConfigurationOption(String option) {
        UsbManager mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        mUsbManager.setCurrentFunction(option);
        if (option.equals("none")) {
            mUsbManager.setUsbDataUnlocked(false);
        } else {
            mUsbManager.setUsbDataUnlocked(true);
        }
    }

    public String getUsbConfigurationOption() {
		String option = "";
		UsbManager mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        if (mUsbManager.isFunctionEnabled("none")) {
            option = "none";
        }else if (mUsbManager.isFunctionEnabled("mtp")){
			option = "mtp";
		}else if (mUsbManager.isFunctionEnabled("ptp")){
			option = "ptp";
		}else if (mUsbManager.isFunctionEnabled("rndis")){
			option = "rndis";
		}else if (mUsbManager.isFunctionEnabled("audio_source")){
			option = "audio_source";
		}else if (mUsbManager.isFunctionEnabled("midi")){
			option = "midi";
		}
		return option;
	}


    /**
     * Change system window animation scale.
     * @param scale there are 6 levels (0, 0.5, 1, 1.5, 2, 5, 10)
     */    
    public void setWindowAnimationScale(float scale) {
        if (scale < 0 || scale > 10) {
            Slog.e(TAG, "Failed to set window animation scale, illegal argument");
            return;
        }
        IWindowManager mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        try {
            mWindowManager.setAnimationScale(0, scale);
        } catch (RemoteException e) {
            Log.w(TAG, "Unable to write scale size");
        }
    }

    public float getWindowAnimationScale() {
        float scale = 0;
        IWindowManager mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        try {
            scale = mWindowManager.getAnimationScale(0);
        } catch (RemoteException e) {
            Log.w(TAG, "Unable to get scale size");
        }
        return scale;
    }
    
    /**
     * Change system transition animation scale.
     * @param scale there are 6 levels (0, 0.5, 1, 1.5, 2, 5, 10)
     */    
    public void setTransitionAnimationScale(float scale) {
        if (scale < 0 || scale > 10) {
            Slog.e(TAG, "Failed to set transition animation scale, illegal argument");
            return;
        }
        IWindowManager mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        try {
            mWindowManager.setAnimationScale(1, scale);
        } catch (RemoteException e) {
            Log.w(TAG, "Unable to write scale size");
        }
    }

    public float getTransitionAnimationScale() {
        float scale = 0;
        IWindowManager mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        try {
            scale = mWindowManager.getAnimationScale(1);
        } catch (RemoteException e) {
            Log.w(TAG, "Unable to get scale size");
        }
        return scale;
    }

    /**
     * Change system animator duration scale.
     * @param scale there are 6 levels (0, 0.5, 1, 1.5, 2, 5, 10)
     */    
    public void setAnimatorDurationScale(float scale) {
        if (scale < 0 || scale > 10) {
            Slog.e(TAG, "Failed to set animation duration scale, illegal argument");
            return;
        }
        IWindowManager mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        try {
            mWindowManager.setAnimationScale(2, scale);
        } catch (RemoteException e) {
            Log.w(TAG, "Unable to write scale size");
        }
    }

    public float getAnimatorDurationScale() {
        float scale = 0;
        IWindowManager mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        try {
            scale = mWindowManager.getAnimationScale(2);
        } catch (RemoteException e) {
            Log.w(TAG, "Unable to get scale size");
        }
        return scale;
    }

}
