package com.wifi.unlockerpro.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class UnLockerProPingTest extends Thread {
    private String server = "";
    private int count;
    private double instantRtt = 0;
    private double avgRtt = 0.0;
    private boolean wifiUnLockerProFinished = false;
    public UnLockerProPingTest(String serverIpAddress, int pingTryCount) {
        this.server = serverIpAddress;
        this.count = pingTryCount;
    }
    public double wifiUnLockerProGetAvgRtt() {
        return avgRtt;
    }
    public double wifiUnLockerProGetInstantRtt() {
        return instantRtt;
    }
    public boolean wifiUnLockerProIsFinished() {
        return wifiUnLockerProFinished;
    }

    @Override
    public void run() {
        try {
            ProcessBuilder ps = new ProcessBuilder("ping", "-c " + count, this.server);

            ps.redirectErrorStream(true);
            Process pr = ps.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.contains("icmp_seq")) {
                    instantRtt = Double.parseDouble(line.split(" ")[line.split(" ").length - 2].replace("time=", ""));
                }
                if (line.startsWith("rtt ")) {
                    avgRtt = Double.parseDouble(line.split("/")[4]);
                    break;
                }
            }
            pr.waitFor();
            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        wifiUnLockerProFinished = true;
    }
}
