package com.example.raunaksethiya.reviewsys1;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

public class ProfileAcitivity extends AppCompatActivity {

    EditText username, name, email, phone;
    Session session;
    private ColorStateList prevCSL;
    private ColorStateList ivCSL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_acitivity);

        session = new Session(getBaseContext());

        username = findViewById(R.id.username_profile);
        name = findViewById(R.id.name_profile);
        email = findViewById(R.id.email_profile);
        phone = findViewById(R.id.phone_profile);
        username.setText(session.getUsername());
        name.setText(session.getFullName());
        email.setText(session.getEmail());
        phone.setText(session.getPhone());
        username.setEnabled(false);
        email.setEnabled(false);
        Snackbar.make(username, "Username and Email cannot be changed", Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show();
        name.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (name.getBackgroundTintList() != prevCSL)
                        name.setBackgroundTintList(prevCSL);
                }
                return false;
            }
        });

        phone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (phone.getBackgroundTintList() != prevCSL)
                        phone.setBackgroundTintList(prevCSL);
                }
                return false;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            prevCSL = name.getBackgroundTintList();
        }
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled}, // enabled
                new int[]{-android.R.attr.state_enabled}, // disabled
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_pressed},  // pressed
                new int[]{-android.R.attr.state_pressed}  // not pressed
        };
        int[] colors = new int[]{
                Color.RED,
                Color.RED,
                Color.RED,
                Color.MAGENTA,
                Color.RED
        };
        ivCSL = new ColorStateList(states, colors);
    }

    public void goBackFromProfile(View view) {
        finish();
    }

    public void updateProfile(View view) {
        if (verifyFields()) {
            String name = this.name.getText().toString();
            String phone = this.phone.getText().toString();
            new sendToServer().execute(name, phone);
        }
    }

    private boolean verifyFields() {
        boolean flag = true;
        if (name.getText().toString().trim().length() == 0) {
            flag = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                name.setBackgroundTintList(ivCSL);
            }
            Toast.makeText(ProfileAcitivity.this, "Enter Valid Name", Toast.LENGTH_SHORT).show();
        }
        if (phone.getText().toString().trim().length() != 10) {
            flag = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                phone.setBackgroundTintList(ivCSL);
            }
            Toast.makeText(ProfileAcitivity.this, "Enter valid Phone Number", Toast.LENGTH_SHORT).show();
        }
        if (Objects.equals(name.getText().toString(), session.getFullName())
                && Objects.equals(phone.getText().toString(), session.getPhone())) {
            flag = false;
            Snackbar.make(name, "No Changes to Update", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }
        return flag;
    }


    private class sendToServer extends AsyncTask<String, Void, String> {
        String name, phone;

        @Override
        protected String doInBackground(String... params) {
            name = params[0];
            phone = params[1];
            try {
                String username = URLEncoder.encode(session.getUsername(), "UTF-8");
                String name = URLEncoder.encode(params[0], "UTF-8");
                String phone = params[1];
                String link = getString(R.string.link_to_server) + "update_profile.php?username="
                        + username + "&name=" + name + "&phone=" + phone;
                return (new PostToServer(link).postToServer());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (Objects.equals(s, "phone exists")) {
                Toast.makeText(getBaseContext(), "Phone Number Already Exists", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ProfileAcitivity.this.phone.setBackgroundTintList(ivCSL);
                }
            } else if (Objects.equals(s, "success")) {
                Toast.makeText(getBaseContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                session.setFullName(name);
                session.setPhone(phone);
            }
        }
    }
}
