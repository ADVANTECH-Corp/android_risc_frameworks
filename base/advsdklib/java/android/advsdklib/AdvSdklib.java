package android.advsdklib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.AlarmManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.IPowerManager;
import android.os.PowerManager;
import android.os.Process;
import android.os.RecoverySystem;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.service.dreams.IDreamManager;
import android.util.Log;
import android.view.IWindowManager;
import android.view.View;
import android.view.Window;
import com.android.internal.widget.LockPatternUtils;
import android.net.wifi.ScanResult;
import java.util.Set;
import android.bluetooth.BluetoothAdapter;  
import android.bluetooth.BluetoothDevice;
import android.app.ActivityManager;  
import android.app.ActivityManager.MemoryInfo;
import java.util.Date;
import java.text.SimpleDateFormat;
import android.text.format.Time;
import android.content.BroadcastReceiver;
import android.content.IntentFilter; 
import android.os.BatteryManager;
import java.text.ParseException;

import android.content.pm.IPackageInstallObserver;

public class AdvSdklib {
    private static final String TAG = "android.advsdklib.AdvSdklib";
    private Context mContext;

	public class MemoryStatus {
        long totalMemory;
        long availMemory;
        
        public MemoryStatus() {
            totalMemory = 0;
            availMemory = 0;
        }
        public void initMemoryStatus() {
            totalMemory = 0;
            availMemory = 0;
        }
        public long getTotalMemory() {
            return totalMemory;
	}
        public long getAvailMemory() {
            return availMemory;
	}
    }
	public interface advCallback{
		void onSuccess(String data);
		void onError(String error);
	}
	
	public class BatteryStatus {
		float voltage;
		float temperature;
		int levelPercent;
		String status;
		String health;
		String plugged;
		public BatteryStatus() {
			voltage = 0;
			temperature = 0;
			levelPercent = 0;
			status = "";
			health = "";
			plugged = "";
		}
		public void initBatteryStatus() {
			voltage = 0;
			temperature = 0;
			levelPercent = 0;
			status = "";
			health = "";
			plugged = "";
		}
		public float getVoltage() {
	    return voltage;
		}
        public float getTemperature() {
            return temperature;
        }
        public int getLevelPercent() {
            return levelPercent;
        }
        public String getStatus() {
            return status;
        }
        public String getHealth() {
            return health;
        }
        public String getPlugged() {
            return plugged;
        }
    }
	
	private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override  
        public void onReceive(Context context, Intent intent) {  
            // TODO Auto-generated method stub    
            String action = intent.getAction();  
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {  
        	int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,-1);
		mBatteryStatus.voltage = (float)voltage/1000;
		int temperature =  intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,-1);
		mBatteryStatus.temperature = (float)temperature/10;
		int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
        	int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
		mBatteryStatus.levelPercent = (int)(((float)level/scale)*100);
		int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);  
                switch (health) {  
                    case BatteryManager.BATTERY_HEALTH_GOOD:  
                        mBatteryStatus.health = "Good";
			break;  
                    case BatteryManager.BATTERY_HEALTH_COLD:  
                        mBatteryStatus.health = "Cold";  
                        break;  
                    case BatteryManager.BATTERY_HEALTH_DEAD:  
                        mBatteryStatus.health = "Dead";  
                        break;  
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:  
                        mBatteryStatus.health = "Overheat";  
                        break;  
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:  
                        mBatteryStatus.health = "Over voltage";  
                        break;  
                    case BatteryManager.BATTERY_HEALTH_UNKNOWN:  
                        mBatteryStatus.health = "Unknown";  
                        break;  
                    case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:  
                        mBatteryStatus.health = "Unspecified failure";  
                        break;  
                    default:  
                        break;  
                }  
		int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);  
                switch (status) {  
                    case BatteryManager.BATTERY_STATUS_CHARGING:  
                        mBatteryStatus.status = "Charging";  
                        break;  
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:  
                        mBatteryStatus.status = "Discharging";  
                        break;  
                    case BatteryManager.BATTERY_STATUS_FULL:  
                        mBatteryStatus.status = "Full";  
                        break;  
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:  
                        mBatteryStatus.status = "Not charging";  
                        break;  
                    case BatteryManager.BATTERY_STATUS_UNKNOWN:  
                        mBatteryStatus.status = "Unknown";  
                        break;  
                    default:  
                        break;  
                }  
		int pluged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);  
                switch (pluged) {  
                    case BatteryManager.BATTERY_PLUGGED_AC:  
                        mBatteryStatus.plugged = "AC";  
                        break;  
                    case BatteryManager.BATTERY_PLUGGED_USB:  
                        mBatteryStatus.plugged = "USB";  
                        break;  
   		    case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                        mBatteryStatus.plugged = "WIRELESS";            
                        break;
                    default:  
                        break;  
                }  
	    } 
        }  
    };  
	
	public BatteryStatus mBatteryStatus = new BatteryStatus();
	
    //AdvSdklib init    
    public AdvSdklib(Context context) {
	this.mContext = context;
    }

	private class PackageInstallObserver extends IPackageInstallObserver.Stub {
		private final PackageInfo pginfo;
		private final advCallback callback;
		public PackageInstallObserver(PackageInfo info, advCallback advcallback) {
			pginfo = info;
			callback = advcallback;
		}
		@Override
		public void packageInstalled(String packageName, int returnCode) {
			String info;
			if (packageName != null && !packageName.equals(pginfo.packageName))  {
				info = "Package doesn't have expected package name.";
				callback.onError(info);
				return;
			}
			if (returnCode == PackageManager.INSTALL_SUCCEEDED) {	
				info = "Success";
				callback.onSuccess(info);
				return;
			} else if (returnCode == PackageManager.INSTALL_FAILED_VERSION_DOWNGRADE) {
				info = "Current version of " + pginfo.packageName + " higher than the version to be installed. It was not reinstalled.";
				callback.onError(info);
				return;
			} else {
				info = "Installing package " + pginfo.packageName + " failed.";
				callback.onError(info);
				return;
			}
		}
	}

	public void installApplicationAsync(String pathFile, advCallback callback){
		File apkFile = new File(pathFile);
		PackageManager mPackageManager = mContext.getPackageManager();
		PackageInfo info = mPackageManager.getPackageArchiveInfo(pathFile, PackageManager.GET_ACTIVITIES);
		mPackageManager.installPackage(Uri.fromFile(apkFile), new PackageInstallObserver(info, callback), PackageManager.INSTALL_REPLACE_EXISTING, null);
	}
	
    //AdvSdklib api implement
 /********************************************** UI ************************************************************/

