package com.wifi.unlockerpro.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.wifi.unlockerpro.R;
import com.wifi.unlockerpro.activities.UnLockerProRedirectActivity;
import com.google.android.material.snackbar.Snackbar;
import com.intentfilter.wificonnect.WifiConnectionManager;
import java.util.List;

public class UnLockerProNetworkListAdapter extends RecyclerView.Adapter<UnLockerProNetworkListAdapter.WifiNetworkConnectItemViewHolder>  {

    private final WifiConnectionManager unlockkwifiConnectionManager;
    private Context contextlock;
    private List<ScanResult> wifiscanRsltsunlockk;
    public UnLockerProNetworkListAdapter(Context contextlock, List<ScanResult> scanResults) {
        this.contextlock = contextlock;
        this.wifiscanRsltsunlockk = scanResults;
        this.unlockkwifiConnectionManager = new WifiConnectionManager(contextlock.getApplicationContext());
    }

    @NonNull
    @Override
    public WifiNetworkConnectItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contextlock).inflate(R.layout.wifi_list_item_view_wifi_unlocker_pro, parent, false);
        return new WifiNetworkConnectItemViewHolder (view);
    }


    @Override
    public void onBindViewHolder(@NonNull final WifiNetworkConnectItemViewHolder holder, int position) {
        holder.addWifiNetworkToList(wifiscanRsltsunlockk.get(position));

        holder.unlockk_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WifiManager wifiManager = (WifiManager) contextlock.getSystemService(Context.WIFI_SERVICE);

                if(wifiManager.isWifiEnabled()){
                    final ScanResult scanResult = wifiscanRsltsunlockk.get(holder.getAdapterPosition());
                    Snackbar.make(((Activity) contextlock).findViewById(R.id.status1), "Connecting to: " + scanResult.SSID, Snackbar.LENGTH_LONG).show();

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder();
                        builder.setSsid(scanResult.SSID);
                        WifiNetworkSpecifier wifiNetworkSpecifier = builder.build();
                        NetworkRequest.Builder networkRequestBuilder = new NetworkRequest.Builder();
                        networkRequestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
                        networkRequestBuilder.removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                        networkRequestBuilder.setNetworkSpecifier(wifiNetworkSpecifier);
                        NetworkRequest networkRequest = networkRequestBuilder.build();

                        ConnectivityManager cm = (ConnectivityManager) contextlock.getSystemService(Context.CONNECTIVITY_SERVICE);
                        ConnectivityManager.NetworkCallback networkCallback = new
                                ConnectivityManager.NetworkCallback() {
                                    @Override
                                    public void onAvailable(Network network) {
                                        super.onAvailable(network);
                                        cm.bindProcessToNetwork(network);
                                        openActivity_redirect_webview(contextlock);
                                    }
                                };
                        cm.requestNetwork(networkRequest, networkCallback);
                    }
                    else
                    {
                        unlockkwifiConnectionManager.connectToAvailableSSID(scanResult.SSID, new WifiConnectionManager.ConnectionStateChangedListener() {
                            @Override
                            public void onConnectionEstablished() {
                                Toast.makeText(contextlock, "Now connected to " + scanResult.SSID, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onConnectionError(String reason) {
                                Toast.makeText(contextlock, "Could't connect due to: " + reason, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                else
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(contextlock);
                    alert.setTitle("Wifi Enable");
                    alert.setMessage("Press ok to enable your wifi");
                    alert.setPositiveButton("OK",
                            (dialog, id) -> {
                                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                contextlock.startActivity(intent);
                            });
                    alert.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                    alert.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return wifiscanRsltsunlockk.size();
    }
    public void openActivity_redirect_webview(Context mainContext)
    {
        Intent intent=new Intent(mainContext, UnLockerProRedirectActivity.class);
        mainContext.startActivity(intent);
    }
    static class WifiNetworkConnectItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView unlockkwifiNetworkNameView;
        private CardView unlockk_cardview;

        WifiNetworkConnectItemViewHolder(View itemView) {
            super(itemView);
            unlockkwifiNetworkNameView = itemView.findViewById(R.id.unlockkwifi_conect_name);
            unlockk_cardview = itemView.findViewById(R.id.unlockkcardview11);
        }

        void addWifiNetworkToList(ScanResult scanResult) {
            unlockkwifiNetworkNameView.setText(scanResult.SSID);
        }
    }

}
