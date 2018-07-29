package com.example.raunaksethiya.reviewsys1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String link;
    private Session session;
    private SupportMapFragment mapFragment;
    private List<nameLoc> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        session = new Session(getBaseContext());
        data = new ArrayList<nameLoc>();

        Intent theIntent = getIntent();
        if (theIntent.getExtras().getBoolean("isCategory")) {
            link = getString(R.string.link_to_server) + "get_map_category.php?category="
                    + theIntent.getExtras().getString("category");
        } else if (theIntent.getExtras().getBoolean("isMyPlace")) {
            link = getString(R.string.link_to_server) + "get_map_places.php?category="
                    + theIntent.getExtras().getString("category") + "&user=" + session.getUsername();
        } else if (theIntent.getExtras().getBoolean("isPlaceDetails")) {
            try {
                link = getString(R.string.link_to_server) + "get_map_detail.php?category="
                        + theIntent.getExtras().getString("category") + "&name=" + URLEncoder.encode(
                        theIntent.getExtras().getString("name"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        new sendToServer().execute();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, 10);
            }
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        // Add a marker in Sydney and move the camera
        for (int i = 0; i < data.size(); i++) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(data.get(i).lat, data.get(i).lng)).title(data.get(i).name));
            if (i == 0)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(data.get(i).lat, data.get(i).lng)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mapFragment.getMapAsync(MapsActivity.this);
                }
        }
    }

    private class sendToServer extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return (new PostToServer(link).postToServer());
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("stuff");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Double lat, lng;
                        String name = jsonArray.getJSONObject(i).getString("name");
                        if (!Objects.equals(jsonArray.getJSONObject(i).getString("lat"), "")) {
                            lat = Double.valueOf(jsonArray.getJSONObject(i).getString("lat"));
                        } else {
                            lat = 18d;
                        }
                        if (!Objects.equals(jsonArray.getJSONObject(i).getString("long"), "")) {
                            lng = Double.valueOf(jsonArray.getJSONObject(i).getString("long"));
                        } else {
                            lng = 73d;
                        }
                        data.add(new nameLoc(name, lat, lng));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mapFragment.getMapAsync(MapsActivity.this);
            }
        }
    }

    class nameLoc {
        public String name;
        public Double lat;
        public Double lng;

        public nameLoc(String name, Double lat, Double lng) {
            this.name = name;
            this.lat = lat;
            this.lng = lng;
        }
    }
}
