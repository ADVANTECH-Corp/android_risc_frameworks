package android.os;

interface IAdvSdkService{
	
	/**
     * Show or hide back button on navigation bar 
     * @param show Flag to show / hide back icon
     */
    void showBackButton(boolean show);	
	boolean isBackButtonShow();
	
	/**
     * Show or hide home button on navigation bar 
     * @param show Flag to show / hide home icon
     */
    void showHomeButton(boolean show);
	boolean isHomeButtonShow();
	
    /**
     * Show or hide recent button on navigation bar 
     * @param show Flag to show / hide recent icon
     */
    void showRecentButton(boolean show);	
	boolean isRecentButtonShow();
	
	/**
     * Set navigation bar color
     * @param color navigation bar color
     */
    void setNavigationBarColor(int color);
	int getNavigationBarColor();
	
	/**
     * Show or hide quick settings menu on status bar 
     * @param show Flag to show / hide quick settings menu
     */
    void showQuickSettingMenu(boolean show);
	boolean isQuickSettingMenuShow();
	
	/**
     * Show or hide time icon on status bar 
     * @param show Flag to show / hide time icon
     */
    void showTimeIcon(boolean show);
	boolean isTimeIconShow();
	
	/**
     * Show or hide battery icon on status bar 
     * @param show Flag to show / hide battery icon
     */
    void showBatteryIcon(boolean show);
	boolean isBatteryIconShow();
	
	/**
     * Set status bar color
     * @param color status bar color
     */
    void setStatusBarColor(int color);
	int getStatusBarColor();
	
	/**
     * enable or disable wifi.
     * @param enabling enable or disable wifi
     */
    void enableWifi(boolean enabling);
	boolean isWifiEnabled();
	
	/**
     * enable or disable bluetooth.
     * @param enabling enable or disable bluetooth
     */
    void enableBluetooth(boolean enabling);
	boolean isBluetoothEnabled();
	
	/**
     * Start discovery bluetooth device nearby.
     */
    void startBluetoothDiscovery();
	
	/**
     * enable or disable airplane mode.
     * @param enabling enable or disable airplane mode
     */
    void enableAirplaneMode(boolean enabling);
	boolean isAirplaneModeEnabled();
	
	/**
     * enable or disable wifi tethering.
     * @param enabling enable or disable wifi tethering
     */
    void enableWifiTethering(boolean enabling);
	boolean isWifiTetheringEnabled();
	
	/**
     * Set default softap account information including user name.
     * and password 
     * @param username softap user name 
     * @param password softap user password 
     */
    void setSsidDefaultAccount(String username, String password);
	
	/**
     * enable or disable mobile data.
     * @param enabling enable or disable mobile data
     */
    void enableMobileData(boolean enabling);
	boolean isMobileDataEnabled();
	
	/**
     * enable or disable data roaming.
     * @param enabling enable or disable data roaming
     */
    void enableDataRoaming(boolean enabling);
	boolean isDataRoamingEnabled();
	
	/**
     * Set mobile network mode.
     * @param mode 4G/3G/2G
     */
    void setMobileNetworkMode(int mode);
	int getMobileNetworkMode();
	
    /**
     * Set brightness.
     * @param brightness 0-255
     */
    void setBrightness(int brightness);	
	int getBrightness();
	
	/**
     * Set screen timeout.
     * @param timeout screen timeout
     */
    void setScreenTimeout(int timeout);
	int getScreenTimeout();
	
	/**
     * Change system font size.
     * @param fontsize 4 level totally(0.85, 1.0, 1.15 and 1.30) 
     */
    void setFontSize(float fontsize);
	float getFontSize();
	
	/**
     * Set media volume.
     * @param volume 0-15
     */
    void setMediaVolume(int volume);
	int getMediaVolume();
	
	/**
     * Set alarm volume.
     * @param volume 0-7
     */
    void setAlarmVolume(int volume);
	int getAlarmVolume();
	
	/**
     * Set notification volume.
     * @param volume 0-7
     */
    void setNotificationVolume(int volume);
	int getNotificationVolume();
	
