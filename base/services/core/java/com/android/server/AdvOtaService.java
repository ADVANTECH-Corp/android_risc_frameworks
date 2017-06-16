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
import android.os.RecoverySystem;
import android.os.IAdvOtaService;
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

import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

//jinxin mask
//import org.apache.http.util.EncodingUtils;
import android.app.Activity;
import android.content.Intent;
import android.view.WindowManager;
import android.view.View;
import android.os.SystemProperties;

//jinxin mask
//import static android.view.WindowManager.LayoutParams.FLAG_SYSTEM_ERROR;
import android.app.NotificationManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.provider.Settings;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.content.pm.IPackageInstallObserver;
import android.net.Uri;
import android.os.RemoteException;
import android.os.PowerManager;
import android.os.SystemClock;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import android.content.pm.PackageInfo;
import android.content.pm.ApplicationInfo;

public final class AdvOtaService extends IAdvOtaService.Stub {
    private static final String TAG = "AdvOtaService";
    private static final String TEST_ACTION = "com.advantech.advfuntest.TEST_ACTION";
    private static final boolean LOCAL_DEBUG = true;//jinxin
    InputStream mIn;
    OutputStream mOut;
    LocalSocket mSocket;
    Context mContext = null;
    Thread serverThread = null;
    private PackageManager mPackageManager;
    NotificationManager notifymanager;
    //private ServerSocket serverSocket;
    private LocalServerSocket serverSocket;
    private static final int SERVERPORT = 6000;
    byte buf[] = new byte[1024];
    int buflen = 0;
    //for test
    private BroadcastReceiver testReceiver = new BroadcastReceiver(){
          public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("test-info");
            Slog.i(TAG, "broadcast receive: "+text);
          }
    };

    public enum Advaction {
    systembackup,wipedata,wipecache,install,appinstall;

    public static Advaction getAdvaction(String advaction){
       return valueOf(advaction.toLowerCase());
    	}
    }

    class AppInfo {  
    	private int versionCode = 0;    
    	// 
    	private String appName = "";   
    	// 
    	private String packageName = "";   
    	private String versionName = "";  
    } 

    //service init
    public AdvOtaService(Context context) {
	mContext = context;
	Slog.i(TAG, "AdvOtaService init...");
	mPackageManager = mContext.getPackageManager();
	//notifymanager =(NotificationManager)getSystemService(Service.NOTIFICATION_SERVICE);

	final IntentFilter userFilter = new IntentFilter();
        userFilter.addAction(TEST_ACTION);
	mContext.registerReceiver(testReceiver, userFilter, null, null);

	//startup main thread
	this.serverThread = new Thread(new ServerThread());
	this.serverThread.start();
	
    }

    //jinxin added for android6.0 apk ota
    private class PackageInstallObserver extends IPackageInstallObserver.Stub {
        private final PackageInfo pginfo;

        public PackageInstallObserver(PackageInfo info) {
            pginfo = info;
        }
	//i try to send notification, but ...
	/*
	public void sendNotification(String notifyinfo) {
   	    NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
            .setContentTitle("AdvOta Service Notify")
            .setContentText(notifyinfo);
   	    notifymanager.notify(1, builder.build());
	}
	*/	
        @Override
        public void packageInstalled(String packageName, int returnCode) {
            String notifyinfo;

	    String retstr;
	    File RESULT_FILE = new File("/cache", "appstatus");
	    //RESULT_FILE.delete();

	    if (packageName != null && !packageName.equals(pginfo.packageName))  {
                notifyinfo = "Package doesn't have expected package name.";
		//sendNotification(notifyinfo);
		Slog.i(TAG, notifyinfo);
		retstr = "NOK";
            }
	    //	
            if (returnCode == PackageManager.INSTALL_SUCCEEDED) {
		notifyinfo = "Package " + pginfo.packageName + " is succesfully installed.";
		//sendNotification(notifyinfo);
               	Slog.i(TAG, notifyinfo);
		retstr = "OK";
            } else if (returnCode == PackageManager.INSTALL_FAILED_VERSION_DOWNGRADE) {
		notifyinfo = "Current version of " + pginfo.packageName + " higher than the version to be installed. It was not reinstalled.";
		//sendNotification(notifyinfo);
                Slog.i(TAG, notifyinfo);
		retstr = "NOK";
            } else {
		notifyinfo = "Installing package " + pginfo.packageName + " failed.";
		//sendNotification(notifyinfo);
               	Slog.i(TAG, notifyinfo);
                Slog.i(TAG, "Errorcode returned by IPackageInstallObserver = " + returnCode);
		retstr = "NOK";
            }

	    // apk ota status to  /cache/appstatus
            try {
		FileWriter command = new FileWriter(RESULT_FILE);
           	command.write(retstr);
             	command.close();
            } catch (IOException e) {
            	Slog.w(TAG, "Failed to write to /cache/appstatus", e);
            }
	}
    }

    class ServerThread implements Runnable {
	public void run() {
		//Socket socket = null;
	    	LocalSocket socket = null; 	
		try {
			serverSocket = new LocalServerSocket("ota_server");
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (!Thread.currentThread().isInterrupted()) {

			try {
				Slog.i(TAG, "AdvOtaService now accept...");
				socket = serverSocket.accept();
				Slog.i(TAG, "AdvOtaService accepted");
				/*
				CommunicationThread commThread = new CommunicationThread(socket);
				new Thread(commThread).start();
				*/
				if(socket != null){
					Slog.e(TAG, "Accept one client");
					startTestThread(socket);	
				}
				else{
					Slog.i(TAG, "Failed accept a client");
					return;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
    }
   
    private void startTestThread(final LocalSocket socket){
	Thread t = new Thread(){
		@Override 
		public void run(){
			try{
				InputStream is = socket.getInputStream();
				OutputStream os = socket.getOutputStream();
				InputStreamReader isr = new InputStreamReader(is);
				while(true){//
					char [] data = new char[256];
					int readBytes = isr.read(data);
					if(readBytes != -1){
						String tempStr = new String(data, 0, readBytes);
						Slog.i(TAG, "recv str: " + tempStr + " len is :" + readBytes);
						String newstr = tempStr.trim();
						Slog.i(TAG, "newstr: " + newstr);
						String[] asplit = newstr.split("#");
						Slog.i(TAG, "asplit[0]: " + asplit[0]);
						//RecoverySystem.rebootWipeCache(mContext);
						switch(Advaction.getAdvaction(asplit[0])){	
						case systembackup:
							Slog.i(TAG, "switch systembackup");
							RecoverySystem.rebootSystemBackup(mContext);
							break;
						case wipedata:
							Slog.i(TAG, "switch wipedata");
							RecoverySystem.rebootWipeUserData(mContext);
							break;
						case wipecache:
							Slog.i(TAG, "swtich wipecache");
							RecoverySystem.rebootWipeCache(mContext);
							break;
						case install:
							Slog.i(TAG, "switch install");
							String file = asplit[1].trim();
							Slog.i(TAG, "install file: " + file);
							File recoveryFile = new File(file);
                					if(!recoveryFile.exists()){
								Slog.i(TAG, "install, file not exist");
								return;
							}

                					// first verify package
							/*
         						try {
                 						mWakelock.acquire();
                 						RecoverySystem.verifyPackage(recoveryFile, recoveryVerifyListener, null);
         						} catch (IOException e1) {
                 						reportInstallError(OTAStateChangeListener.ERROR_PACKAGE_VERIFY_FALIED);
                 						e1.printStackTrace();
                 						return;
         						} catch (GeneralSecurityException e1) {
                 						reportInstallError(OTAStateChangeListener.ERROR_PACKAGE_VERIFY_FALIED);
                 						e1.printStackTrace();
                 						return;
         						} finally {
                 						mWakelock.release();
         						}
							*/
							RecoverySystem.installPackage(mContext, recoveryFile);
							break;
						case appinstall:
							Slog.i(TAG, "switch appinstall");
                                                        String strfile = asplit[1].trim();
                                                        String newVersionName, newpackageName;
                                                        String oldVersionName = null;
							int  newVersionCode = 0, oldVersionCode = 0;
						    	int willinstall = 0;	
							//get apk info 
							PackageInfo info = mPackageManager.getPackageArchiveInfo(strfile, PackageManager.GET_ACTIVITIES);
							if(info != null){
								ApplicationInfo appInfo = info.applicationInfo;
								newpackageName = appInfo.packageName;
								newVersionName = info.versionName;
								newVersionCode = info.versionCode;
								Slog.i(TAG, "ApkName: "+newpackageName+"; versionName: "+newVersionName+"; versionCode: "+Integer.toString(newVersionCode));	
								
								//get installed package info
                                                        	List<PackageInfo> packages = mPackageManager.getInstalledPackages(0);
                                                       	 	for(int i=0;i<packages.size();i++) {
                                                                	PackageInfo packageInfo = packages.get(i);
                                                               	 	AppInfo tmpInfo =new AppInfo();
                                                                	tmpInfo.appName = packageInfo.applicationInfo.loadLabel(mPackageManager).toString();
                                                                	tmpInfo.packageName = packageInfo.packageName;
                                                                	tmpInfo.versionName = packageInfo.versionName;
                                                                	tmpInfo.versionCode = packageInfo.versionCode;
                                                                	Slog.i(TAG, "Name: "+tmpInfo.packageName+"; versionName: "+tmpInfo.versionName+"; versionCode: "+Integer.toString(tmpInfo.versionCode));
                                                        		if(tmpInfo.packageName.equals(newpackageName)){
										oldVersionName = tmpInfo.versionName;
										oldVersionCode = tmpInfo.versionCode;
										break;
									}
								}
                                                        	//get installed package info end
								if(oldVersionName == null){
									Slog.i(TAG, "get installed apk info failed, this apk may not installed before");
									willinstall = 1;
								}
							}
							else{
								Slog.i(TAG, "getPackageArchiveInfo failed, may be invalid apk file");
								return;
							}
							//get apk info end
						
							Slog.i(TAG, "install apk name: " + strfile);
							File apkfile = new File(strfile);
							// willinstall == 1 means first install
							// newVersionCode >= oldVersionCode means ota
							if(willinstall == 1 || newVersionCode >= oldVersionCode){
								//truely need install the apk
								Slog.i(TAG, "now begin install the apk "+strfile);
								mPackageManager.installPackage(Uri.fromFile(apkfile), new PackageInstallObserver(info), PackageManager.INSTALL_REPLACE_EXISTING, null);
							}
							break;
						default:
							Slog.i(TAG, "error paramer");
							break;		
						}
						/*					
						Intent intent = new Intent();
                				intent.setAction(TEST_ACTION);
                				intent.putExtra("test-info", tempStr);
                				mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
						*/
					}
					else
						Slog.i(TAG, "recv error");
					Slog.i(TAG, "finished");
					return;
				}
			}catch (IOException e){
				e.printStackTrace();	
			}
			
		}
	};
	t.start();
    }
	
}
