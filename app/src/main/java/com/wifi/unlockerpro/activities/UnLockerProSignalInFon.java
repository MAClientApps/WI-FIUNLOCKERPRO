package com.wifi.unlockerpro.activities;

public class UnLockerProSignalInFon implements Comparable{
    public String UnlockWifiId;
    public String UnlockWifiName;
    public int UnlockWifiStrength;
    public double UnlockWifiLatitude;
    public double UnlockWifiLongitude;

    public UnLockerProSignalInFon(String UnlockWifiId, String UnlockWifiName, int UnlockWifistrength, double UnlockWifilatitude, double UnlockWifilongitude) {
        this.UnlockWifiId = UnlockWifiId;
        this.UnlockWifiName = UnlockWifiName;
        this.UnlockWifiStrength = UnlockWifistrength;
        this.UnlockWifiLatitude = UnlockWifilatitude;
        this.UnlockWifiLongitude = UnlockWifilongitude;
    }

    @Override
    public String toString() {
        return UnlockWifiId + " - " + UnlockWifiName + " - " + UnlockWifiLatitude + ", " + UnlockWifiLongitude + " - " + UnlockWifiStrength;
    }

    @Override
    public int compareTo(Object o) {
        UnLockerProSignalInFon otherSignal = (UnLockerProSignalInFon) o;
        return otherSignal.UnlockWifiStrength - UnlockWifiStrength;
    }
}
