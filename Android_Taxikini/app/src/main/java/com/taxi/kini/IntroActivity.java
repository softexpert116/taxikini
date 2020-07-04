package com.taxi.kini;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taxi.adapters.IntroPager;

public class IntroActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private ViewPager introPager;
    private IntroPager introAdapter;
    private Button nextBtn, backBtn, forwardBtn;
    private LinearLayout nextLayout;
    private TextView skipTxt;
    private int introPageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        getSupportActionBar().show();
        //getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("INTRO");

        introPager = findViewById(R.id.introPager);
        introAdapter = new IntroPager(this);

        introPager.setAdapter(introAdapter);
        introPager.addOnPageChangeListener(this);

        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(this);

        nextLayout = findViewById(R.id.nextLayout);

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);
        forwardBtn = findViewById(R.id.forwardBtn);
        forwardBtn.setOnClickListener(this);

        skipTxt = findViewById(R.id.skipTxt);
        skipTxt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == nextBtn) {
            introPageIndex ++;
            introPager.setCurrentItem(introPageIndex, true);
            nextBtn.setVisibility(View.GONE);
            nextLayout.setVisibility(View.VISIBLE);
        }

        if (v == backBtn) {
            introPageIndex --;

            introPager.setCurrentItem(introPageIndex, true);

            if (introPageIndex == 0) {
                nextBtn.setVisibility(View.VISIBLE);
                nextLayout.setVisibility(View.GONE);
            }
        }

        if (v== forwardBtn) {
            introPageIndex ++;
            introPager.setCurrentItem(introPageIndex, true);
        }

        if (v == skipTxt) {
            Intent homeIntent = new Intent(IntroActivity.this, HomeActivity.class);
            this.startActivity(homeIntent);
            finish();
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        introPageIndex = i;

        if (i == 0) {
            nextBtn.setVisibility(View.VISIBLE);
            nextLayout.setVisibility(View.GONE);
        }
        else {
            nextBtn.setVisibility(View.GONE);
            nextLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
