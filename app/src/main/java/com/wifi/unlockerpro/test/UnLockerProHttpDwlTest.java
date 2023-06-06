package com.wifi.unlockerpro.test;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UnLockerProHttpDwlTest extends Thread {

    public String fileURL = "";
    private long wifiUnLockerProStartTime = 0;
    private long wifiUnLockerProEndTime = 0;
    private double wifiUnLockerProDownloadElapsedTime = 0;
    private int wifiUnLockerProDownloadedByte = 0;
    private double finalDownloadRate = 0.0;
    private boolean finished = false;
    private double instantDownloadRate = 0;
    private int timeout = 15;

    HttpURLConnection httpConn = null;

    public UnLockerProHttpDwlTest(String fileURL) {
        this.fileURL = fileURL;
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd;
        try {
            bd = new BigDecimal(value);
        } catch (Exception ex) {
            return 0.0;
        }
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public double wifiUnLockerProGetInstantDownloadRate() {
        return instantDownloadRate;
    }

    public void wifiUnLockerProSetInstantDownloadRate(int downloadedByte, double elapsedTime) {

        if (downloadedByte >= 0) {
            this.instantDownloadRate = round((Double) (((downloadedByte * 8) / (1000 * 1000)) / elapsedTime), 2);
        } else {
            this.instantDownloadRate = 0.0;
        }
    }

    public double wifiUnLockerProGetFinalDownloadRate() {
        return round(finalDownloadRate, 2);
    }

    public boolean wifiUnLockerProIsFinished() {
        return finished;
    }

    @Override
    public void run() {
        URL url = null;
        wifiUnLockerProDownloadedByte = 0;
        int responseCode = 0;

        List<String> fileUrls = new ArrayList<>();
        fileUrls.add(fileURL + "random4000x4000.jpg");
        fileUrls.add(fileURL + "random3000x3000.jpg");

        wifiUnLockerProStartTime = System.currentTimeMillis();

        outer:
        for (String link : fileUrls) {
            try {
                url = new URL(link);
                httpConn = (HttpURLConnection) url.openConnection();
                responseCode = httpConn.getResponseCode();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            try {
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    byte[] buffer = new byte[10240];

                    InputStream inputStream = httpConn.getInputStream();
                    int len = 0;

                    while ((len = inputStream.read(buffer)) != -1) {
                        wifiUnLockerProDownloadedByte += len;
                        wifiUnLockerProEndTime = System.currentTimeMillis();
                        wifiUnLockerProDownloadElapsedTime = (wifiUnLockerProEndTime - wifiUnLockerProStartTime) / 1000.0;
                        wifiUnLockerProSetInstantDownloadRate(wifiUnLockerProDownloadedByte, wifiUnLockerProDownloadElapsedTime);
                        if (wifiUnLockerProDownloadElapsedTime >= timeout) {
                            break outer;
                        }
                    }

                    inputStream.close();
                    httpConn.disconnect();
                } else {
                    System.out.println("Link not found...");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        wifiUnLockerProEndTime = System.currentTimeMillis();
        wifiUnLockerProDownloadElapsedTime = (wifiUnLockerProEndTime - wifiUnLockerProStartTime) / 1000.0;
        finalDownloadRate = ((wifiUnLockerProDownloadedByte * 8) / (1000 * 1000.0)) / wifiUnLockerProDownloadElapsedTime;

        finished = true;
    }

}
