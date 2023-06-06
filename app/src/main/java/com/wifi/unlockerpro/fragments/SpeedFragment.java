package com.wifi.unlockerpro.fragments;

import android.location.Location;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wifi.unlockerpro.UnLockerProDBHelper;
import com.wifi.unlockerpro.test.UnLockerProHttpDwlTest;
import com.wifi.unlockerpro.test.UnLockerProUploadTest;
import com.wifi.unlockerpro.test.UnLockerProPingTest;
import com.wifi.unlockerpro.R;
import com.wifi.unlockerpro.activities.UnLockerProSpeedTestHostsHandler;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SpeedFragment extends Fragment {

    private static int wifiUnLockerProPosition = 0;
    private static int wifiUnLockerProLastPosition = 0;
    private UnLockerProSpeedTestHostsHandler wifiUnLockerProWifiSpeedTestHostsHandler = null;
    private HashSet<String> wifiUnLockerProTempBlackList;
    private TextView wifiUnLockerProOnStartBtnTheRun;
    private ImageView wifiUnLockerProBarImgVwa;
    private TextView pingTxtView, showDownSpeedInNum, showUploadSpeedInNum,text_digital;
    private DecimalFormat wifiUnLockerProDecimalNumberInt;
    private TextView showWifiHostLocationText, showWifi_ISP_Text;
    private UnLockerProDBHelper wifiUnLockerProSDataBaseHeLpeRun;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_speed, container, false);
        wifiUnLockerProWifiSpeedTestHostsHandler = new UnLockerProSpeedTestHostsHandler();
        wifiUnLockerProWifiSpeedTestHostsHandler.start();
        wifiUnLockerProDecimalNumberInt = new DecimalFormat("#.##");
        wifiUnLockerProSDataBaseHeLpeRun = new UnLockerProDBHelper(getActivity());

        wifiUnLockerProTempBlackList = new HashSet<>();
        wifiUnLockerProOnStartBtnTheRun =   root.findViewById(R.id.onStartBtnTheRun_id);
        wifiUnLockerProOnStartBtnTheRun.setText("");
        wifiUnLockerProBarImgVwa =   root.findViewById(R.id.showTheNail_id);
        pingTxtView =   root.findViewById(R.id.pingTxtView);
        showDownSpeedInNum =   root.findViewById(R.id.showDownSpeedInNum_id);
        showUploadSpeedInNum =   root.findViewById(R.id.showUploadSpeedInNum_id);
        text_digital =   root.findViewById(R.id.text_digital);
        showWifiHostLocationText =   root.findViewById(R.id.showWifiHostLocationText_id);
        showWifi_ISP_Text =   root.findViewById(R.id.showWifi_ISP_Text_id);
        text_digital.setText("START");


        text_digital.setOnClickListener(v -> {
            if(text_digital.getText().toString().equalsIgnoreCase("TEST AGAIN"))
            {
                wifiUnLockerProStartSpeedTest();
            }
            else
            {
                wifiUnLockerProStartSpeedTest();
            }
        });
        return root;
    }
    public int wifiUnLockerProGetPositionByRate(double rate) {
        if (rate <= 1) {
            return (int) (rate * 30);

        } else if (rate <= 10) {
            return (int) (rate * 6) + 30;

        } else if (rate <= 30) {
            return (int) ((rate - 10) * 3) + 90;

        } else if (rate <= 50) {
            return (int) ((rate - 30) * 1.5) + 150;

        } else if (rate <= 100) {
            return (int) ((rate - 50) * 1.2) + 180;
        }
        return 0;
    }
    public int wifiUnLockerProGetPositionByRatePing(double rate) {
        if (rate <= 1) {
            return (int) (rate * 30);

        } else if (rate <= 10) {
            return (int) (rate * 6) + 30;

        } else if (rate <= 30) {
            return (int) ((rate - 10) * 3) + 90;

        } else if (rate <= 50) {
            return (int) ((rate - 30) * 1.5) + 150;

        } else if (rate <= 100) {
            return (int) ((rate - 50) * 1.2) + 180;
        }

        return (int) ((rate - 50) * 1.2) + 180;
    }
    public void onPause() {
        super.onPause();
    }
    public void onResume() {
        super.onResume();
    }
    public void wifiUnLockerProStartSpeedTest(){
        wifiUnLockerProOnStartBtnTheRun.setEnabled(false);
        text_digital.setEnabled(false);

        if (wifiUnLockerProWifiSpeedTestHostsHandler == null) {
            wifiUnLockerProWifiSpeedTestHostsHandler = new UnLockerProSpeedTestHostsHandler();
            wifiUnLockerProWifiSpeedTestHostsHandler.start();
        }

        new Thread(new Runnable() {
            RotateAnimation rotate;
            @Override
            public void run() {
                try {
                    getActivity().runOnUiThread(() -> {
                        text_digital.setText("WAIT");
                        wifiUnLockerProOnStartBtnTheRun.setText("Selecting best server based on ping...");
                    });
                }catch (Exception ignored){

                }
                int timeCount = 500; //1min
                while (!wifiUnLockerProWifiSpeedTestHostsHandler.isFinished()) {
                    timeCount--;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                    if (timeCount <= 0) {
                        try {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getActivity(), "No Connection...", Toast.LENGTH_LONG).show();
                                wifiUnLockerProOnStartBtnTheRun.setEnabled(true);
                                text_digital.setEnabled(true);
                                wifiUnLockerProOnStartBtnTheRun.setText("");
                                text_digital.setText("TEST AGAIN");
                            });
                        }catch (Exception ignored){
                        }
                        wifiUnLockerProWifiSpeedTestHostsHandler = null;
                        return;
                    }
                }
                //Find closest server
                HashMap<Integer, String> mapKey = wifiUnLockerProWifiSpeedTestHostsHandler.getMapKey();
                HashMap<Integer, List<String>> mapValue = wifiUnLockerProWifiSpeedTestHostsHandler.getMapValue();
                double selfLat = wifiUnLockerProWifiSpeedTestHostsHandler.getSelfLat();
                double selfLon = wifiUnLockerProWifiSpeedTestHostsHandler.getSelfLon();
                double tmp = 19349458;
                double dist = 0.0;
                int findServerIndex = 0;
                for (int index : mapKey.keySet()) {
                    if (wifiUnLockerProTempBlackList.contains(mapValue.get(index).get(5))) {
                        continue;
                    }

                    Location source = new Location("Source");
                    source.setLatitude(selfLat);
                    source.setLongitude(selfLon);

                    List<String> ls = mapValue.get(index);
                    Location dest = new Location("Dest");
                    dest.setLatitude(Double.parseDouble(ls.get(0)));
                    dest.setLongitude(Double.parseDouble(ls.get(1)));

                    double distance = source.distanceTo(dest);
                    if (tmp > distance) {
                        tmp = distance;
                        dist = distance;
                        findServerIndex = index;
                    }
                }
                String uploadAddr = mapKey.get(findServerIndex);
                final List<String> info = mapValue.get(findServerIndex);

                if (info == null) {
                    getActivity().runOnUiThread(() -> {
                        wifiUnLockerProOnStartBtnTheRun.setText("There was a problem in getting Host Location. Try again later.");
                    });
                    return;
                }
                getActivity().runOnUiThread(() -> {
                    wifiUnLockerProOnStartBtnTheRun.setText("");
                    showWifiHostLocationText.setText(info.get(2));
                    showWifi_ISP_Text.setText(wifiUnLockerProWifiSpeedTestHostsHandler.getIspName());
                });
                //Reset value, graphics
                getActivity().runOnUiThread(() -> {
                    pingTxtView.setText("0 ms");
                    showDownSpeedInNum.setText("0 Mbps");
                    showUploadSpeedInNum.setText("0 Mbps");
                    text_digital.setText("0.00");
                });
                final List<Double> detectPingRateList = new ArrayList<>();
                final List<Double> detectDownloadRateList = new ArrayList<>();
                final List<Double> detectUploadRateList = new ArrayList<>();
                boolean detectPingTestStarted = false;
                boolean detectPingTestFinished = false;
                boolean detectDownloadTestStarted = false;
                boolean detectDownloadTestFinished = false;
                boolean detectUploadTestStarted = false;
                boolean detectUploadTestFinished = false;
                //Init Test
                final UnLockerProPingTest pingTest = new UnLockerProPingTest(info.get(6).replace(":8080", ""), 6);
                final UnLockerProHttpDwlTest downloadTest = new UnLockerProHttpDwlTest(uploadAddr.replace(uploadAddr.split("/")[uploadAddr.split("/").length - 1], ""));
                final UnLockerProUploadTest uploadTest = new UnLockerProUploadTest(uploadAddr);
                //Tests
                while (true) {
                    if (!detectPingTestStarted) {
                        pingTest.start();
                        detectPingTestStarted = true;
                    }
                    if (detectPingTestFinished && !detectDownloadTestStarted) {
                        downloadTest.start();
                        detectDownloadTestStarted = true;
                    }
                    if (detectDownloadTestFinished && !detectUploadTestStarted) {
                        uploadTest.start();
                        detectUploadTestStarted = true;
                    }
                    //Ping Test
                    if (detectPingTestFinished) {
                        //Failure
                        if (pingTest.wifiUnLockerProGetAvgRtt() == 0) {
                            System.out.println("Ping error...");
                        } else {
                            try {
                                //Success
                                getActivity().runOnUiThread(() -> {
                                    pingTxtView.setText(wifiUnLockerProDecimalNumberInt.format(pingTest.wifiUnLockerProGetAvgRtt()) + " ms");
                                    wifiUnLockerProBarImgVwa.setRotation(0);
                                });
                            }catch (Exception ignored){
                            }
                        }
                    } else {
                        double pingRate = pingTest.wifiUnLockerProGetInstantRtt();
                        detectPingRateList.add(pingRate);
                        wifiUnLockerProPosition = wifiUnLockerProGetPositionByRatePing(pingRate);
                        try {
                            getActivity().runOnUiThread(() -> {
                                pingTxtView.setText(wifiUnLockerProDecimalNumberInt.format(pingTest.wifiUnLockerProGetInstantRtt()) + " ms");
                                text_digital.setText(wifiUnLockerProDecimalNumberInt.format(pingTest.wifiUnLockerProGetInstantRtt()));
                            });
                        }catch (Exception ignored){
                        }
                        wifiUnLockerProLastPosition = wifiUnLockerProPosition;
                    }
                    //Download Test
                    if (detectPingTestFinished) {
                        if (detectDownloadTestFinished) {
                            //Failure
                            if (downloadTest.wifiUnLockerProGetFinalDownloadRate() == 0) {
                                System.out.println("Download error...");
                            } else {
                                //Success
                                try {
                                    getActivity().runOnUiThread(() -> {
                                        showDownSpeedInNum.setText(wifiUnLockerProDecimalNumberInt.format(downloadTest.wifiUnLockerProGetFinalDownloadRate()) + " Mbps");
                                    });
                                }catch (Exception e){

                                }
                            }
                        } else {
                            //Calc position
                            double downloadRate = downloadTest.wifiUnLockerProGetInstantDownloadRate();
                            detectDownloadRateList.add(downloadRate);
                            wifiUnLockerProPosition = wifiUnLockerProGetPositionByRate(downloadRate);
                            try {
                                getActivity().runOnUiThread(() -> {
                                    rotate = new RotateAnimation(wifiUnLockerProLastPosition, wifiUnLockerProPosition, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                    rotate.setInterpolator(new LinearInterpolator());
                                    rotate.setDuration(100);
                                    wifiUnLockerProBarImgVwa.startAnimation(rotate);
                                    showDownSpeedInNum.setText(wifiUnLockerProDecimalNumberInt.format(downloadTest.wifiUnLockerProGetInstantDownloadRate()) + " Mbps");
                                    text_digital.setText(wifiUnLockerProDecimalNumberInt.format(downloadTest.wifiUnLockerProGetInstantDownloadRate()));
                                });
                            }catch (Exception ignored){
                            }
                            wifiUnLockerProLastPosition = wifiUnLockerProPosition;
                        }
                    }
                    if (detectDownloadTestFinished) {
                        if (detectUploadTestFinished) {
                            if (uploadTest.getFinalUploadRate() == 0) {
                                System.out.println("Upload error...");
                            } else {
                                getActivity().runOnUiThread(() -> {
                                    showUploadSpeedInNum.setText(wifiUnLockerProDecimalNumberInt.format(uploadTest.getFinalUploadRate()) + " Mbps");
                                });
                            }
                        } else {
                            double uploadRate = uploadTest.getInstantUploadRate();
                            detectUploadRateList.add(uploadRate);
                            wifiUnLockerProPosition = wifiUnLockerProGetPositionByRate(uploadRate);
                            try {
                                getActivity().runOnUiThread(() -> {
                                    rotate = new RotateAnimation(wifiUnLockerProLastPosition, wifiUnLockerProPosition, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                    rotate.setInterpolator(new LinearInterpolator());
                                    rotate.setDuration(100);
                                    wifiUnLockerProBarImgVwa.startAnimation(rotate);
                                    showUploadSpeedInNum.setText(wifiUnLockerProDecimalNumberInt.format(uploadTest.getInstantUploadRate()) + " Mbps");
                                    text_digital.setText(wifiUnLockerProDecimalNumberInt.format(uploadTest.getInstantUploadRate()));
                                });
                            }catch (Exception ignored){
                            }
                            wifiUnLockerProLastPosition = wifiUnLockerProPosition;
                        }
                    }
                    if (detectPingTestFinished && detectDownloadTestFinished && uploadTest.isFinished()) {
                        break;
                    }
                    if (pingTest.wifiUnLockerProIsFinished()) {
                        detectPingTestFinished = true;
                    }
                    if (downloadTest.wifiUnLockerProIsFinished()) {
                        detectDownloadTestFinished = true;
                    }
                    if (uploadTest.isFinished()) {
                        detectUploadTestFinished = true;
                    }
                    if (detectPingTestStarted && !detectPingTestFinished) {
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException ignored) {
                        }
                    } else {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
                try {
                    getActivity().runOnUiThread(() -> {
                        wifiUnLockerProOnStartBtnTheRun.setEnabled(true);
                        text_digital.setEnabled(true);
                        wifiUnLockerProOnStartBtnTheRun.setText("");
                        text_digital.setText("TEST AGAIN");
                        wifiUnLockerProSDataBaseHeLpeRun.insert(wifiUnLockerProDecimalNumberInt.format(pingTest.wifiUnLockerProGetInstantRtt())+"", wifiUnLockerProDecimalNumberInt.format(downloadTest.wifiUnLockerProGetFinalDownloadRate())+"", wifiUnLockerProDecimalNumberInt.format(uploadTest.getFinalUploadRate()),System.currentTimeMillis()+"", wifiUnLockerProWifiSpeedTestHostsHandler.getIspName(),info.get(2));
                    });
                }catch (Exception ignored){
                }
            }
        }).start();
    }
}