package com.example.raunaksethiya.reviewsys1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlaceDetailsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Session session;
    private float myStars;
    private String myComment;
    private List<Review> userReviews;
    private EditText editText;
    private RatingBar ratingBar;
    private android.support.v7.widget.RecyclerView theRecyclerView;
    private FloatingActionButton myReview, toMap;
    private Animation fabOpenAnimation, fabCloseAnimation;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new Session(getBaseContext());
        setTitle(session.getCatItem());

        theRecyclerView = findViewById(R.id.details_recyclerview);
        theRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userReviews = new ArrayList<Review>();

        DrawerLayout placeDetailsDrawer = findViewById(R.id.placedetails_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, placeDetailsDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        placeDetailsDrawer.setDrawerListener(toggle);
        toggle.syncState();

        myReview = findViewById(R.id.fab);
        toMap = findViewById(R.id.toMapDetails);
        fabOpenAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_clicked);
        fabCloseAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_clicked_close);

        View reviewView = (LayoutInflater.from(PlaceDetailsActivity.this)).inflate(R.layout.user_review, null);
        editText = reviewView.findViewById(R.id.user_comment);
        ratingBar = reviewView.findViewById(R.id.user_stars);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

            }
        });
        AlertDialog.Builder reviewDialog = new AlertDialog.Builder(PlaceDetailsActivity.this);
        reviewDialog.setTitle("Review");
        reviewDialog.setCancelable(false);
        reviewDialog.setMessage("Submit your Review ");
        reviewDialog.setView(reviewView);
        reviewDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                myComment = editText.getText().toString();
                myStars = ratingBar.getRating();
                new sendMyReview().execute();
            }
        });
        reviewDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final Dialog rDialog = reviewDialog.create();

        myReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.getLoggedInStatus()) {
                    rDialog.show();
                    editText.setText(myComment);
                    ratingBar.setRating(myStars);
                } else {
                    Snackbar.make(v, "You must be logged in to make your review", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });

        new sendToServer().execute();

        NavigationView navigationView = findViewById(R.id.nav_view_details);
        navigationView.setNavigationItemSelectedListener(this);

        View v = navigationView.inflateHeaderView(R.layout.nav_header_main);

        if (session.getLoggedInStatus()) {
            ((TextView) v.findViewById(R.id.nav_name)).setText(session.getFullName());
            ((TextView) v.findViewById(R.id.nav_email)).setText(session.getEmail());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.placedetails_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
                Snackbar.make(editText, "You must be logged in to check your profile",
                        Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        } else if (id == R.id.toMycampus) {
            finish();
        } else if (id == R.id.toMyPlaces) {
            if (session.getLoggedInStatus()) {
                Intent toMyPlaces = new Intent(getBaseContext(), MyPlacesActivity.class);
                startActivity(toMyPlaces);
                finish();
            } else {
                Snackbar.make(editText, "You must be logged in",
                        Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        } else if (id == R.id.nav_logout) {
            if (session.getLoggedInStatus()) {
                logOut();
            } else {
                Toast.makeText(getBaseContext(), "Good Bye", Toast.LENGTH_SHORT).show();
                session.setGuest(false);
                finish();
            }
        }
        DrawerLayout drawer = findViewById(R.id.placedetails_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void toMapDetails(View view) {
        toMap.setAnimation(fabCloseAnimation);
        toMap.setAnimation(fabOpenAnimation);
        String category = session.getCategory();
        String name = session.getCatItem();
        Intent totheMaps = new Intent(PlaceDetailsActivity.this, MapsActivity.class);
        totheMaps.putExtra("isCategory", false);
        totheMaps.putExtra("isMyPlace", false);
        totheMaps.putExtra("isPlaceDetails", true);
        totheMaps.putExtra("category", category);
        totheMaps.putExtra("name", name);
        startActivity(totheMaps);

    }

    private void logOut() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PlaceDetailsActivity.this);
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

    private class sendToServer extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String link = null;
            try {
                link = getString(R.string.link_to_server) + "get_details.php?category=" +
                        session.getCategory() + "&name=" + URLEncoder.encode(session.getCatItem(), "UTF-8") + "&username=";
                if (session.getLoggedInStatus()) {
                    link = link + URLEncoder.encode(session.getUsername(), "UTF-8");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return (new PostToServer(link).postToServer());
        }

        @Override
        protected void onPostExecute(String s) {
            //Toast.makeText(getBaseContext(),s,Toast.LENGTH_LONG).show();
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray details = jsonObject.getJSONArray("details");
                JSONArray udetails = jsonObject.getJSONArray("udetails");
                JSONArray avgstar = jsonObject.getJSONArray("avgstar");
                userReviews.clear();
                if (udetails.length() != 0) {
                    myStars = (float) (udetails.getJSONObject(0).getDouble("stars"));
                    myComment = udetails.getJSONObject(0).getString("comment");
                } else {
                    myStars = 0;
                    myComment = "";
                }
                if (myStars == 0) {
                    myReview.setImageResource(R.drawable.review_unset);
                } else {
                    myReview.setImageResource(R.drawable.review_set);
                }
                if (avgstar.length() != 0) {
                    String tv = "by " + String.valueOf(avgstar.getJSONObject(0).getJSONObject("rating").getInt("count")) + " users";
                    ((TextView) findViewById(R.id.num_rated)).setText(tv);
                    float str = (float) avgstar.getJSONObject(0).getJSONObject("rating").getDouble("stars");
                    //Toast.makeText(getBaseContext(), String.valueOf(str), Toast.LENGTH_LONG).show();
                    ((RatingBar) findViewById(R.id.rating_detail)).setRating(str);
                } else {
                    String tv = "No User rated yet";
                    ((TextView) findViewById(R.id.num_rated)).setText(tv);
                }
                JSONObject jObj = details.getJSONObject(0);
                String des = jObj.getString("description");
                String picSource = jObj.getString("picture");
                if (!Objects.equals(picSource, "")) {
                    new setPicture(picSource).execute();
                }
                if (Objects.equals(des, "")) {
                    ((TextView) findViewById(R.id.tv_description)).setText("No description Yet");
                } else {
                    ((TextView) findViewById(R.id.tv_description)).setText(des);
                }
                JSONArray jArr = jObj.getJSONArray("review");
                for (int i = 0; i < jArr.length(); i++) {
                    String user = jArr.getJSONObject(i).getString("username");
                    String comment = jArr.getJSONObject(i).getString("comment");
                    float str = (float) jArr.getJSONObject(i).getDouble("stars");
                    userReviews.add(new Review(user, comment, str));
                }
                count = userReviews.size();
                for (int i = 0; i < userReviews.size(); i++) {
                    new getName().execute(i);
                }
            } catch (JSONException e) {
                Toast.makeText(getBaseContext(), "Exception", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private class Review {
        private String username, comment;
        private float stars;

        public Review(String username, String comment, float stars) {
            this.username = username;
            this.comment = comment;
            this.stars = stars;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getComment() {
            return comment;
        }

        public float getStars() {
            return stars;
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<Review> data;

        public MyAdapter(List<Review> theData) {
            data = theData;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View theView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.details_listview, parent, false);

            return (new ViewHolder(theView));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Review rv = data.get(position);
            holder.user.setText(rv.getUsername());
            if (!Objects.equals(rv.getComment(), ""))
                holder.comment.setText(rv.getComment());
            else
                holder.comment.setText("No Comment Yet");
            holder.rb.setRating(rv.getStars());
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView user, comment;
            public RatingBar rb;

            public ViewHolder(View theView) {
                super(theView);
                user = theView.findViewById(R.id.username);
                comment = theView.findViewById(R.id.comment);
                rb = theView.findViewById(R.id.user_rating);
            }
        }
    }

    private class sendMyReview extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                String link = getString(R.string.link_to_server) + "set_review.php?category=" + session.getCategory()
                        + "&username=" + URLEncoder.encode(session.getUsername(), "UTF-8") + "&name="
                        + URLEncoder.encode(session.getCatItem(), "UTF-8") + "&rating=" + String.valueOf(myStars)
                        + "&comment=" + URLEncoder.encode(myComment, "UTF-8");

                return (new PostToServer(link).postToServer());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (Objects.equals(s, "success")) {
                Toast.makeText(getBaseContext(), "Thanks for Review " + session.getFullName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(), "Your Review is invalid", Toast.LENGTH_LONG).show();
            }
            new sendToServer().execute();
        }
    }

    private class getName extends AsyncTask<Integer, Void, String> {
        int index;

        @Override
        protected String doInBackground(Integer... params) {
            index = params[0];
            String link = getString(R.string.link_to_server) + "get_full_name.php?username=";
            String uname = userReviews.get(index).getUsername();
            try {
                uname = URLEncoder.encode(uname, "UTF-8");
                link = link + uname;
                return (new PostToServer(link).postToServer());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                Review rv = userReviews.get(index);
                rv.setUsername(s);
                userReviews.set(index, rv);
                count--;
            }
            if (count == 0) {
                RecyclerView.Adapter adapter = new MyAdapter(userReviews);
                theRecyclerView.setAdapter(adapter);
            }
        }
    }

    private class setPicture extends AsyncTask<Void, Void, Bitmap> {
        String src;

        public setPicture(String picSource) {
            src = picSource.replace(" ", "%20");
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                String link = getString(R.string.link_to_server) + src;
                HttpURLConnection conn;

                URL url = new URL(link);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream input = conn.getInputStream();
                return (BitmapFactory.decodeStream(input));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //Toast.makeText(getBaseContext(),src,Toast.LENGTH_LONG).show();
            if (bitmap != null) {
                ((ImageView) findViewById(R.id.bg_image_view)).setImageBitmap(bitmap);
            } else {
                Toast.makeText(getBaseContext(), "oops", Toast.LENGTH_LONG).show();
            }
        }
    }
}
