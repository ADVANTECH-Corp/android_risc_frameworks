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

public class AdvSdklib {
    private static final String TAG = "android.advsdklib.AdvSdklib";
    private Context mContext;

    //AdvSdklib init    
    public AdvSdklib(Context context) {
	this.mContext = context;
    }

    //AdvSdklib api implement
    public void showNavigationBar(Window win, boolean show){
        SystemProperties.set("persist.sb.immersive", show ? "2" : "1");
        View decorView = win.getDecorView();
        if(show){
            showSystemUI(win);
        } else {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }
    
    public void showStatusBar(Window win, boolean show){
        SystemProperties.set("persist.sb.immersive", show ? "2" : "1");
        View decorView = win.getDecorView();
        if(show){
            showSystemUI(win);
        } else {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }
    
    private void showSystemUI(Window win){
        View decorView = win.getDecorView();
        decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
