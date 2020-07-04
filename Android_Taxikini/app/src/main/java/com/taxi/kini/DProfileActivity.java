package com.taxi.kini;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dprofile);

        getSupportActionBar().hide();
    }
}
