package com.wifi.unlockerpro.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import com.wifi.unlockerpro.R;

public class UnLockerProPopWindow extends Activity{
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popwindow_wifi_unlocker_pro);

        String nameF = getIntent().getExtras().getString("name1");
        int strengthF = getIntent().getExtras().getInt("strength1");
        String nameS = getIntent().getExtras().getString("name2");
        int strengthS = getIntent().getExtras().getInt("strength2");
        String nameT = getIntent().getExtras().getString("name3");
        int strengthT = getIntent().getExtras().getInt("strength3");
        TextView wifiNameF = findViewById(R.id.UnlockWifi1);
        TextView wifiNameS = findViewById(R.id.UnlockWifi2);
        TextView wifiNameT = findViewById(R.id.UnlockWifi3);
        wifiNameF.setText(nameF);
        wifiNameS.setText(nameS);
        wifiNameT.setText(nameT);
        ImageView wifiF = findViewById(R.id.ic_wifi1);
        ImageView wifiS = findViewById(R.id.ic_wifi2);
        ImageView wifiT = findViewById(R.id.ic_wifi3);
        setImage(wifiF, strengthF);
        setImage(wifiS, strengthS);
        setImage(wifiT, strengthT);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        getWindow().setLayout((int)(width * 0.8), (int)(height * 0.6));
        wifiNameF.setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result","1"); // Send value back to MapsActivity
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });

        wifiNameS.setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result","2"); // Send value back to MapsActivity
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });

        wifiNameT.setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result","3"); // Send value back to MapsActivity
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });
        Button retryButton = findViewById(R.id.Retry_UnlockWifi);
        retryButton.setOnClickListener(v -> new AlertDialog.Builder(UnLockerProPopWindow.this)
                .setMessage("Do you want to test again?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    startActivity(new Intent(UnLockerProPopWindow.this, UnLockerProVisibleActivity.class)); //close the pop window
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create().show());
    }
    // Show WiFi strength
    private void setImage(ImageView v, int strength) {
        if (strength > 66) {
            v.setImageResource(R.drawable.ic_excellent);
        } else if (strength >= 33 || strength <= 66) {
            v.setImageResource(R.drawable.ic_good);
        } else if (strength >= 0 || strength < 33) {
            v.setImageResource(R.drawable.ic_fair);
        } else {
            v.setImageResource(R.drawable.ic_poor);
        }
    }
}
