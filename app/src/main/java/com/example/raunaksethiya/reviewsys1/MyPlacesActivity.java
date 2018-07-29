package com.example.raunaksethiya.reviewsys1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyPlacesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final static String SORT_ALPHABETICALLY = "name";
    private final static String SORT_BY_RATING = "rating";
    private final static String ASCENDING = "asc";
    private final static String DESCENDING = "des";
    private static Session session;
    private static MyAdapter[] theAdapter;
    private static String[] catCollName;
    private static Intent toPlaceDetails;
    private static ListView[] theListView;
    private static TextView[] theTextView;
    boolean isSortOpen, isAlphaOpen, isRatingOpen;
    private ViewPager viewPager;
    private String sortBy;
    private String asc;
    private FloatingActionButton SortFab, AlphaFab, RatingFab, AscAlphaFab, DesAlphaFab, AscRatingFab, DesRatingFab, MapFab;
    private Animation fabOpenAnimation, fabCloseAnimation, fabClickOpen, fabClickClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_places);
        session = new Session(getBaseContext());
        Toolbar toolbar = findViewById(R.id.myplaces_toolbar);
        setSupportActionBar(toolbar);

        sortBy = null;
        asc = null;
        catCollName = new String[]{"canteen", "restaurants", "general_stores", "clinic_medical",
                "bank_atm", "saloons", "photocopy", "laundry", "hostels", "mess", "book_stores"};
        theAdapter = new MyAdapter[11];
        theListView = new ListView[11];
        theTextView = new TextView[11];
        toPlaceDetails = new Intent(MyPlacesActivity.this, PlaceDetailsActivity.class);

        DrawerLayout myCampusDrawer = findViewById(R.id.myplaces_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, myCampusDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        myCampusDrawer.setDrawerListener(toggle);
        toggle.syncState();

        viewPager = findViewById(R.id.myplaces_viewpager);
        viewPager.setAdapter(new myFragmentPagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = findViewById(R.id.myplaces_tab);
        tabLayout.setupWithViewPager(viewPager);

        MapFab = findViewById(R.id.toMap);
        SortFab = findViewById(R.id.baseFloatingActionButton);
        AlphaFab = findViewById(R.id.byAlphaFab);
        RatingFab = findViewById(R.id.byRatingFab);
        AscAlphaFab = findViewById(R.id.ascAlphaFab);
        DesAlphaFab = findViewById(R.id.desAlphaFab);
        AscRatingFab = findViewById(R.id.ascRatingFab);
        DesRatingFab = findViewById(R.id.desRatingFab);
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
    }

    @Override
    protected void onResume() {
        for (int i = 0; i < 11; i++)
            new sendToServer().execute(i);
        if (isSortOpen)
            collapseFabMenu();
        super.onResume();
    }

    private void logOut() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyPlacesActivity.this);
        alertDialog.setTitle("Log out");
        alertDialog.setCancelable(true);
        alertDialog.setMessage("Do you want to log out");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "Good Bye " + session.getFullName(), Toast.LENGTH_SHORT).show();
                session.setLoggedInStatus(false);
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
        DrawerLayout drawer = findViewById(R.id.myplaces_drawer_layout);
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
                finish();
            } else {
                Snackbar.make(SortFab, "You must be logged in to check your profile",
                        Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        } else if (id == R.id.toMycampus) {
            finish();
        } else if (id == R.id.nav_logout) {
            if (session.getLoggedInStatus()) {
                logOut();
            } else {
                Toast.makeText(getBaseContext(), "Good Bye", Toast.LENGTH_SHORT).show();
                session.setGuest(false);
                finish();
            }
        }
        DrawerLayout drawer = findViewById(R.id.myplaces_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onSortFabClick1(View view) {
        if (isSortOpen)
            collapseFabMenu();
        else
            expandFabMenu();
    }

    private void expandFabMenu() {
        ViewCompat.animate(SortFab).rotation(-90.0F).withLayer().setDuration(300).setInterpolator(new OvershootInterpolator(5.0F)).start();
        RatingFab.setClickable(true);
        AlphaFab.setClickable(true);
        isSortOpen = true;
        RatingFab.setVisibility(View.VISIBLE);
        AlphaFab.setVisibility(View.VISIBLE);
        AlphaFab.startAnimation(fabOpenAnimation);
        RatingFab.startAnimation(fabOpenAnimation);
    }

    private void collapseFabMenu() {
        ViewCompat.animate(SortFab).rotation(0.0F).withLayer().setDuration(300).setInterpolator(new OvershootInterpolator(5.0F)).start();
        RatingFab.setClickable(false);
        AlphaFab.setClickable(false);
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
        RatingFab.startAnimation(fabCloseAnimation);
        AlphaFab.startAnimation(fabCloseAnimation);
        RatingFab.setVisibility(View.INVISIBLE);
        AlphaFab.setVisibility(View.INVISIBLE);
    }

    public void onAlphaFabClick1(View view) {
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

    public void onRatingFabClick1(View view) {
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

    public void onAscAlphaFabClick1(View view) {
        sortBy = SORT_ALPHABETICALLY;
        asc = ASCENDING;
        collapseFabMenu();
        for (int i = 0; i < 11; i++)
            new sendToServer().execute(i);
    }

    public void onDesAlphaFabClick1(View view) {
        sortBy = SORT_ALPHABETICALLY;
        asc = DESCENDING;
        collapseFabMenu();
        for (int i = 0; i < 11; i++)
            new sendToServer().execute(i);
    }

    public void onAscRatingFabClick1(View view) {
        sortBy = SORT_BY_RATING;
        asc = ASCENDING;
        collapseFabMenu();
        for (int i = 0; i < 11; i++)
            new sendToServer().execute(i);
    }

    public void onDesRatingFabClick1(View view) {
        sortBy = SORT_BY_RATING;
        asc = DESCENDING;
        collapseFabMenu();
        for (int i = 0; i < 11; i++)
            new sendToServer().execute(i);
    }

    public void onMapClicked1(View view) {
        MapFab.setAnimation(fabClickClose);
        MapFab.setAnimation(fabClickOpen);
        String category = catCollName[viewPager.getCurrentItem()];
        Intent toMaps = new Intent(MyPlacesActivity.this, MapsActivity.class);
        toMaps.putExtra("isCategory", false);
        toMaps.putExtra("isMyPlace", true);
        toMaps.putExtra("isPlaceDetails", false);
        toMaps.putExtra("category", category);
        startActivity(toMaps);
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
            theTextView[getArguments().getInt(ARG_SECTION_NUMBER)] = theView.findViewById(
                    R.id.tv_is_res_found);
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
            if (theAdapter[getArguments().getInt(ARG_SECTION_NUMBER)] != null) {
                if (theAdapter[getArguments().getInt(ARG_SECTION_NUMBER)].getCount() == 0) {
                    theTextView[getArguments().getInt(ARG_SECTION_NUMBER)].setText("Nothing Found");
                }
            }
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
            String link = getString(R.string.link_to_server) + "get_my_places_list.php?category=" + catCollName[index]
                    + "&sortby=" + sortBy + "&asc=" + asc + "&user=" + session.getUsername();
            return (new PostToServer(link).postToServer());
        }

        @Override
        protected void onPostExecute(String s) {
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
                if (cList.size() == 0) {
                    if (theTextView[index] != null)
                        theTextView[index].setText("Nothing Found");
                }
                theAdapter[index] = new MyAdapter(getBaseContext(), cList);
                if (theListView[index] != null)
                    theListView[index].setAdapter(theAdapter[index]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
