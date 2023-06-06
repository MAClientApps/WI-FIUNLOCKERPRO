package com.wifi.unlockerpro.fragments;

import static android.content.Context.WIFI_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.wifi.unlockerpro.activities.UnLockerProGPSTracker;
import com.wifi.unlockerpro.activities.UnLockerProPopWindow;
import com.wifi.unlockerpro.activities.UnLockerProSQLiteHelper;
import com.wifi.unlockerpro.activities.UnLockerProSignalInFon;
import com.wifi.unlockerpro.activities.UnLockerProSignAln;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.wifi.unlockerpro.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap wifiUnLockerProMMap;
    FusedLocationProviderClient wifiUnLockerProMFusedLocationProviderClient;

    Handler wifiUnLockerProHandler = new Handler (Looper.getMainLooper ());

    double wifiUnLockerProLatitude, wifiUnLockerProLongitude;
    UnLockerProGPSTracker gps;
    LatLng wifiUnLockerProCurPos;
    UnLockerProSQLiteHelper wifiUnLockerProHelper;
    private static final float DEFAULT_ZOOM = 18f;
    private EditText wifiUnLockerProSearchText;
    private ImageView wifiUnLockerProMGps;
    private Spinner wifiUnLockerProSpinner;
    private ImageView wifiUnLockerProMRotate;

    private HeatmapTileProvider wifiUnLockerProMProvider;
    private TileOverlay wifiUnLockerProMOverlay;

    private WifiManager wifiUnLockerProMainWifi;
    private Map<String, UnLockerProSignAln> wifiUnLockerProWifiSignals;
    private Map<String, UnLockerProSignAln> wifiUnLockerProNewWifiSignals;
    private SignalWifiUnlockerReceiver wifiUnLockerProSignalWifiUnlockerReceiver;

    public List<PriorityQueue<UnLockerProSignalInFon>> wifiUnLockerProTopFivelist = new ArrayList<>();
    private Runnable wifiUnLockerProRunnable;

    Button wifiUnLockerProMark;

    ImageView wifiUnLockerProSaveBtn;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        wifiUnLockerProHelper = new UnLockerProSQLiteHelper(requireActivity ());
        wifiUnLockerProSearchText = root.findViewById (R.id.wifiUnLockerProSearchText_id);
        wifiUnLockerProMGps = root.findViewById (R.id.wifiUnLockerProMGps_id);
        wifiUnLockerProMRotate = root.findViewById (R.id.wifiUnLockerProMRotate_id);
        wifiUnLockerProMark = root.findViewById (R.id.wifiUnLockerProMark_id);
        wifiUnLockerProSaveBtn = root.findViewById (R.id.wifiUnLockerProSaveBtn_id);
        wifiUnLockerProSpinner = root.findViewById (R.id.wifiUnLockerProSpinner_id);

        SupportMapFragment wifiUnLockerProSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_id);
        wifiUnLockerProSupportMapFragment.getMapAsync( this);

        Dexter.withContext(requireActivity())
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CHANGE_NETWORK_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE
                ).withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if(report.areAllPermissionsGranted()){
                            init ();
                            setSignalWifiUnlockerReceiver ();
                            startSignalSearchWifiUnlocker ();
                            spinner ();
                            startBtn ();
                            getDeviceLocation ();
                        }
                    }
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();
        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView ();
    }

    @Override
    public void onPause() {
        super.onPause ();
        try {
            requireContext ().unregisterReceiver (wifiUnLockerProSignalWifiUnlockerReceiver);
        }catch (Exception ignored){
        }
    }
    @SuppressLint("MissingPermission")
    public void onMapReady(GoogleMap googleMap) {
        wifiUnLockerProMMap = googleMap;
//        init ();
//        setSignalWifiUnlockerReceiver ();
//        startSignalSearchWifiUnlocker ();
//        spinner ();
//        startBtn ();

        if (ContextCompat.checkSelfPermission (requireActivity (), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getDeviceLocation ();
            wifiUnLockerProMMap.setMyLocationEnabled (true);
            wifiUnLockerProMMap.getUiSettings ().setMyLocationButtonEnabled (false);
        }

        wifiUnLockerProWifiSignals = new HashMap<>();
        wifiUnLockerProNewWifiSignals = new HashMap<> ();

        try {
            populateHeatmap ();
        } catch (JSONException e) {
            Toast.makeText (requireActivity (), "Problem getting data from db.", Toast.LENGTH_LONG).show ();
        }
    }
    private void init() {
        wifiUnLockerProSearchText.setOnEditorActionListener ((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                    keyEvent.getAction () == KeyEvent.ACTION_DOWN || keyEvent.getAction () == KeyEvent.KEYCODE_ENTER) {
                // Start searching
                geoLocate ();
            }
            return false;
        });

        wifiUnLockerProMGps.setOnClickListener (v -> {
            getDeviceLocation ();
        });

        wifiUnLockerProMRotate.setOnClickListener (v -> {
            CameraPosition cameraPosition = new CameraPosition.Builder ()
                    .target (wifiUnLockerProCurPos)
                    .zoom (18)
                    .bearing (45)
                    .tilt (90)                   // Tilt can only be 0 - 90
                    .build ();
            wifiUnLockerProMMap.animateCamera (CameraUpdateFactory.newCameraPosition (cameraPosition));
        });
    }

    private void geoLocate() {
        String searchString = wifiUnLockerProSearchText.getText ().toString ();
        Geocoder geocoder = new Geocoder (requireContext ());
        List<Address> list = new ArrayList<> ();

        try {
            list = geocoder.getFromLocationName (searchString, 1);
        } catch (IOException ignored) {
        }

        if (list.size () > 0) {
            Address address = list.get (0);
            moveCamera (new LatLng (address.getLatitude (), address.getLongitude ()), DEFAULT_ZOOM);
        }
    }

    private void startBtn() {
        // Create the gradient
        final int[] colors = setColors (102, 255, 0, 255, 0, 0);
        final float[] startPoints = setStartPoints (0.2f, 1f);
        final Gradient gradient = setGradient (colors, startPoints);

        wifiUnLockerProMark.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                wifiUnLockerProSaveBtn.setVisibility (View.INVISIBLE);
                if (wifiUnLockerProMark.getText ().equals ("Start")) {
                    wifiUnLockerProMark.setText ("Stop");
                    if (wifiUnLockerProMOverlay != null) {
                        wifiUnLockerProMOverlay.remove ();
                    }
                    // Updating all signals every other second
                        wifiUnLockerProRunnable = new Runnable () {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void run() {
                                try {
                                    gps = new UnLockerProGPSTracker(requireContext ());
                                    wifiUnLockerProLatitude = gps.getLatitude ();
                                    wifiUnLockerProLongitude = gps.getLongitude ();
                                    wifiUnLockerProCurPos = new LatLng (wifiUnLockerProLatitude, wifiUnLockerProLongitude);
                                    Log.i ("CurPos", wifiUnLockerProCurPos.toString ());
                                    wifiUnLockerProWifiSignals = updateWifi ();
                                    getBestKLoc (wifiUnLockerProWifiSignals);
                                    getBestKWifi ();

                                    if (!wifiUnLockerProTopFivelist.isEmpty ()) {
                                        drawHeatmap (getHeatmap (wifiUnLockerProTopFivelist.get (0)), gradient, 15, 0.7);
                                    }
                                    wifiUnLockerProHandler.postDelayed (() -> {
                                        if (wifiUnLockerProMOverlay != null) {
                                            wifiUnLockerProMOverlay.remove ();
                                        }
                                    }, 2000);
                                    wifiUnLockerProHandler.postDelayed (this, 2000);
                                }catch (Exception ignored){}
                            }
                        };
                        try {
                            wifiUnLockerProHandler.postDelayed (wifiUnLockerProRunnable, 1000);
                        }catch (Exception ignored){}
                } else {
                    try {
                        if (!wifiUnLockerProTopFivelist.isEmpty ()) {
                            makeJSON (wifiUnLockerProTopFivelist.get (0));
                        }
                    } catch (JSONException ignored) {}
                    Intent intent = new Intent (requireActivity (), UnLockerProPopWindow.class);
                    intent.putExtra ("name1", wifiUnLockerProTopFivelist.get (0).peek ().UnlockWifiName);
                    intent.putExtra ("strength1", wifiUnLockerProTopFivelist.get (0).peek ().UnlockWifiStrength);

                    intent.putExtra ("name2", wifiUnLockerProTopFivelist.get (1).peek ().UnlockWifiName);
                    intent.putExtra ("strength2", wifiUnLockerProTopFivelist.get (1).peek ().UnlockWifiStrength);

                    intent.putExtra ("name3", wifiUnLockerProTopFivelist.get (2).peek ().UnlockWifiName);
                    intent.putExtra ("strength3", wifiUnLockerProTopFivelist.get (2).peek ().UnlockWifiStrength);

                    startActivityForResult (intent, 1000);
                    wifiUnLockerProMark.setText ("View");
                    wifiUnLockerProHandler.removeCallbacks (wifiUnLockerProRunnable);
                }
            }
        });
    }
    private void saveBtn(String s) {
        final String data = s;
        wifiUnLockerProSaveBtn.setVisibility (View.VISIBLE);
        wifiUnLockerProSaveBtn.setOnClickListener (v -> new AlertDialog.Builder (requireContext ())
                .setMessage ("Are you sure to save this heat map?")
                .setPositiveButton ("Yes", (dialog, which) -> {
                    // Save data to DB
                    wifiUnLockerProHelper.addData (data);
                })
                .setNegativeButton ("No", (dialog, which) -> dialog.dismiss ())
                .create ().show ());
    }
    // Enable the Dropdown List
    private void spinner() {
        List<String> types = new ArrayList<> ();
        types.add ("Normal");
        types.add ("Hybrid");
        types.add ("Satellite");
        types.add ("Terrain");
        // Style and populate the spinner
        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter<> (requireActivity (), android.R.layout.simple_spinner_item, types);

        // Dropdown layout style
        dataAdapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);

        // Attaching data adapter to spinner
        wifiUnLockerProSpinner.setAdapter (dataAdapter);
        wifiUnLockerProSpinner.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition (position).equals ("Normal")) {
                    wifiUnLockerProMMap.setMapType (GoogleMap.MAP_TYPE_NORMAL);
                } else if (parent.getItemAtPosition (position).equals ("Hybrid")) {
                    wifiUnLockerProMMap.setMapType (GoogleMap.MAP_TYPE_HYBRID);
                } else if (parent.getItemAtPosition (position).equals ("Satellite")) {
                    wifiUnLockerProMMap.setMapType (GoogleMap.MAP_TYPE_SATELLITE);
                } else if (parent.getItemAtPosition (position).equals ("Terrain")) {
                    wifiUnLockerProMMap.setMapType (GoogleMap.MAP_TYPE_TERRAIN);
                }
                // On selecting a spinner item
                String item = parent.getItemAtPosition (position).toString ();

                // Show selected spinner item
                Toast.makeText (parent.getContext (), "Selected: " + item, Toast.LENGTH_SHORT).show ();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private void getDeviceLocation() {
        wifiUnLockerProMFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient (requireActivity ());
        try {
            if (ContextCompat.checkSelfPermission (requireContext (), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Task location = wifiUnLockerProMFusedLocationProviderClient.getLastLocation ();
                location.addOnCompleteListener (task -> {
                    if (task.isSuccessful ()) {
                        Location currentLocation = (Location) task.getResult ();
                        if (currentLocation != null) {
                            wifiUnLockerProCurPos = new LatLng (currentLocation.getLatitude (), currentLocation.getLongitude ());
                            moveCamera (wifiUnLockerProCurPos, DEFAULT_ZOOM);
                        }
                    }
                });
            }
        } catch (SecurityException ignored) {
        }
    }
    // Move camera
    private void moveCamera(LatLng latLng, float zoom) {
        CameraPosition cameraPosition = new CameraPosition.Builder ()
                .target (latLng)
                .zoom (zoom)
                .build ();
        wifiUnLockerProMMap.animateCamera (CameraUpdateFactory.newCameraPosition (cameraPosition));
    }

    private void populateHeatmap() throws JSONException {
        // Get data from db
        Cursor data = wifiUnLockerProHelper.getData ();
        ArrayList<WeightedLatLng> list = new ArrayList<> ();
        while (data.moveToNext ()) {
            // Get Json String data
            String json = data.getString (1);
            JSONArray array = new JSONArray (json);
            for (int i = 0; i < array.length (); i++) {
                JSONObject object = array.getJSONObject (i);
                double lat = object.getDouble ("lat");
                double lng = object.getDouble ("lng");
                int strength = object.getInt ("strength");

                list.add (new WeightedLatLng (new LatLng (lat, lng), strength));
            }
        }
        if (list.isEmpty ()) return;
        // Draw heatmap
        wifiUnLockerProMProvider = new HeatmapTileProvider.Builder ()
                .weightedData (list)
                .build ();
        wifiUnLockerProMOverlay = wifiUnLockerProMMap.addTileOverlay (new TileOverlayOptions().tileProvider (wifiUnLockerProMProvider));
    }
    // Generate heatmap data from pq
    private List<WeightedLatLng> getHeatmap(PriorityQueue<UnLockerProSignalInFon> pq) {
        List<WeightedLatLng> list = new ArrayList<> ();
        for (UnLockerProSignalInFon info : pq) {
            double lat = info.UnlockWifiLatitude;
            double lng = info.UnlockWifiLongitude;
            list.add (new WeightedLatLng (new LatLng (lat, lng), info.UnlockWifiStrength));
        }
        return list;
    }

    // Draw heatmap
    private void drawHeatmap(List<WeightedLatLng> data, Gradient gradient, int radius, double opacity) {
        // Create the tile provider
        wifiUnLockerProMProvider = new HeatmapTileProvider.Builder ()
                .weightedData (data)
                .gradient (gradient)
                .radius (radius)
                .opacity (opacity)
                .build ();

        // Add the tile overlay to the map
        wifiUnLockerProMOverlay = wifiUnLockerProMMap.addTileOverlay (new TileOverlayOptions ().tileProvider (wifiUnLockerProMProvider));
    }

    private int[] setColors(int r1, int g1, int b1, int r2, int g2, int b2) {
        int[] colors = {
                Color.rgb (r1, g1, b1), //Outer Color
                Color.rgb (r2, g2, b2)  //Inner Color
        };
        return colors;
    }

    // Set Heatmap StartPoints
    private float[] setStartPoints(float a, float b) {
        float[] startPoints = {a, b};
        return startPoints;
    }

    // Set Heatmap Gradients
    private Gradient setGradient(int[] colors, float[] startPoints) {
        Gradient gradient = new Gradient (colors, startPoints);
        return gradient;
    }

    // Reconstruction when refreshing the top 5 wifi pqs
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void get5WifiPQ() {
        PriorityQueue<UnLockerProSignalInFon> top5Wifi = new PriorityQueue<> (new Comparator<UnLockerProSignalInFon>() {
            @Override
            public int compare(UnLockerProSignalInFon o1, UnLockerProSignalInFon o2) {
                return o2.UnlockWifiStrength - o1.UnlockWifiStrength;
            }
        });
        Set<String> set = new HashSet<>();
        HashMap<String, UnLockerProSignAln> updatedWifi = new HashMap<> ();
        for (UnLockerProSignAln signaln : wifiUnLockerProWifiSignals.values ()) {
            if (wifiUnLockerProNewWifiSignals.containsKey (signaln.getUnlockWifiid ())) {
                updatedWifi.put (signaln.getUnlockWifiname (), signaln);
            }
        }
        for (UnLockerProSignAln signaln : wifiUnLockerProNewWifiSignals.values ()) {
            if (updatedWifi.containsKey (signaln.getUnlockWifiname ())) {
                UnLockerProSignAln cur = updatedWifi.get (signaln.getUnlockWifiname ());
                cur.update (signaln.getUnlockWifilevel ());
            } else {
                updatedWifi.put (signaln.getUnlockWifiname (), signaln);
            }

            if (!set.contains (signaln.getUnlockWifiname ())) {
                top5Wifi.add (new UnLockerProSignalInFon(signaln.getUnlockWifiid (), signaln.getUnlockWifiname (), signaln.getUnlockWifistrength (),
                        wifiUnLockerProLatitude, wifiUnLockerProLongitude));
                set.add (signaln.getUnlockWifiname ());
            }
            while (top5Wifi.size () > 5) {
                top5Wifi.remove ();
            }
        }
        initPQ (top5Wifi);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initPQ(PriorityQueue<UnLockerProSignalInFon> pqInfo) {
        while (!pqInfo.isEmpty ()) {
            PriorityQueue<UnLockerProSignalInFon> pq = new PriorityQueue<> (new Comparator<UnLockerProSignalInFon> () {
                @Override
                public int compare(UnLockerProSignalInFon o1, UnLockerProSignalInFon o2) {
                    return o2.UnlockWifiStrength - o1.UnlockWifiStrength;
                }
            });
            pq.add (pqInfo.poll ());
            wifiUnLockerProTopFivelist.add (pq);
        }
    }

    // Update top 5 wifis
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getBestKWifi() {
        PriorityQueue<UnLockerProSignAln> temp = new PriorityQueue<> (new Comparator<UnLockerProSignAln> () {
            @Override
            public int compare(UnLockerProSignAln o1, UnLockerProSignAln o2) {
                return o2.getUnlockWifistrength () - o1.getUnlockWifistrength ();
            }
        });
        HashSet<String> set = new HashSet<> ();
        for (UnLockerProSignAln signaln : wifiUnLockerProNewWifiSignals.values ()) {
            Log.i (signaln.getUnlockWifiname (), "newwifi: ");
            for (PriorityQueue<UnLockerProSignalInFon> pq : wifiUnLockerProTopFivelist)
                if (pq.peek ().UnlockWifiName.equals (signaln.getUnlockWifiname ())) {
                    if (!set.contains (signaln.getUnlockWifiname ())) {
                        set.add (signaln.getUnlockWifiname ());
                        temp.add (signaln);
                    }

                }
        }
        wifiUnLockerProTopFivelist.sort (new PQComparator ());
    }
    // Comparator to the sorted list
    class PQComparator implements Comparator<PriorityQueue<UnLockerProSignalInFon>> {
        public int compare(PriorityQueue<UnLockerProSignalInFon> p1, PriorityQueue<UnLockerProSignalInFon> p2) {
            return findMedian (p2) - findMedian (p1);
        }
    }
    private Integer findMedian(PriorityQueue<UnLockerProSignalInFon> pq) {
        List<UnLockerProSignalInFon> temp = new ArrayList<> ();
        int half = pq.size () / 2;
        int res = 0;
        while (half > 0) {
            UnLockerProSignalInFon curr = pq.poll ();
            if (curr != null) {
                temp.add (curr);
                res = curr.UnlockWifiStrength;
            }
            half--;
        }
        for (UnLockerProSignalInFon s : temp) {
            pq.add (s);
        }
        return res;
    }
    // Get the strongest 100 signal sources
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getBestKLoc(Map<String, UnLockerProSignAln> map) {
        String wifiId;
        String wifiName;

        if (wifiUnLockerProTopFivelist.size () < 1) {
            get5WifiPQ ();

        } else {
            for (Map.Entry<String, UnLockerProSignAln> entry : map.entrySet ()) {
                String name = entry.getValue ().getUnlockWifiname ();
                for (int i = 0; i < wifiUnLockerProTopFivelist.size (); i++) {
                    PriorityQueue<UnLockerProSignalInFon> info = new PriorityQueue<> (new Comparator<UnLockerProSignalInFon> () {
                        @Override
                        public int compare(UnLockerProSignalInFon o1, UnLockerProSignalInFon o2) {
                            return o2.UnlockWifiStrength - o1.UnlockWifiStrength;
                        }
                    });

                    for (UnLockerProSignalInFon s : wifiUnLockerProTopFivelist.get (i)) {
                        info.add (s);
                    }

                    if (info.peek ().UnlockWifiName.equals (name)) {
                        int strength = entry.getValue ().getUnlockWifistrength ();
                        wifiId = entry.getValue ().getUnlockWifiid ();
                        wifiName = entry.getValue ().getUnlockWifiname ();
                        info.add (new UnLockerProSignalInFon(wifiId, wifiName, strength, wifiUnLockerProLatitude, wifiUnLockerProLongitude));
                        wifiUnLockerProTopFivelist.set (i, info);
                    }
                }
            }
        }
    }

    private String makeJSON(PriorityQueue<UnLockerProSignalInFon> pq) throws JSONException {
        JSONArray array = new JSONArray ();
        for (UnLockerProSignalInFon element : pq) {
            JSONObject jsonElement = new JSONObject ();
            jsonElement.put ("name", element.UnlockWifiName);
            jsonElement.put ("lat", element.UnlockWifiLatitude);
            jsonElement.put ("lng", element.UnlockWifiLongitude);
            jsonElement.put ("strength", element.UnlockWifiStrength);
            array.put (jsonElement);
        }
        return array.toString ();
    }

    private HashMap<String, UnLockerProSignAln> updateWifi() {
        HashMap<String, UnLockerProSignAln> updatedWifi = new HashMap<> ();
        for (UnLockerProSignAln signaln : wifiUnLockerProWifiSignals.values ()) {
            if (wifiUnLockerProNewWifiSignals.containsKey (signaln.getUnlockWifiid ())) {
                updatedWifi.put (signaln.getUnlockWifiid (), signaln);
            }
        }
        for (UnLockerProSignAln signaln : wifiUnLockerProNewWifiSignals.values ()) {
            if (updatedWifi.containsKey (signaln.getUnlockWifiid ())) {
                UnLockerProSignAln cur = updatedWifi.get (signaln.getUnlockWifiid ());
                cur.update (signaln.getUnlockWifilevel ());
            } else {
                updatedWifi.put (signaln.getUnlockWifiid (), signaln);
            }
        }
        return updatedWifi;
    }

    private void wifiReceived() {
        HashMap<String, UnLockerProSignAln> tempWifiSignals = new HashMap<> ();
        @SuppressLint("MissingPermission") List<ScanResult> wifiScanResults = wifiUnLockerProMainWifi.getScanResults ();
        for (ScanResult wifi : wifiScanResults) {
            UnLockerProSignAln wifiSignaln = new UnLockerProSignAln(wifi);
            tempWifiSignals.put (wifi.BSSID, wifiSignaln);
        }
        wifiUnLockerProNewWifiSignals = tempWifiSignals;

        wifiUnLockerProMainWifi.startScan ();
    }

    public class SignalWifiUnlockerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction ();
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals (action)) {
                wifiReceived ();
            }
        }
    }

    /* Set up broadcast receiver to identify signals */
    private void setSignalWifiUnlockerReceiver() {
        wifiUnLockerProSignalWifiUnlockerReceiver = new SignalWifiUnlockerReceiver ();
        IntentFilter signalIntent = new IntentFilter ();
        signalIntent.addAction (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        requireContext ().registerReceiver (wifiUnLockerProSignalWifiUnlockerReceiver, signalIntent);
    }

    /* Start seaching for signals in area signals */
    @SuppressLint("WifiManagerLeak")
    private void startSignalSearchWifiUnlocker() {
        // for wifi
        wifiUnLockerProMainWifi = (WifiManager) requireContext ().getSystemService (WIFI_SERVICE);
        if (wifiUnLockerProMainWifi.isWifiEnabled ()) {
            wifiUnLockerProMainWifi.startScan ();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra ("result");
                if (result.equals ("1")) {
                    if (wifiUnLockerProMOverlay != null) wifiUnLockerProMOverlay.remove ();

                    int[] colors = setColors (102, 225, 0, 255, 0, 0);
                    float[] startPoints = setStartPoints (0.2f, 1f);
                    Gradient gradient = setGradient (colors, startPoints);
                    drawHeatmap (getHeatmap (wifiUnLockerProTopFivelist.get (0)), gradient, 15, 0.7);

                    // Convert pq to JSON String
                    try {
                        String json = makeJSON (wifiUnLockerProTopFivelist.get (0));
                        saveBtn (json);
                    } catch (JSONException e) {
                        Log.i ("Error", "Json convert failed.");
                    }
                }
                if (result.equals ("2")) {
                    if (wifiUnLockerProMOverlay != null) wifiUnLockerProMOverlay.remove ();

                    int[] colors = setColors (0, 0, 255, 255, 255, 0);
                    float[] startPoints = setStartPoints (0.2f, 1f);
                    Gradient gradient = setGradient (colors, startPoints);
                    drawHeatmap (getHeatmap (wifiUnLockerProTopFivelist.get (1)), gradient, 15, 0.7);

                    // Convert pq to JSON String
                    try {
                        String json = makeJSON (wifiUnLockerProTopFivelist.get (1));
                        saveBtn (json);
                    } catch (JSONException e) {
                        Log.i ("Error", "Json convert failed.");
                    }
                }
                if (result.equals ("3")) {
                    if (wifiUnLockerProMOverlay != null) wifiUnLockerProMOverlay.remove ();

                    int[] colors = setColors (128, 128, 128, 255, 0, 0);
                    float[] startPoints = setStartPoints (0.2f, 1f);
                    Gradient gradient = setGradient (colors, startPoints);
                    drawHeatmap (getHeatmap (wifiUnLockerProTopFivelist.get (2)), gradient, 15, 0.7);
                    // Convert pq to JSON String
                    try {
                        String json = makeJSON (wifiUnLockerProTopFivelist.get (2));
                        saveBtn (json);
                    } catch (JSONException e) {
                        Log.i ("Error", "Json convert failed.");
                    }
                }
            }
        }
    }
}