/********************************** UI ---- Navigation Bar ********************************************/    
	/**
     * Show navigation bar.
     * @param win 
     * @param show Show navigation bar
     */
    public void showNavigationBar(Window win, boolean show){
        SystemProperties.set("persist.navbar", "" + show);
        View decorView = win.getDecorView();
        if(show){
            showSystemUI(win);
        } else {
            /*
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
            */
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }

    }
   
    public boolean isNavigationBarShow() {
	return SystemProperties.getBoolean("persist.navbar", true);
    }
    
/******************************************************************************************************/

/************************************** UI ---- Status Bar ********************************************/

    /**
     * Show status bar.
     * @param win 
     * @param show Show status bar
     */
    public void showStatusBar(Window win, boolean show){
        SystemProperties.set("persist.statusbar", "" + show);
        View decorView = win.getDecorView();
        if(show){
            showSystemUI(win);
        } else {
            /*
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
            */
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }

    }

    public boolean isStatusBarShow() {
		return SystemProperties.getBoolean("persist.statusbar", true);
    }
	
/******************************************************************************************************/

/************************************** UI ---- More **************************************************/

    /**
     * Set system kiosk mode.
     * @param win 
     * @param enabling enter kiosk mode
     */
    public void setKioskMode(Window win, boolean enabling){
        SystemProperties.set("persist.navbar", "" + !enabling);
		SystemProperties.set("persist.statusbar", "" + !enabling);
	View decorView = win.getDecorView();
        if(enabling){
            /*
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
            */
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
        } else {
            showSystemUI(win);
        }
    }

    public boolean isKioskModeSet() {
	return (!SystemProperties.getBoolean("persist.statusbar", true)) && (!SystemProperties.getBoolean("persist.navbar", true));
    }

/******************************************************************************************************/

/************************************** UI ---- private functions *************************************/

    /**
     * Show System UI.
     * @param win 
     */
    private void showSystemUI(Window win){
        View decorView = win.getDecorView();
/*
	Log.e("AdvSdk", "flags = 0x" + Integer.toHexString(decorView.getSystemUiVisibility()));
        decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
*/
        decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

/*****************************************************************************************************/
	
	/**
     * Get all wifi devices.
     * @return list wifi scan results
     */
    public List<ScanResult> getWifiScanResults() {
        WifiManager mWm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mWm.startScan();
        List<ScanResult> results = mWm.getScanResults();
        return results;
    }
	
	/**
     * Get all bonded bluetooth devices.
     * @return list bonded bluetooth devices
     */
    public Set<BluetoothDevice> getBluetoothBondedDevices() {
        BluetoothAdapter  mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter != null) {
			return mBluetoothAdapter.getBondedDevices();
		}
		return null;
    }
	
	/**
     * Start get battery status.
     */
    public void startGetBatteryStatus() {
        mBatteryStatus.initBatteryStatus();
        IntentFilter mFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED); 
        mContext.registerReceiver(mBatteryReceiver, mFilter);
    }
	
    /**
     * Stop get battery status.
     */
    public void stopGetBatteryStatus() {
        mBatteryStatus.initBatteryStatus();
        mContext.unregisterReceiver(mBatteryReceiver);
    }
	
	/**
     * Get battery status.
	 * @return BatteryStatus include battery voltage, temperature, levelPercent, status, health and plugged.
     */
    public BatteryStatus getBatteryStatus() {
        return mBatteryStatus;
    }
		
	/**
     * Get memory status.
     * @return MemoryStatus include total memory size and availible memory size.
     */
    public MemoryStatus getMemoryStatus() {
        MemoryStatus mMemoryStatus = new MemoryStatus();
        ActivityManager am = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        mMemoryStatus.totalMemory = mi.totalMem;
        mMemoryStatus.availMemory = mi.availMem;
        return mMemoryStatus;
    }
	
	public Time formatDateTime(String datetime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Time time = new Time();
        try {
        Date date = formatter.parse(datetime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        time.year = calendar.get(Calendar.YEAR);
        time.month = calendar.get(Calendar.MONTH);
        time.monthDay = calendar.get(Calendar.DAY_OF_MONTH);
        time.hour = calendar.get(Calendar.HOUR_OF_DAY);
        time.minute = calendar.get(Calendar.MINUTE);
        time.second = calendar.get(Calendar.SECOND);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

}
