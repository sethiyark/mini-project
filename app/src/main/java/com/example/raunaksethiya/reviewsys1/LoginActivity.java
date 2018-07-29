package com.example.raunaksethiya.reviewsys1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private ColorStateList prevCSL;
    private ColorStateList ivCSL;
    private JSONObject responseFromServer;
    private Intent toMyCampus;
    private Session session;
    private Dialog dialogAskPhone;
    private Dialog dialogPassOTP;
    private Dialog dialogNewPass;
    private EditText enteredEmail;
    private EditText enteredOTP;
    private EditText new_password;
    private EditText new_confirm_password;
    private boolean launch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Login");

        username = findViewById(R.id.UserID);
        password = findViewById(R.id.Password);
        TextView forgot_password = findViewById(R.id.ForgotPassword);
        TextView skip = findViewById(R.id.SkipLogin);

        session = new Session(getBaseContext());
        launch = true;

        toMyCampus = new Intent(getBaseContext(), MyCampusActivity.class);

        View viewAskPhone = (LayoutInflater.from(LoginActivity.this)).inflate(R.layout.ask_phone, null);
        AlertDialog.Builder AskPhone = new AlertDialog.Builder(LoginActivity.this);
        AskPhone.setView(viewAskPhone);
        enteredEmail = viewAskPhone.findViewById(R.id.email_pass_change);
        AskPhone.setCancelable(true);
        enteredEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (enteredEmail.getBackgroundTintList() != prevCSL)
                        enteredEmail.setBackgroundTintList(prevCSL);
                }
                return false;
            }
        });
        dialogAskPhone = AskPhone.create();

        View viewPassOTP = (LayoutInflater.from(LoginActivity.this)).inflate(R.layout.pass_change_otp, null);
        AlertDialog.Builder PassOTP = new AlertDialog.Builder(LoginActivity.this);
        PassOTP.setView(viewPassOTP);
        enteredOTP = viewPassOTP.findViewById(R.id.otp_pass_edit);
        PassOTP.setCancelable(true);
        enteredOTP.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (enteredOTP.getBackgroundTintList() != prevCSL)
                        enteredOTP.setBackgroundTintList(prevCSL);
                }
                return false;
            }
        });
        dialogPassOTP = PassOTP.create();

        View viewNewPass = (LayoutInflater.from(LoginActivity.this)).inflate(R.layout.new_pass, null);
        AlertDialog.Builder NewPass = new AlertDialog.Builder(LoginActivity.this);
        NewPass.setView(viewNewPass);
        new_password = viewNewPass.findViewById(R.id.new_password);
        new_confirm_password = viewNewPass.findViewById(R.id.new_confirm_password);
        NewPass.setCancelable(true);
        new_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (new_password.getBackgroundTintList() != prevCSL)
                        new_password.setBackgroundTintList(prevCSL);
                }
                return false;
            }
        });
        new_confirm_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (new_confirm_password.getBackgroundTintList() != prevCSL)
                        new_confirm_password.setBackgroundTintList(prevCSL);
                }
                return false;
            }
        });
        dialogNewPass = NewPass.create();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            prevCSL = username.getBackgroundTintList();
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

        username.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (username.getBackgroundTintList() != prevCSL)
                        username.setBackgroundTintList(prevCSL);
                }
                return false;
            }
        });

        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (password.getBackgroundTintList() != prevCSL)
                        password.setBackgroundTintList(prevCSL);
                }
                return false;
            }
        });

        forgot_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (session.getPassResetStatus() == 0) {
                    dialogAskPhone.show();
                } else if (session.getPassResetStatus() == 1) {
                    dialogPassOTP.show();
                } else if (session.getPassResetStatus() == 2) {
                    dialogNewPass.show();
                }
                return false;
            }
        });

        skip.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
                alertDialog.setTitle("Warning!!!");
                alertDialog.setCancelable(true);
                alertDialog.setMessage("Do you want to skip");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        session.setPassResetStatus(0);
                        session.setGuest(true);
                        startActivity(toMyCampus);
                        finish();
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.create().show();
                return false;
            }
        });

    }

    public void OpenRegistration(View view) {
        Intent toRegister = new Intent(this, RegisterActivity.class);
        startActivity(toRegister);
    }

    public void VerifyLogin(View view) {

        if (verifyFields()) {
            String uname = username.getText().toString();
            String pword = password.getText().toString();
            username.setEnabled(false);
            password.setEnabled(false);
            new sendToServer().execute(uname, pword);
        }

    }

    private boolean verifyFields() {
        boolean flag = true;
        if (username.getText().toString().trim().length() == 0) {
            flag = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                username.setBackgroundTintList(ivCSL);
            }
            Toast.makeText(getBaseContext(), "Enter Username to Continue", Toast.LENGTH_SHORT).show();
        }
        if (password.getText().toString().trim().length() == 0) {
            flag = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                password.setBackgroundTintList(ivCSL);
            }
            Toast.makeText(getBaseContext(), "Enter Password to Continue", Toast.LENGTH_SHORT).show();
        }
        return flag;
    }

    public void askPhone(View view) {
        String phoneEntered = enteredEmail.getText().toString();
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(phoneEntered).matches() && phoneEntered.trim().length() != 0) {
            new sendPhoneToVerify().execute(phoneEntered);
        } else {
            Toast.makeText(getBaseContext(), "Enter Valid Email", Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                enteredEmail.setBackgroundTintList(ivCSL);
            }
        }
    }

    public void otpPasswordChange(View view) {
        String otpEntered = enteredOTP.getText().toString();
        if (otpEntered.trim().length() != 0) {
            new sentOTPtoVerify().execute(otpEntered);
        } else {
            Toast.makeText(getBaseContext(), "Enter OTP to continue", Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                enteredOTP.setBackgroundTintList(ivCSL);
            }
        }
    }

    public void newPassword(View view) {
        String enteredPassword = new_password.getText().toString();
        String enteredConfirm = new_confirm_password.getText().toString();
        if (enteredPassword.trim().length() == 0) {
            Toast.makeText(getBaseContext(), "Enter Password", Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                new_password.setBackgroundTintList(ivCSL);
                new_confirm_password.setBackgroundTintList(ivCSL);
            }
        } else if (enteredConfirm.trim().length() == 0) {
            Toast.makeText(getBaseContext(), "Re-Enter password", Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                new_confirm_password.setBackgroundTintList(ivCSL);
            }
        } else if (!Objects.equals(enteredPassword, enteredConfirm)) {
            Toast.makeText(getBaseContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                new_password.setBackgroundTintList(ivCSL);
                new_confirm_password.setBackgroundTintList(ivCSL);
            }
        } else {
            new sendNewPassword().execute(enteredPassword);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(getBaseContext(),"you are back",Toast.LENGTH_LONG).show();
        if (launch) {
            if (session.getLoggedInStatus()) {
                launch = false;
                Toast.makeText(getBaseContext(), "Welcome " + session.getFullName(), Toast.LENGTH_LONG).show();
                startActivity(toMyCampus);
                finish();
            }
            if (session.getGuest()) {
                launch = false;
                Toast.makeText(getBaseContext(), "Logged in as Guest", Toast.LENGTH_SHORT).show();
                startActivity(toMyCampus);
                finish();
            }
        } else {
            finish();
        }
    }

    private class sendToServer extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String username = null, password = null;
            try {
                username = URLEncoder.encode(params[0], "UTF-8");
                password = URLEncoder.encode(new MD5Encoder(params[1]).encodeToMD5(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String link = getString(R.string.link_to_server) + "login.php?" +
                    "username=" + username + "&password=" + password;
            try {
                responseFromServer = new JSONObject(new PostToServer(link).postToServer());
                return responseFromServer.getString("result");
            } catch (JSONException e1) {
                e1.printStackTrace();
                return "Exception: " + e1.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            username.setEnabled(true);
            password.setEnabled(true);
            //Toast.makeText(getBaseContext(),s,Toast.LENGTH_LONG).show();
            if (Objects.equals(s, "no user")) {
                Toast.makeText(getBaseContext(), "User doesn't Exist", Toast.LENGTH_LONG).show();
            }
            if (Objects.equals(s, "wrong password")) {
                Toast.makeText(getBaseContext(), "Wrong Password", Toast.LENGTH_LONG).show();
            }
            if (Objects.equals(s, "success")) {
                //Toast.makeText(getBaseContext(),"Successfully Logged In",Toast.LENGTH_SHORT).show();
                try {
                    JSONArray jsonArray = responseFromServer.getJSONArray("stuff");
                    session.setFullName(jsonArray.getJSONObject(0).getString("name"));
                    session.setEmail(jsonArray.getJSONObject(0).getString("email"));
                    session.setPhone(jsonArray.getJSONObject(0).getString("phone_num"));
                    session.setUsername(jsonArray.getJSONObject(0).getString("username"));
                    session.setLoggedInStatus(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                Toast.makeText(getBaseContext(), "Welcome " + session.getFullName(), Toast.LENGTH_LONG).show();
                if (session.getLoggedInStatus()) {
                    session.setPassResetStatus(0);
                    session.setGuest(false);
                    startActivity(toMyCampus);
                    finish();
                }
            }
        }
    }

    private class sendPhoneToVerify extends AsyncTask<String, Void, String> {
        String enteredPhon;

        @Override
        protected String doInBackground(String... params) {
            enteredPhon = params[0];
            String link = getString(R.string.link_to_server) + "forgot_password.php?email=" + enteredPhon;
            return (new PostToServer(link).postToServer());
        }

        @Override
        protected void onPostExecute(String s) {
            if (Objects.equals(s, "unregistered")) {
                Toast.makeText(getBaseContext(), "Email not Registered", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    enteredEmail.setBackgroundTintList(ivCSL);
                }
            } else if (Objects.equals(s, "otp sent")) {
                Toast.makeText(getBaseContext(), "OTP Sent to " + session.getEmail(), Toast.LENGTH_SHORT).show();
                session.setPassResetStatus(1);
                dialogAskPhone.cancel();
                session.setEmail(enteredPhon);
                dialogPassOTP.show();
            }
        }
    }

    private class sentOTPtoVerify extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String enteredOTP = params[0];
            String enteredPhone = session.getEmail();
            String link = getString(R.string.link_to_server) + "forgot_password_otp.php?otp=" + enteredOTP + "&email=" + enteredPhone;
            return (new PostToServer(link).postToServer());
        }

        @Override
        protected void onPostExecute(String s) {
            if (Objects.equals(s, "success")) {
                Toast.makeText(getBaseContext(), "Email Verified", Toast.LENGTH_SHORT).show();
                session.setPassResetStatus(2);
                dialogPassOTP.cancel();
                dialogNewPass.show();
            } else if (Objects.equals(s, "fail")) {
                Toast.makeText(getBaseContext(), "Invalid OTP", Toast.LENGTH_LONG).show();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
                alertDialog.setTitle("Invalid OTP");
                alertDialog.setCancelable(true);
                alertDialog.setMessage("Do you want to Resend OTP?");
                alertDialog.setPositiveButton("Resend OTP", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getBaseContext(), "OTP Resent", Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        }
    }

    private class sendNewPassword extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String newPassword = null;
            try {
                newPassword = URLEncoder.encode(new MD5Encoder(params[0]).encodeToMD5(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String link = getString(R.string.link_to_server) + "password_new.php?password=" + newPassword + "&email=" + session.getEmail();
            return (new PostToServer(link).postToServer());
        }

        @Override
        protected void onPostExecute(String s) {
            if (Objects.equals(s, "success")) {
                Toast.makeText(getBaseContext(), "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                session.setPassResetStatus(0);
                dialogNewPass.cancel();
            } else if (Objects.equals(s, "fail")) {
                Toast.makeText(getBaseContext(), "Error!!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