	/**
     * enable or disable lock screen notification .
     * @param enabling enable or disable lock screen notification
     */
    void enableLockScreenNotification(boolean enabling);
	boolean isLockScreenNotificationEnabled();
	
	/**
     * enable or disable application notification.
     * @param pkgname application package name
     * @param enabling enable or disable application notification
     */
    void enableApplicationNotification(String pkgname, boolean enabling);
	boolean isApplicationNotificationEnabled(String pkgname);
	
    /**
     * Enable or disable to enter app info in Settings application.
     * @param enabling enable or disable enter app info 
     */
    void enableManagedAppInfoPage(boolean enabling);
	boolean isManagedAppInfoPageEnabled();

    /**
     * Disable application.
     * @param pkgname the application package name.
     */
    void disableApplication(String pkgname);

    /**
     * Enable application.
     * @param pkgname the application package name.
     */
    void enableApplication(String pkgname);	
	
	/**
     * Update or install application from file.
     * @param path the updated file full path.
     */
    void updateApplication(String path);
	
	/**
     * Remove application.
     * @param pkgname the application package name.
     */
    void removeApplication(String pkgname);
	
	/**
     * Start application.
     * @param pkgname the application package name.
     */
    void startApplication(String pkgname);
	
	/**
     * Auto start application when Starting up.
     * @param pkgname the application package name.
     */
    void setAutoStartApplication(String pkgname);
    String getAutoStartApplication();
	
	
    /**
     * Enable or disable allow add user.
     * @param enabling enable or disable allow add user
     */
    void enableAllowAddUser(boolean enabling);
	boolean isAllowAddUserEnabled();
	
	/**
     * Enable or disable allow remove user.
     * @param enabling enable or disable allow remove user
     */
    void enableAllowRemoveUser(boolean enabling);
	boolean isAllowRemoveUserEnabled();
	
	/**
     * Get all users id.
     * @return String[] get all users id
     */
    String[] getUsersID();
	
	/**
     * Get all users name.
     * @return String[] get all users name
     */
    String[] getUsersName();
	
	/**
     * remove user.
     * @param userid user id
     */
    void removeUser(int userid);
	
	/**
     * Enable or disable location.
     * @param enabling enable or disable location
     */
    void enableLocation(boolean enabling);
	boolean isLocationEnabled();
	
    /**
     * Set location mode.
     * @param mode set location mode.
     */
    void setLocationMode(int mode);
	int getLocationMode();

    /**
     * Enable or disable lock screen.
     * @param enabling enable or disable lock screen
     */  
    void enableLockScreen(boolean enabling);
	boolean isLockScreenEnabled();
	
	/**
     * Set screen lock info.
     * @param info set screen lock info.
     */
    void setScreenLockInfo(String info);
	String getScreenLockInfo();
	
	/**
     * Enable or disable visible pattern when lock screen.
     * @param enabling enable or disable visible pattern
     */
    void enableVisiblePattern(boolean enabling);
	boolean isVisiblePatternEnabled();
	
	/**
     * Set screen lock timeout.
     * @param timeout set screen lock info timeout.
     */
    void setScreenLockTimeout(int timeout);
	int getScreenLockTimeout();
	
	/**
     * Enable or disable power button lock screen instantly.
     * @param enabling enable or disable power button lock screen instantly
     */
    void enablePowerInstantlyLock(boolean enabling);
	boolean isPowerInstantlyLockEnabled();
	
	/**
     * Enable or disable show password when entering password.
     * @param enabling enable or disable show password when entering password
     */
    void enableShowPassword(boolean enabling);
	boolean isShowPasswordEnabled();
	
	/**
     * Allow or disallow non-market application install.
     * @param enabling allow or disallow non-marke application install
     */
    void allowNonMarketAppsInstall(boolean enabling);
	boolean isNonMarketAppsInstallAllowed();
	
	/**
     * Set language.
     * @param languageTag set language.
     */
    void setLanguage(String languageTag);
	String getLanguage();
	
    /**
     * Get all languages name.
     * @return String[] all languages name list.
     */
    String[] getSystemAllLanguageName();

    /**
     * Get all languages tag.
     * @return String[] all languages tag list.
     */
    String[] getSystemAllLanguageTag();

