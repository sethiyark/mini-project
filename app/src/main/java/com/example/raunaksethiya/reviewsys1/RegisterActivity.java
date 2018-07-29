package com.example.raunaksethiya.reviewsys1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

/**
 * Created by Raunak Sethiya on 10-Sep-16.
 */
public class RegisterActivity extends AppCompatActivity {

    toOTPtask verifyOTPDetails;
    private EditText name;
    private EditText email;
    private EditText user_name;
    private EditText phone_num;
    private EditText password;
    private EditText confirm_password;
    private ColorStateList prevCSL;
    private ColorStateList ivCSL;
    private EditText enteredOTP;
    private Dialog dialogOTP;
    private Session session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        View view = (LayoutInflater.from(RegisterActivity.this)).inflate(R.layout.verify_otp, null);

        AlertDialog.Builder reqOTP = new AlertDialog.Builder(RegisterActivity.this);
        reqOTP.setView(view);
        enteredOTP = view.findViewById(R.id.otp_edit);
        reqOTP.setCancelable(true);
        dialogOTP = reqOTP.create();
        verifyOTPDetails = new toOTPtask("\0", "\0");

        name = findViewById(R.id.Name);
        email = findViewById(R.id.Email);
        user_name = findViewById(R.id.User_Name);
        phone_num = findViewById(R.id.Phone_Num);
        password = findViewById(R.id.Password_New);
        confirm_password = findViewById(R.id.Password_Confirm);
        TextView haveOTP = findViewById(R.id.haveOTP);

