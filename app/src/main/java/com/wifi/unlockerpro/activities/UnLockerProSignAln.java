package com.wifi.unlockerpro.activities;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;

import java.util.ArrayList;

public class UnLockerProSignAln implements Comparable {
    private final String UnlockWifiId; // Mac address or BSSID
    private Integer UnlockWifilevel; // RSSI. -70...-30
    private Integer UnlockWifistrength; // Calculated strength based on RSSI. 0..100
    private final String UnlockWifiname; // SSID for wifi
    private final String UnlockWifitype; // Wifi
    private final ArrayList<Integer> UnlockWifihistory;

    public UnLockerProSignAln(ScanResult wifi) {
        this.UnlockWifiId = wifi.BSSID;
        this.UnlockWifilevel = wifi.level;
        this.UnlockWifistrength = WifiManager.calculateSignalLevel(wifi.level, 100);
        this.UnlockWifiname = wifi.SSID==null?"NA WiFi":wifi.SSID;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        }
        this.UnlockWifitype = "wifi";
        UnlockWifihistory = new ArrayList<>();
        UnlockWifihistory.add(this.UnlockWifilevel);
    }

    @Override
    public int compareTo(Object o) {
        UnLockerProSignAln otherSignaln = (UnLockerProSignAln) o;
        return otherSignaln.UnlockWifilevel - UnlockWifilevel;
    }

    @Override
    public String toString() {
        return UnlockWifitype + " - " + UnlockWifiname + " - " + UnlockWifistrength;
    }

    public void update(int level) {
        this.UnlockWifilevel = level;
        UnlockWifistrength = WifiManager.calculateSignalLevel(level, 100);
        UnlockWifihistory.add(level);
        while (UnlockWifihistory.size() > 21) {
            UnlockWifihistory.remove(0);
        }
    }

    @Override
    public boolean equals(Object obj) {
        UnLockerProSignAln otherSignaln = (UnLockerProSignAln) obj;
        return UnlockWifiId.equals(otherSignaln.UnlockWifiId);
    }

    public String getUnlockWifiid() {
        return UnlockWifiId;
    }

//    public ArrayList<Integer> getUnlockWifihistory() {
//        return UnlockWifihistory;
//    }

    public int getUnlockWifilevel() {
        return UnlockWifilevel;
    }

    public int getUnlockWifistrength() {
        return UnlockWifistrength;
    }

    public String getUnlockWifiname() {
        return UnlockWifiname;
    }

//    public String getUnlockWifivenue() {
//        return UnlockWifivenue;
//    }

//    public int getUnlockWififreq() {
//        return UnlockWififreq;
//    }

//    public String getUnlockWifitype() {
//        return UnlockWifitype;
//    }
}
