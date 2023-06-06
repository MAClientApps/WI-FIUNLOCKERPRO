package com.wifi.unlockerpro.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.wifi.unlockerpro.R;

public class UnLockerProRedirectActivity extends AppCompatActivity {

    @SuppressLint({"MissingInflatedId", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_redirect_wifi_unlocker_pro);

        WebView webViewWifiUnLockerPro = findViewById(R.id.redirect_webview_unlockk);
        webViewWifiUnLockerPro.setWebViewClient(new WebViewClient());
        webViewWifiUnLockerPro.getSettings().setLoadsImagesAutomatically(true);
        webViewWifiUnLockerPro.getSettings().setJavaScriptEnabled(true);
        webViewWifiUnLockerPro.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webViewWifiUnLockerPro.loadUrl("https://www.google.com/");
    }
}