        haveOTP.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                otpVerify();
                return false;
            }
        });

        session = new Session(getBaseContext());

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

        Button signup = findViewById(R.id.Sign_up);

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

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyFields()) {
                    String name_str = name.getText().toString();
                    String email_str = email.getText().toString();
                    String user_name_str = user_name.getText().toString();
                    String phone_num_str = phone_num.getText().toString();
                    String password_str = password.getText().toString();
                    new sendToServer().execute(name_str, email_str, user_name_str, phone_num_str, password_str);
                    //Toast.makeText(RegisterActivity.this, name_str, Toast.LENGTH_LONG).show();
                }
            }
        });

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

        email.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (email.getBackgroundTintList() != prevCSL)
                        email.setBackgroundTintList(prevCSL);
                }
                return false;
            }
        });

        phone_num.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (phone_num.getBackgroundTintList() != prevCSL)
                        phone_num.setBackgroundTintList(prevCSL);
                }
                return false;
            }
        });

        user_name.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (user_name.getBackgroundTintList() != prevCSL)
                        user_name.setBackgroundTintList(prevCSL);
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

        confirm_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (confirm_password.getBackgroundTintList() != prevCSL)
                        confirm_password.setBackgroundTintList(prevCSL);
                }
                return false;
            }
        });
    }

    private boolean verifyFields() {
        boolean flag = true;
        if (name.getText().toString().trim().length() == 0) {
            flag = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                name.setBackgroundTintList(ivCSL);
            }
            Toast.makeText(RegisterActivity.this, "Enter Valid Name", Toast.LENGTH_SHORT).show();
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()
                || email.getText().toString().trim().length() == 0) {
            flag = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                email.setBackgroundTintList(ivCSL);
            }
            Toast.makeText(RegisterActivity.this, "Enter a Valid Email", Toast.LENGTH_SHORT).show();
        }
        if (user_name.getText().toString().contains(" ") || user_name.getText().toString().trim().length() == 0) {
            flag = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                user_name.setBackgroundTintList(ivCSL);
            }
            Toast.makeText(RegisterActivity.this, "Enter a Valid User Name", Toast.LENGTH_SHORT).show();
        }
        if (phone_num.getText().toString().trim().length() != 10) {
            flag = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                phone_num.setBackgroundTintList(ivCSL);
            }
            Toast.makeText(RegisterActivity.this, "Enter valid Phone Number", Toast.LENGTH_SHORT).show();
        }
        if (password.getText().toString().trim().length() == 0) {
            flag = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                password.setBackgroundTintList(ivCSL);
                confirm_password.setBackgroundTintList(ivCSL);
            }
            Toast.makeText(RegisterActivity.this, "Enter a Password", Toast.LENGTH_SHORT).show();
        }
        if (confirm_password.getText().toString().trim().length() == 0) {
            flag = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                confirm_password.setBackgroundTintList(ivCSL);
            }
            Toast.makeText(RegisterActivity.this, "Re-Enter the Password", Toast.LENGTH_SHORT).show();
        }
        if (!Objects.equals(password.getText().toString(), confirm_password.getText().toString())) {
            flag = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                password.setBackgroundTintList(ivCSL);
                confirm_password.setBackgroundTintList(ivCSL);
            }
            Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        }
        return flag;
    }

    public void goBack(View view) {
        finish();
    }

    private void otpVerify() {
        dialogOTP.show();
    }

    public void otpClick(View view) {
        String otpEntered = enteredOTP.getText().toString().trim();
        if (otpEntered.trim().length() != 0) {
            verifyOTPDetails.uname = session.getUsername();
            verifyOTPDetails.string = otpEntered;
            new sendOTPtoVerify().execute(verifyOTPDetails);
        } else {
            Toast.makeText(RegisterActivity.this, "Enter OTP to Continue", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                enteredOTP.setBackgroundTintList(ivCSL);
            }
        }
    }

    private class sendToServer extends AsyncTask<String, Void, String> {
        String username, eemail;

        @Override
        protected String doInBackground(String... params) {
            String password = null, name = null, phone_num = null;
            try {
                password = URLEncoder.encode(new MD5Encoder(params[4]).encodeToMD5(), "UTF-8");
                name = URLEncoder.encode(params[0], "UTF-8");
                eemail = URLEncoder.encode(params[1], "UTF-8");
                username = URLEncoder.encode(params[2], "UTF-8");
                phone_num = URLEncoder.encode(params[3], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //Toast.makeText(RegisterActivity.this, name, Toast.LENGTH_LONG).show();
            String link = getString(R.string.link_to_server) + "signup.php";
            String data = "name=" + name + "&email=" + eemail
                    + "&phone_num=" + phone_num + "&username=" + username
                    + "&password=" + password;
            //Toast.makeText(RegisterActivity.this, data.toString(), Toast.LENGTH_LONG).show();
            return (new PostToServer(link + "?" + data).postToServer());
        }

        @Override
        protected void onPostExecute(String s) {
            if (Objects.equals(s, "otp sent")) {
                Toast.makeText(RegisterActivity.this, "Data Entered Successfully\nSending OTP to " + eemail, Toast.LENGTH_LONG).show();
                session.setPassResetStatus(0);
                session.setUsername(username);
                otpVerify();
            } else if (Objects.equals(s, "user name exists")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    user_name.setBackgroundTintList(ivCSL);
                }
                Toast.makeText(RegisterActivity.this, "User Name Already Registered", Toast.LENGTH_LONG).show();
            } else if (Objects.equals(s, "email exists")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    email.setBackgroundTintList(ivCSL);
                }
                Toast.makeText(RegisterActivity.this, "Email Already Registered", Toast.LENGTH_LONG).show();
            } else if (Objects.equals(s, "phone exists")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    phone_num.setBackgroundTintList(ivCSL);
                }
                Toast.makeText(RegisterActivity.this, "Phone Number Already Registered", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class toOTPtask {
        public String string;
        public String uname;

        public toOTPtask(String uname, String string) {
            this.uname = uname;
            this.string = string;
        }
    }

    private class sendOTPtoVerify extends AsyncTask<toOTPtask, Void, String> {
        @Override
        protected String doInBackground(toOTPtask... params) {
            String enteredOTP = null, userid = null;
            try {
                enteredOTP = URLEncoder.encode(params[0].string, "UTF-8");
                userid = URLEncoder.encode(params[0].uname, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String link = getString(R.string.link_to_server) + "verify_otp.php";

            String data = "otp_entered=" + enteredOTP + "&username=" + userid;

            return (new PostToServer(link + "?" + data).postToServer());
        }

        @Override
        protected void onPostExecute(String s) {
            if (Objects.equals(s, "success")) {
                Toast.makeText(getBaseContext(), "Registration Success", Toast.LENGTH_SHORT).show();
                dialogOTP.dismiss();
                finish();
            } else if (Objects.equals(s, "fail")) {
                Toast.makeText(getBaseContext(), "Invalid OTP", Toast.LENGTH_LONG).show();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);

                // Setting Dialog Title
                alertDialog.setTitle("Invalid OTP");
                alertDialog.setCancelable(true);

                // Setting Dialog Message
                alertDialog.setMessage("Do you want to Resend OTP?");

                // On pressing the Settings button.
                alertDialog.setPositiveButton("Resend OTP", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (verifyFields()) {
                            String name_str = name.getText().toString();
                            String email_str = email.getText().toString();
                            String user_name_str = user_name.getText().toString();
                            String phone_num_str = phone_num.getText().toString();
                            String password_str = password.getText().toString();
                            //new sendToServer().execute(name_str, email_str, user_name_str,
                            //phone_num_str, password_str);
                        }
                    }
                });

                // On pressing the cancel button
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // Showing Alert Message
                alertDialog.show();
            }
        }
    }
}
