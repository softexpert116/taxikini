package com.taxi.kini;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


//    public void Register(View v)
//    {
//        Intent i=new Intent();
//        i.setClass(this,Second.class);
//        startActivity(i);
//    }
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_main);
//
//        TextView txtRegister = (TextView) findViewById(R.id.txtRegister);
//        txtRegister.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            protected void onClick(View view) {
//
//                Register(view);
//            }
//
//        });
//
//        //Repeat for each text view...
//    }
}
