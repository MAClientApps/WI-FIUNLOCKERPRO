package com.wifi.unlockerpro.activities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class UnLockerProSpeedTestHostsHandler extends Thread {
    private final HashMap<Integer, String> mapKey = new HashMap<>();
    private final HashMap<Integer, List<String>> mapValue = new HashMap<>();
    private double selfLat = 0.0;
    private double selfLon = 0.0;
    private String isp = "";
    private boolean finished = false;
    public HashMap<Integer, String> getMapKey() {
        return mapKey;
    }
    public HashMap<Integer, List<String>> getMapValue() {
        return mapValue;
    }
    public double getSelfLat() {
        return selfLat;
    }
    public String getIspName() {
        return isp;
    }
    public double getSelfLon() {
        return selfLon;
    }
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void run() {
        try {
            URL url = new URL("https://www.speedtest.net/speedtest-config.php");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            int code = urlConnection.getResponseCode();
            if (code == 200) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                urlConnection.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.contains("isp=")) {
                        continue;
                    }
                    selfLat = Double.parseDouble(line.split("lat=\"")[1].split(" ")[0].replace("\"", ""));
                    selfLon = Double.parseDouble(line.split("lon=\"")[1].split(" ")[0].replace("\"", ""));
                    isp = line.split("isp=\"")[1].split("\"")[0];
                    break;
                }
                br.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        String detectUploadAddress = "";
        String detectName = "";
        String detectCountry = "";
        String detectCc = "";
        String detectSponsor = "";
        String detectlat = "";
        String detectLon = "";
        String detectHost = "";
        int detectCount = 0;
        try {
            URL url = new URL("https://www.speedtest.net/speedtest-servers-static.php");
            HttpURLConnection detectUrlConnection = (HttpURLConnection) url.openConnection();
            int code = detectUrlConnection.getResponseCode();

            if (code == 200) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                detectUrlConnection.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains("<server url")) {
                        detectUploadAddress = line.split("server url=\"")[1].split("\"")[0];
                        detectlat = line.split("lat=\"")[1].split("\"")[0];
                        detectLon = line.split("lon=\"")[1].split("\"")[0];
                        detectName = line.split("name=\"")[1].split("\"")[0];
                        detectCountry = line.split("country=\"")[1].split("\"")[0];
                        detectCc = line.split("cc=\"")[1].split("\"")[0];
                        detectSponsor = line.split("sponsor=\"")[1].split("\"")[0];
                        detectHost = line.split("host=\"")[1].split("\"")[0];

                        List<String> ls = Arrays.asList(detectlat, detectLon, detectName, detectCountry, detectCc, detectSponsor, detectHost);
                        mapKey.put(detectCount, detectUploadAddress);
                        mapValue.put(detectCount, ls);
                        detectCount++;
                    }
                }
                br.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        finished = true;
    }
}
