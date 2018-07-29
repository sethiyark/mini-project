package com.example.raunaksethiya.reviewsys1;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Raunak Sethiya on 10-Sep-16.
 */

public class MyCampusActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final static String SORT_ALPHABETICALLY = "name";
    private final static String SORT_BY_RATING = "rating";
    private final static String ASCENDING = "asc";
    private final static String DESCENDING = "des";
    private static Session session;
    private static MyAdapter[] theAdapter;
    private static ListView[] theListView;
    private static String[] catCollName;
    private static Intent toPlaceDetails;
    boolean isSortOpen, isAlphaOpen, isRatingOpen;
    LocationRequest mLocationRequest;
    private String sortBy;
    private String asc;
    private FloatingActionButton SortFab, AlphaFab, RatingFab, AscAlphaFab, DesAlphaFab, AscRatingFab, DesRatingFab, MapFab, byLocationFab;
    private Animation fabOpenAnimation, fabCloseAnimation, fabClickOpen, fabClickClose;
    private ViewPager viewPager;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation, mCurrentLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_campus);
        session = new Session(getBaseContext());
        Toolbar toolbar = findViewById(R.id.mycampus_toolbar);
        setSupportActionBar(toolbar);

        sortBy = null;
        asc = null;
        catCollName = new String[]{"canteen", "restaurants", "general_stores", "clinic_medical",
                "bank_atm", "saloons", "photocopy", "laundry", "hostels", "mess", "book_stores"};
        theAdapter = new MyAdapter[11];
        theListView = new ListView[11];
        toPlaceDetails = new Intent(MyCampusActivity.this, PlaceDetailsActivity.class);

        DrawerLayout myCampusDrawer = findViewById(R.id.mycampus_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, myCampusDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        myCampusDrawer.setDrawerListener(toggle);
        toggle.syncState();

        viewPager = findViewById(R.id.mycampus_viewpager);
        viewPager.setAdapter(new myFragmentPagerAdapter(getSupportFragmentManager()));
        TabLayout tabLayout = findViewById(R.id.mycampus_tab);
        tabLayout.setupWithViewPager(viewPager);

        MapFab = findViewById(R.id.toMap);
        SortFab = findViewById(R.id.baseFloatingActionButton);
        AlphaFab = findViewById(R.id.byAlphaFab);
        RatingFab = findViewById(R.id.byRatingFab);
        AscAlphaFab = findViewById(R.id.ascAlphaFab);
        DesAlphaFab = findViewById(R.id.desAlphaFab);
        AscRatingFab = findViewById(R.id.ascRatingFab);
        DesRatingFab = findViewById(R.id.desRatingFab);
        byLocationFab = findViewById(R.id.byLocationFab);
        isSortOpen = true;
        isAlphaOpen = true;
        isRatingOpen = true;
        fabOpenAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabCloseAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        fabClickClose = AnimationUtils.loadAnimation(this, R.anim.fab_clicked_close);
        fabClickOpen = AnimationUtils.loadAnimation(this, R.anim.fab_clicked);
        collapseFabMenu();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View v = navigationView.inflateHeaderView(R.layout.nav_header_main);

        if (session.getLoggedInStatus()) {
            ((TextView) v.findViewById(R.id.nav_name)).setText(session.getFullName());
            ((TextView) v.findViewById(R.id.nav_email)).setText(session.getEmail());
        }

        // Create an instance of GoogleAPIClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        for (int i = 0; i < 11; i++)
            new sendToServer().execute(i);
        if (isSortOpen)
            collapseFabMenu();
        if (!session.getLoggedInStatus() && !session.getGuest()) {
            final Intent toLogin = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(toLogin);
            finish();
        }
        if (mGoogleApiClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, 10);
                }
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent toLogin = new Intent(getBaseContext(), LoginActivity.class);
        if (id == R.id.logout) {
            if (session.getLoggedInStatus()) {
                logOut();
            } else {
                Toast.makeText(getBaseContext(), "Good Bye", Toast.LENGTH_SHORT).show();
                session.setGuest(false);
                startActivity(toLogin);
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        final Intent toLogin = new Intent(getBaseContext(), LoginActivity.class);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyCampusActivity.this);
        alertDialog.setTitle("Log out");
        alertDialog.setCancelable(true);
        alertDialog.setMessage("Do you want to log out");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "Good Bye " + session.getFullName(), Toast.LENGTH_SHORT).show();
                session.setLoggedInStatus(false);
                startActivity(toLogin);
                finish();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.create().show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.mycampus_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (isSortOpen) {
            collapseFabMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.myProfile) {
            if (session.getLoggedInStatus()) {
                Intent toProfile = new Intent(getBaseContext(), ProfileAcitivity.class);
                startActivity(toProfile);
            } else {
                Snackbar.make(SortFab, "You must be logged in to check your profile",
                        Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        } else if (id == R.id.toMyPlaces) {
            if (session.getLoggedInStatus()) {
                Intent toMyPlaces = new Intent(getBaseContext(), MyPlacesActivity.class);
                startActivity(toMyPlaces);
            } else {
                Snackbar.make(SortFab, "You must be logged in",
                        Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        } else if (id == R.id.nav_logout) {
            Intent toLogin = new Intent(getBaseContext(), LoginActivity.class);
            if (session.getLoggedInStatus()) {
                logOut();
            } else {
                Toast.makeText(getBaseContext(), "Good Bye", Toast.LENGTH_SHORT).show();
                session.setGuest(false);
                startActivity(toLogin);
                finish();
            }
        }
        DrawerLayout drawer = findViewById(R.id.mycampus_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onSortFabClick(View view) {
        if (isSortOpen)
            collapseFabMenu();
        else
            expandFabMenu();
    }

    private void expandFabMenu() {
        ViewCompat.animate(SortFab).rotation(-90.0F).withLayer().setDuration(300).setInterpolator(new OvershootInterpolator(5.0F)).start();
        RatingFab.setClickable(true);
        AlphaFab.setClickable(true);
        byLocationFab.setClickable(true);
        isSortOpen = true;
        RatingFab.setVisibility(View.VISIBLE);
        AlphaFab.setVisibility(View.VISIBLE);
        byLocationFab.setVisibility(View.VISIBLE);
        AlphaFab.startAnimation(fabOpenAnimation);
        RatingFab.startAnimation(fabOpenAnimation);
        byLocationFab.startAnimation(fabOpenAnimation);
    }

    private void collapseFabMenu() {
        ViewCompat.animate(SortFab).rotation(0.0F).withLayer().setDuration(300).setInterpolator(new OvershootInterpolator(5.0F)).start();
        RatingFab.setClickable(false);
        AlphaFab.setClickable(false);
        byLocationFab.setClickable(false);
        isSortOpen = false;
        if (isAlphaOpen) {
            AscAlphaFab.setClickable(false);
            AscAlphaFab.startAnimation(fabCloseAnimation);
            AscAlphaFab.setVisibility(View.INVISIBLE);
            DesAlphaFab.setClickable(false);
            DesAlphaFab.startAnimation(fabCloseAnimation);
            DesAlphaFab.setVisibility(View.INVISIBLE);
            isAlphaOpen = false;
        }
        if (isRatingOpen) {
            AscRatingFab.setClickable(false);
            AscRatingFab.startAnimation(fabCloseAnimation);
            AscRatingFab.setVisibility(View.INVISIBLE);
            DesRatingFab.setClickable(false);
            DesRatingFab.startAnimation(fabCloseAnimation);
            DesRatingFab.setVisibility(View.INVISIBLE);
            isRatingOpen = false;
        }
        byLocationFab.startAnimation(fabCloseAnimation);
        RatingFab.startAnimation(fabCloseAnimation);
        AlphaFab.startAnimation(fabCloseAnimation);
        RatingFab.setVisibility(View.INVISIBLE);
        AlphaFab.setVisibility(View.INVISIBLE);
        byLocationFab.setVisibility(View.INVISIBLE);
    }

    public void onAlphaFabClick(View view) {
        if (isAlphaOpen) {
            AlphaFab.startAnimation(fabClickClose);
            AscAlphaFab.setClickable(false);
            AscAlphaFab.startAnimation(fabCloseAnimation);
            AscAlphaFab.setVisibility(View.INVISIBLE);
            DesAlphaFab.setClickable(false);
            DesAlphaFab.startAnimation(fabCloseAnimation);
            DesAlphaFab.setVisibility(View.INVISIBLE);
            isAlphaOpen = false;
        } else {
            AlphaFab.startAnimation(fabClickOpen);
            AscAlphaFab.setClickable(true);
            AscAlphaFab.setVisibility(View.VISIBLE);
            AscAlphaFab.startAnimation(fabOpenAnimation);
            DesAlphaFab.setClickable(true);
            DesAlphaFab.setVisibility(View.VISIBLE);
            DesAlphaFab.startAnimation(fabOpenAnimation);
            isAlphaOpen = true;
        }
    }

    public void onRatingFabClick(View view) {
        if (isRatingOpen) {
            RatingFab.startAnimation(fabClickClose);
            AscRatingFab.setClickable(false);
            AscRatingFab.startAnimation(fabCloseAnimation);
            AscRatingFab.setVisibility(View.INVISIBLE);
            DesRatingFab.setClickable(false);
            DesRatingFab.startAnimation(fabCloseAnimation);
            DesRatingFab.setVisibility(View.INVISIBLE);
            isRatingOpen = false;
        } else {
            RatingFab.startAnimation(fabClickOpen);
            AscRatingFab.setClickable(true);
            AscRatingFab.setVisibility(View.VISIBLE);
            AscRatingFab.startAnimation(fabOpenAnimation);
            DesRatingFab.setClickable(true);
            DesRatingFab.setVisibility(View.VISIBLE);
            DesRatingFab.startAnimation(fabOpenAnimation);
            isRatingOpen = true;
        }
    }

    public void onAscAlphaFabClick(View view) {
        sortBy = SORT_ALPHABETICALLY;
        asc = ASCENDING;
        collapseFabMenu();
        for (int i = 0; i < 11; i++)
            new sendToServer().execute(i);
    }

    public void onDesAlphaFabClick(View view) {
        sortBy = SORT_ALPHABETICALLY;
        asc = DESCENDING;
        collapseFabMenu();
        for (int i = 0; i < 11; i++)
            new sendToServer().execute(i);
    }

    public void onAscRatingFabClick(View view) {
        sortBy = SORT_BY_RATING;
        asc = ASCENDING;
        collapseFabMenu();
        for (int i = 0; i < 11; i++)
            new sendToServer().execute(i);
    }

    public void onDesRatingFabClick(View view) {
        sortBy = SORT_BY_RATING;
        asc = DESCENDING;
        collapseFabMenu();
        for (int i = 0; i < 11; i++)
            new sendToServer().execute(i);
    }

    public void onMapClicked(View view) {
        MapFab.setAnimation(fabClickOpen);
        MapFab.setAnimation(fabClickClose);
        String category = catCollName[viewPager.getCurrentItem()];
        Intent toMaps = new Intent(MyCampusActivity.this, MapsActivity.class);
        toMaps.putExtra("isCategory", true);
        toMaps.putExtra("isMyPlace", false);
        toMaps.putExtra("isPlaceDetails", false);
        toMaps.putExtra("category", category);
        startActivity(toMaps);
    }

    public void onByLocationClick(View view) {
        sortBy = "location";
        collapseFabMenu();
        for (int i = 0; i < 11; i++) {
            new sendToServer().execute(i);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, 10);
            }
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    protected void onPause() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onConnected(Bundle.EMPTY);
                }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        //Toast.makeText(getBaseContext(), "new location :"+String.valueOf(location.getLatitude()),Toast.LENGTH_SHORT).show();
    }

    public static class CategoryFragment extends android.support.v4.app.Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public CategoryFragment() {
        }

        public static CategoryFragment newInstance(int sectionNumber) {
            CategoryFragment fragment = new CategoryFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View theView = inflater.inflate(R.layout.category_fragment, container, false);
            theListView[getArguments().getInt(ARG_SECTION_NUMBER)] = theView.findViewById(
                    R.id.mycampus_listview);
            theListView[getArguments().getInt(ARG_SECTION_NUMBER)].setAdapter(
                    theAdapter[getArguments().getInt(ARG_SECTION_NUMBER)]);
            theListView[getArguments().getInt(ARG_SECTION_NUMBER)].setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            CategoryList cl = theAdapter[getArguments().getInt(ARG_SECTION_NUMBER)].getItem(position);
                            session.setCategory(catCollName[getArguments().getInt(ARG_SECTION_NUMBER)]);
                            session.setCatItem(cl.getName());
                            startActivity(toPlaceDetails);
                        }
                    });
            return theView;
        }

        @Override
        public void onResume() {
            theListView[getArguments().getInt(ARG_SECTION_NUMBER)].setAdapter(
                    theAdapter[getArguments().getInt(ARG_SECTION_NUMBER)]);
            super.onResume();
        }
    }

    public class myFragmentPagerAdapter extends FragmentPagerAdapter {

        public myFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 11;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Canteen";
                case 1:
                    return "Restaurants";
                case 2:
                    return "General Stores";
                case 3:
                    return "Clinics & Medicals";
                case 4:
                    return "Banks and ATM";
                case 5:
                    return "Saloons";
                case 6:
                    return "PhotoCopy Centre";
                case 7:
                    return "Laundry";
                case 8:
                    return "Hostels";
                case 9:
                    return "Mess";
                case 10:
                    return "Book Stores";
            }
            return super.getPageTitle(position);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            CategoryFragment cf = CategoryFragment.newInstance(position);
            new sendToServer().execute(position);
            return cf;
        }
    }

    public class MyAdapter extends ArrayAdapter<CategoryList> {


        public MyAdapter(Context context, List<CategoryList> resource) {
            super(context, R.layout.mycampus_listview, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View theView = LayoutInflater.from(getContext()).inflate(R.layout.mycampus_listview, parent, false);
            TextView tv = theView.findViewById(R.id.tv_listview);
            CategoryList cl = getItem(position);
            tv.setText(cl.getName());
            RatingBar rb = theView.findViewById(R.id.rating);
            rb.setRating(cl.getRating());
            return theView;
        }
    }

    public class CategoryList {
        private String name;
        private float rating;

        public CategoryList(String name, float rating) {
            this.name = name;
            this.rating = rating;
        }

        public String getName() {
            return name;
        }

        public float getRating() {
            return rating;
        }

        public void setRating(float rating) {
            this.rating = rating;
        }
    }

    private class sendToServer extends AsyncTask<Integer, Void, String> {
        int index;

        @Override
        protected String doInBackground(Integer... params) {
            index = params[0];
            String link = getString(R.string.link_to_server) + "get_category_list.php?category=" + catCollName[index]
                    + "&sortby=" + sortBy + "&asc=" + asc;
            Location l;
            if (Objects.equals(sortBy, "location")) {
                if (mCurrentLocation != null) {
                    l = mCurrentLocation;
                } else {
                    l = mLastLocation;
                }
                link += "&lat=" + String.valueOf(l.getLatitude()) + "&long=" + String.valueOf(l.getLongitude());
            } else {
                link += "&lat=&long=";
            }
            return (new PostToServer(link).postToServer());
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                try {
                    JSONObject jObject = new JSONObject(s);
                    JSONArray rList = jObject.getJSONArray("stuff");
                    int rlen = rList.length();
                    List<CategoryList> cList = new ArrayList<CategoryList>();
                    for (int i = 0; i < rlen; i++) {
                        JSONObject jsonObject = rList.getJSONObject(i);
                        String nm = jsonObject.getString("name");
                        float rt = (float) jsonObject.getDouble("rating");
                        cList.add(new CategoryList(nm, rt));
                    }
                    theAdapter[index] = new MyAdapter(getBaseContext(), cList);
                    if (theListView[index] != null)
                        theListView[index].setAdapter(theAdapter[index]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else
                Snackbar.make(SortFab, "Can't Access the Server", Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show();
        }
    }
}
