package com.wifi.unlockerpro.fragments;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.CHANGE_NETWORK_STATE;
import static android.Manifest.permission.CHANGE_WIFI_STATE;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.wifi.unlockerpro.Adapter.UnLockerProNetworkListAdapter;
import com.wifi.unlockerpro.R;
import com.wifi.unlockerpro.databinding.FragmentHomeBinding;
import com.intentfilter.androidpermissions.PermissionManager;
import com.intentfilter.wificonnect.ScanResultsListener;
import com.intentfilter.wificonnect.WifiConnectionManager;
import java.util.Collections;
import java.util.List;

public class AvailableWifiFragment extends Fragment {
    private FragmentHomeBinding binding;
    LocationManager wifiUnLockerProManager11;
    boolean ShowGpsStatue;
    RecyclerView wifiNetworksListView;
    public AvailableWifiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate (inflater, container, false);
        View root = binding.getRoot ();
        wifiNetworksListView = root.findViewById (R.id.listof_wifi_networks1);
        PermissionManager permissionManager = PermissionManager.getInstance (requireContext ());
        permissionManager.checkPermissions (Collections.singletonList (ACCESS_COARSE_LOCATION),
                new PermissionManager.PermissionRequestListener () {
                    @Override
                    public void onPermissionGranted() {
                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText (requireActivity (), "Please provide permission to scan networks", Toast.LENGTH_LONG).show ();
                    }
                });
        permissionManager.checkPermissions (Collections.singletonList (CHANGE_WIFI_STATE),
                new PermissionManager.PermissionRequestListener () {
                    @Override
                    public void onPermissionGranted() {

                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText (requireActivity (), "Please provide permission to scan networks", Toast.LENGTH_LONG).show ();
                    }
                });
        permissionManager.checkPermissions (Collections.singletonList (ACCESS_WIFI_STATE),
                new PermissionManager.PermissionRequestListener () {
                    @Override
                    public void onPermissionGranted() {

                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText (requireActivity (), "Please provide permission to scan networks", Toast.LENGTH_LONG).show ();
                    }
                });
        permissionManager.checkPermissions (Collections.singletonList (CHANGE_NETWORK_STATE),
                new PermissionManager.PermissionRequestListener () {
                    @Override
                    public void onPermissionGranted() {

                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText (requireActivity (), "Please provide permission to scan networks", Toast.LENGTH_LONG).show ();
                    }
                });
        permissionManager.checkPermissions (Collections.singletonList (ACCESS_NETWORK_STATE),
                new PermissionManager.PermissionRequestListener () {
                    @Override
                    public void onPermissionGranted() {
                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText (requireActivity (), "Please provide permission to scan networks", Toast.LENGTH_LONG).show ();
                    }
                });
        permissionManager.checkPermissions (Collections.singletonList (ACCESS_FINE_LOCATION),
                new PermissionManager.PermissionRequestListener () {
                    @Override
                    public void onPermissionGranted() {
                        if (CheckunlockwificonnectStatus ()) {
                            scanForAvailableNeunlock ();
                                    binding.listofWifiNetworks1.setVisibility (View.VISIBLE);
                        } else {
                            Intent intent = new Intent (Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult (intent, 1);
                        }
                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText (requireActivity (), "Please provide permission to scan networks", Toast.LENGTH_LONG).show ();
                    }
                });

        return root;
    }

    public boolean CheckunlockwificonnectStatus() {
        wifiUnLockerProManager11 = (LocationManager) requireActivity ().getSystemService (Context.LOCATION_SERVICE);
        assert wifiUnLockerProManager11 != null;
        ShowGpsStatue = wifiUnLockerProManager11.isProviderEnabled (LocationManager.GPS_PROVIDER);
        if (ShowGpsStatue) {
            return true;
        } else {
            return false;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if (CheckunlockwificonnectStatus ()) {

            scanForAvailableNeunlock ();
            new Handler ().postDelayed(new Runnable(){
                @Override
                public void run() {
                    binding.listofWifiNetworks1.setVisibility (View.VISIBLE);
                }
            }, 400);
        } else {
            Intent intent = new Intent (Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult (intent, 1);
        }
    }

    private void scanForAvailableNeunlock() {
        WifiConnectionManager wifiConnectionManager = new WifiConnectionManager (requireActivity ());
        wifiConnectionManager.scanForNetworks (new ScanResultsListener() {
            @Override
            public void onScanResultsAvailable(List<ScanResult> scanResults) {
                try {
                    showNetworkAvailablilityUnlockWifi (scanResults);
                }catch (Exception ignored){
                }
            }
        });

    }
    private void showNetworkAvailablilityUnlockWifi(List<ScanResult> scanResults) {
        try {
            wifiNetworksListView.addItemDecoration (new DividerItemDecoration(requireActivity (), 0));
            wifiNetworksListView.setLayoutManager (new LinearLayoutManager(requireActivity ()));
            wifiNetworksListView.setAdapter (new UnLockerProNetworkListAdapter(requireActivity (), scanResults));
        }catch (Exception ignored){
        }
     }

    @Override
    public void onDestroyView() {
        super.onDestroyView ();
        binding = null;
    }

}
