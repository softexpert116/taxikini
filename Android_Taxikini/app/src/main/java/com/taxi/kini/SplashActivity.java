package com.taxi.kini;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.taxi.App;
import com.taxi.common.Utils.Util;

public class SplashActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    String mobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                checkLocationPermission();
            }
        }, 1000);
    }


    public void checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (Util.mUser != null) {
                App.goToIntroPage(this);
            } else {
                Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                SplashActivity.this.startActivity(loginIntent);
                finish();
            }
        } else {

            Intent settingsIntent = new Intent(SplashActivity.this, SettingsActivity.class);
            SplashActivity.this.startActivity(settingsIntent);
            finish();
        }
    }

}