    /**
     * Set pointer speed.
     * @param speed set pointer speed.
     */
    void setPointerSpeed(int speed);
	int getPointerSpeed();

	/**
     * Enable or disable factory reset on Settings application.
     * @param enabling enable or disable factory reset
     */
    void enableFactoryReset(boolean enabling);
	boolean isFactoryResetEnabled();
	
	/**
     * Enable or disable auto time.
     * @param enabling enable or disable auto time
     */
    void enableAutoTime(boolean enabling);
	boolean isAutoTimeEnabled();
	
	/**
     * Enable or disable auto time zone.
     * @param enabling enable or disable auto time zone
     */
    void enableAutoTimeZone(boolean enabling);
	boolean isAutoTimeZoneEnabled();
	
	/**
     * Set system date and time.
     * @param year Date of year
     * @param month Date of month
     * @param day Day of day
     * @param hour Time of hour
     * @param minute Time of minute
     * @param second Time of second
     */
    void setDateTime(int year, int month, int day, int hour, int minute, int second);
	String getDateTime();
	
	/**
     * Set time zone.
     * @param id time zone id.
     */
    void setTimeZone(String id);
	String getTimeZone();
	
	/**
     * Get all time zone ids.
     * @return String[] time zone ids.
     */
    String[] getAllTimeZoneIDs();
	
	/**
     * Get all time zone names.
     * @return String[] all time zone names.
     */
    String[] getAllTimeZoneNames();
	
	/**
     * Set 12/24 hour format.
     * @param is24Hour 12/24 hour format.
     */
    void set24HourFormat(boolean is24Hour);
	boolean is24HourFormat();
	
	/**
     * Enable or disable large text.
     * @param enabling enable or disable large text
     */
    void enableLargeText(boolean enabling);
	boolean isLargeTextEnabled();
	
	/**
     * Enable or disable high contrast text.
     * @param enabling enable or disable high contrast text
     */
    void enableTextHighContrast(boolean enabling);
	boolean isTextHighContrastEnabled();
	
	/**
     * Enable or disable auto screen rotation.
     * @param enabling enable or disable auto screen rotation
     */
    void enableAutoScreenRotation(boolean enabling);
	boolean isAutoScreenRotationEnabled();
	
	/**
     * Set screen rotation.
     * @param rotation set screen rotation
     */
    void setScreenRotation(int rotation);
	int getScreenRotation();
	
	/**
     * Enable or disable screen color inversion.
     * @param enabling enable or disable screen color inversion
     */
    void enableColorInversion(boolean enabling);
	boolean isColorInversionEnabled();
	
	/**
     * Enable or disable developer options.
     * @param enabling enable or disable developer options
     */
    void enableDeveloperOptions(boolean enabling);
	boolean isDeveloperOptionsEnabled();
	
	/**
     * Enable or disable stay awake when charging.
     * @param enabling enable or disable stay awake when charging
     */
    void enableStayAwakeForCharging(boolean enabling);
	boolean isStayAwakeForChargingEnabled();
	
	/**
     * Enable or disable usb debug.
     * @param enabling enable or disable usb debug
     */
    void enableAdb(boolean enabling);
	boolean isAdbEnabled();
	
	/**
     * Set usb configuration option.
     * @param option set usb configuration option 
     */
    void setUsbConfigurationOption(String option);
	String getUsbConfigurationOption();
	
	/**
     * Change system window animation scale.
     * @param scale there are 6 levels (0, 0.5, 1, 1.5, 2, 5, 10)
     */    
    void setWindowAnimationScale(float scale);
	float getWindowAnimationScale();
	
	/**
     * Change system transition animation scale.
     * @param scale there are 6 levels (0, 0.5, 1, 1.5, 2, 5, 10)
     */    
    void setTransitionAnimationScale(float scale);
	float getTransitionAnimationScale();
	
	/**
     * Change system animator duration scale.
     * @param scale there are 6 levels (0, 0.5, 1, 1.5, 2, 5, 10)
     */    
    void setAnimatorDurationScale(float scale);
	float getAnimatorDurationScale();
	
	
}
