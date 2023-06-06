package com.wifi.unlockerpro.activities;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import androidx.core.app.ActivityCompat;

public class UnLockerProGPSTracker extends Service implements LocationListener {
    private final Context wifiUnLockerProContext;
    private boolean wifiUnLockerProIsGPSEnabled = false;
    private boolean wifiUnLockerProNetworkEnabled = false;
    private Location wifiUnLockerProLocation;
    private double latitude,longitude;

    private LocationManager locationWifiUnLockerProManager;

    public UnLockerProGPSTracker(Context wifiUnLockerProContext){
        this.wifiUnLockerProContext = wifiUnLockerProContext;
        getLocation();
    }

    private Location getLocation() {
        try {
            locationWifiUnLockerProManager = (LocationManager) wifiUnLockerProContext.getSystemService(Context.LOCATION_SERVICE);
            wifiUnLockerProIsGPSEnabled = locationWifiUnLockerProManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            wifiUnLockerProNetworkEnabled = locationWifiUnLockerProManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!wifiUnLockerProIsGPSEnabled && !wifiUnLockerProNetworkEnabled){
            } else {
                if (wifiUnLockerProNetworkEnabled){
                    if(ActivityCompat.checkSelfPermission(wifiUnLockerProContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        locationWifiUnLockerProManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 3, this);
                        if (locationWifiUnLockerProManager != null){
                            wifiUnLockerProLocation = locationWifiUnLockerProManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (wifiUnLockerProLocation != null){
                                latitude = wifiUnLockerProLocation.getLatitude();
                                longitude = wifiUnLockerProLocation.getLongitude();
                            }
                        }
                    }
                }
                if (wifiUnLockerProIsGPSEnabled){
                    if (wifiUnLockerProLocation == null){
                        locationWifiUnLockerProManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 3, this);
                        if (locationWifiUnLockerProManager != null){
                            wifiUnLockerProLocation = locationWifiUnLockerProManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (wifiUnLockerProLocation != null){
                                latitude = wifiUnLockerProLocation.getLatitude();
                                longitude = wifiUnLockerProLocation.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return wifiUnLockerProLocation;
    }
    public double getLatitude(){
        if (wifiUnLockerProLocation != null){
            latitude = wifiUnLockerProLocation.getLatitude();
        }
        return latitude;
    }
    public double getLongitude(){
        if (wifiUnLockerProLocation != null){
            longitude = wifiUnLockerProLocation.getLongitude();
        }
        return longitude;
    }
    @Override
    public void onLocationChanged(Location location) {
        if (location != null){
            this.wifiUnLockerProLocation = location;
        }
    }
    @Override
    public void onProviderDisabled(String provider) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
