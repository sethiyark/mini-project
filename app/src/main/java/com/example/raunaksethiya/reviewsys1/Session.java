package com.example.raunaksethiya.reviewsys1;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Raunak Sethiya on 12-Sep-16.
 */
public class Session {
    private SharedPreferences myPref;

    public Session(Context context) {
        myPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean getGuest() {
        return myPref.getBoolean("guest", false);
    }

    public void setGuest(boolean guest) {
        SharedPreferences.Editor editor = myPref.edit();
        editor.putBoolean("guest", guest);
        editor.apply();
    }

    public int getPassResetStatus() {
        return myPref.getInt("prStat", 0);
    }

    public void setPassResetStatus(int status) {
        SharedPreferences.Editor editor = myPref.edit();
        editor.putInt("prStat", status);
        editor.apply();
    }

    public String getUsername() {
        return myPref.getString("username", "");
    }

    public void setUsername(String username) {
        SharedPreferences.Editor editor = myPref.edit();
        editor.putString("username", username);
        editor.apply();
    }

    public boolean getLoggedInStatus() {
        return myPref.getBoolean("loginStatus", false);
    }

    public void setLoggedInStatus(boolean loggedInStatus) {
        SharedPreferences.Editor editor = myPref.edit();
        editor.putBoolean("loginStatus", loggedInStatus);
        editor.apply();
    }

    public String getEmail() {
        return myPref.getString("email", "");
    }

    public void setEmail(String email) {
        SharedPreferences.Editor editor = myPref.edit();
        editor.putString("email", email);
        editor.apply();
    }

    public String getFullName() {
        return myPref.getString("name", "");
    }

    public void setFullName(String name) {
        SharedPreferences.Editor editor = myPref.edit();
        editor.putString("name", name);
        editor.apply();
    }

    public String getPhone() {
        return myPref.getString("phone", "");
    }

    public void setPhone(String phone) {
        SharedPreferences.Editor editor = myPref.edit();
        editor.putString("phone", phone);
        editor.apply();
    }

    public String getCategory() {
        return myPref.getString("category", "");
    }

    public void setCategory(String category) {
        SharedPreferences.Editor editor = myPref.edit();
        editor.putString("category", category);
        editor.apply();
    }

    public String getCatItem() {
        return myPref.getString("catitem", "");
    }

    public void setCatItem(String catItem) {
        SharedPreferences.Editor editor = myPref.edit();
        editor.putString("catitem", catItem);
        editor.apply();
    }
}
