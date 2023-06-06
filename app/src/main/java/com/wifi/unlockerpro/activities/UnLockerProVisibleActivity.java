package com.wifi.unlockerpro.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;


import com.wifi.unlockerpro.R;
import com.wifi.unlockerpro.fragments.MapFragment;
import com.wifi.unlockerpro.fragments.AvailableWifiFragment;
import com.wifi.unlockerpro.fragments.SpeedFragment;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class UnLockerProVisibleActivity extends AppCompatActivity {
    private static final String TAG = UnLockerProVisibleActivity.class.getSimpleName();
    AnimatedBottomBar animatedBottomBar;
    FragmentManager fragmentManager;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visible_un_locker_pro);
        animatedBottomBar = findViewById(R.id.animatedBottomBar);

        if (savedInstanceState == null) {
            animatedBottomBar.selectTabById(R.id.map, true);
            fragmentManager = getSupportFragmentManager();
            MapFragment BookFragment = new MapFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, BookFragment)
                    .commit();
        }
        animatedBottomBar.setOnTabSelectListener((lastIndex, lastTab, newIndex, newTab) -> {
            Fragment fragment = null;
            switch (newTab.getId()) {
                case R.id.map:
                    fragment = new MapFragment();
                    break;
                case R.id.available_wifi:
                    fragment = new AvailableWifiFragment();
                    break;
                case R.id.speed:
                    fragment = new SpeedFragment();
                    break;
            }

            if (fragment != null) {
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                        .commit();
            } else {
                Log.e(TAG, "Error in creating Fragment");
            }
        });
    }
}
