package android.os;

interface IAdvSdkService{
    /**
    * Enable/Disable Bluetooth
    */
    //void setBluetoothEnabled(boolean enabled);

    /**
    * Enable/Disable Wifi
    */
    //void setWifiEnabled(boolean enabled);

    /**
    * Enable/Disable Airplane
    */
    //void setAirplaneEnabled(boolean enabled);

    /**
    * Show/Hide StatusbarDropdown
    */
    //void ShowStatusBarDropdown(boolean enabled);
    
    /**
    * Show/Hide StatusBar
    */
    //void showStatusBar(boolean show);
    
    /**
    * Show/Hide NavigationBar
    */
    //void showNavigationBar(boolean show);
    
    boolean setWifiTethering(boolean enable);
    void setBrightness(int brightness);

}
