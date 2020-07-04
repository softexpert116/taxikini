package com.taxi.kini;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.taxi.common.Utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class NewMobileActivity extends AppCompatActivity {

    Button loginBtn;
    EditText mobileTxt;
    CountryCodePicker ccp;

    // Creating Progress dialog.
    ProgressDialog progressDialog;

    String mobileNumber, email;
    String ccpNumber;

    private Handler mHandler;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_mobile);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if (extras == null) {
                Intent recoverIntent = new Intent(NewMobileActivity.this, RecoverActivity.class);
                NewMobileActivity.this.startActivity(recoverIntent);
                Toast.makeText(getApplicationContext(), "Please type your email registered.", Toast.LENGTH_LONG).show();

            } else {
                email = extras.getString("email");
            }
        } else {
            email= (String) savedInstanceState.getSerializable("email");
        }

        getSupportActionBar().hide();

        Typeface typeFace = getResources().getFont(R.font.gibson_regular);

        ccp = findViewById(R.id.ccp);
        ccp.setTypeFace(typeFace);

        mobileTxt = findViewById(R.id.mobileEdit);

        loginBtn = findViewById(R.id.btnLogin);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ccpNumber = ccp.getSelectedCountryCode();
                mobileNumber = ccpNumber + mobileTxt.getText().toString().trim();

                if (Util.CheckEditTextIsEmptyOrNot(mobileTxt) && mobileNumber.length() > 5 && mobileNumber.length() < 30) {
                    phoneState();
                }
                else {
                    Toast.makeText(NewMobileActivity.this, "Please input your mobile number exact.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    // --------- Check if registered phone  ---------
    public void phoneState() {
        // Showing progress dialog at user registration time.
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait");
        progressDialog.show();

        String loginUrl = Util.serverUrl + "passenger/phoneState";

        RequestBody formBody = new FormBody.Builder()
                .add("phone", mobileNumber)
                .add("email", email)
                .build();

        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url(loginUrl)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(phoneStateCallback);
    }

    Callback phoneStateCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

            progressDialog.dismiss();
            try {
                Log.i("OKHttp", "Check failed: " + call.execute().code());
                // Showing error message if something goes wrong.
                Toast.makeText(NewMobileActivity.this, call.execute().code(), Toast.LENGTH_LONG).show();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            progressDialog.dismiss();
            // String loginResponseString = response.body().string();
            try {
                JSONObject responseObj = new JSONObject(response.body().string());
                Log.i("OKHttp", "responseObj: " + responseObj);

                Boolean status = responseObj.getBoolean("status");

                if (status) {
                    // If response matched then show the toast.
                    Intent verifyIntent = new Intent(NewMobileActivity.this, VerifyActivity.class);
                    verifyIntent.putExtra("phone", mobileNumber);
                    NewMobileActivity.this.startActivity(verifyIntent);
                }
                else {
                    String msg = responseObj.getString("result");

                    // If response matched then show the toast.
                    if (msg.equalsIgnoreCase("phone")) {
                        mHandler = new Handler(Looper.getMainLooper());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(NewMobileActivity.this, "Taken this phone number already",Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    if (msg.equalsIgnoreCase("server")) {
                        mHandler = new Handler(Looper.getMainLooper());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(NewMobileActivity.this, "Sorry, Internal Server Error",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

}



























