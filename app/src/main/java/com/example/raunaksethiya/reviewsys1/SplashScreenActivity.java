package com.example.raunaksethiya.reviewsys1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {
    private static int DELAY = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.


        FloatingActionButton fab = findViewById(R.id.splashfab);
        ImageView imageView = findViewById(R.id.splash_image);
        Animation splashAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_animation);
        fab.setAnimation(splashAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent toLogin = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(toLogin);
                finish();
            }
        }, DELAY);
    }
}
