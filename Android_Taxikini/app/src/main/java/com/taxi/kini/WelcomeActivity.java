package com.taxi.kini;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.taxi.common.Utils.Util;

import de.hdodenhof.circleimageview.CircleImageView;

public class WelcomeActivity extends AppCompatActivity {

    private Button enterBtn;
    private TextView nameTxt;
    private CircleImageView profileImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getSupportActionBar().hide();

        profileImg = findViewById(R.id.profileImg);
        nameTxt = findViewById(R.id.nameTxt);

//        Picasso.get()
//                .load(Util.currentUser.getAvatar())
//                .resize(100,100).noFade().into(profileImg);
//        nameTxt.setText(Util.currentUser.getName());

        enterBtn = findViewById(R.id.btnEnter);
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(WelcomeActivity.this, HomeActivity.class);
                WelcomeActivity.this.startActivity(homeIntent);
            }
        });
    }
}
