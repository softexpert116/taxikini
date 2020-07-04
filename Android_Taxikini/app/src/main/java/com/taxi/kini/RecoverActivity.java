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
import android.widget.TextView;
import android.widget.Toast;

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


public class RecoverActivity extends AppCompatActivity {

    Button btnEnter;
    TextView mobileNumberTxt;
    EditText emailEdit, passwordEdit;
    String recoverEmail, password;

    ProgressDialog progressDialog;
    private Handler mHandler;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover);

        getSupportActionBar().hide();

        Typeface typeFace = getResources().getFont(R.font.gibson_regular);

        emailEdit = findViewById(R.id.emailEdit);
        passwordEdit = findViewById(R.id.passwordEdit);

        btnEnter = findViewById(R.id.btnEnter);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Util.CheckEditTextIsEmptyOrNot(emailEdit) || Util.CheckEditTextIsEmptyOrNot(passwordEdit)) {
                    recoverEmail = emailEdit.getText().toString().trim();
                    password = passwordEdit.getText().toString().trim();

                    emailPasswordState();
                }
                else {
                    Toast.makeText(RecoverActivity.this, "Please input your email address and password.", Toast.LENGTH_LONG).show();
                }

            }
        });

        mobileNumberTxt = findViewById(R.id.mobileNumberTxt);
        mobileNumberTxt.setTypeface(typeFace);
        mobileNumberTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RecoverActivity.this, LoginActivity.class);
                RecoverActivity.this.startActivity(loginIntent);
            }
        });
    }


    // --------- Check if registered user  ---------
    public void emailPasswordState() {
        // Showing progress dialog at user registration time.
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait");
        progressDialog.show();

        String emailCheckUrl = Util.serverUrl + "passenger/emailState";

        RequestBody formBody = new FormBody.Builder()
                .add("email", recoverEmail)
                .add("password", password)
                .build();

        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url(emailCheckUrl)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(emailStateCallback);
    }

    Callback emailStateCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            progressDialog.dismiss();
            try {
                Log.i("OKHttp", "Check failed: " + call.execute().code());
                // Showing error message if something goes wrong.
                Toast.makeText(RecoverActivity.this, call.execute().code(), Toast.LENGTH_LONG).show();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            progressDialog.dismiss();

            try {
                JSONObject responseObj = new JSONObject(response.body().string());
                Log.i("OKHttp", "responseObj: " + responseObj);

                Boolean status = responseObj.getBoolean("status");

                if (status) {
                    // If response matched then show the toast.
                    Intent newMobileIntent = new Intent(RecoverActivity.this, NewMobileActivity.class);
                    newMobileIntent.putExtra("email", recoverEmail);
                    RecoverActivity.this.startActivity(newMobileIntent);
                }
                else {

                    String msg = responseObj.getString("result");

                    // If response matched then show the toast.

                    if (msg.equalsIgnoreCase("password")) {
                        mHandler = new Handler(Looper.getMainLooper());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RecoverActivity.this, "Invalid password. please type exactly",Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    if (msg.equalsIgnoreCase("email")) {
                        mHandler = new Handler(Looper.getMainLooper());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RecoverActivity.this, "Invalid email, please type your email registered",Toast.LENGTH_LONG).show();
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
