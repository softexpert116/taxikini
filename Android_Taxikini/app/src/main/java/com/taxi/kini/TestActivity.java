package com.taxi.kini;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.taxi.chat.util.Util;
//import com.taxi.Dialogs.PlaceSearchDialog;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestActivity extends AppCompatActivity {
    // Initialize Places.
    int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final String BUNDLE_KEY = "AIzaSyAlwVsZwxcZiZhg4Zf1kJLDJmZodI194wc";
    TextView txt_location;
    private static final int REQUEST_PHONE_CALL = 1;
    Intent intent;
    private FirebaseAuth mFirebaseAuth;
    ProgressDialog progressDialog;
    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(this);

        final EditText edit_email = (EditText)findViewById(R.id.edit_email);
        final EditText edit_password = (EditText)findViewById(R.id.edit_password);
        Button btn_login = (Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                progressDialog.show();
//                mFirebaseAuth.signInWithEmailAndPassword(edit_email.getText().toString(), edit_password.getText().toString()).addOnCompleteListener(TestActivity.this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        progressDialog.dismiss();
//                        if (!task.isSuccessful()) {
//                            Toast.makeText(TestActivity.this, "sign in error", Toast.LENGTH_SHORT).show();
//                        } else {
//                            goToChat();
//                        }
//                    }
//                });
//                sendNotificationToUser();
                addMessage("this is test");
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null){
        }else {
            goToChat();
        }
    }
    void goToChat() {
        startActivity(new Intent(TestActivity.this, ChatActivity.class));
        finish();
    }

    public static void sendNotificationToUser() {
        String topic = "highScores";

// See documentation on defining a message payload.
        RemoteMessage msg = new RemoteMessage.Builder("dVUgiDZyLP0:APA91bE5g1VeVKKDLWkvTuLXsBz69B51lmoQ1UR79rlOHCIsd0dWQWuJq8yxfLWI7BelChRklBqfjWaMk9365BdWWbWwupLCZmv0vihaBIGzwzXddkk41b35-z8P0Tn9edqleD8jw12i")
                .addData("score", "850")
                .addData("time", "2:45")
                .build();

// Send a message to the devices subscribed to the provided topic.
        FirebaseMessaging.getInstance().send(msg);
    }
    private Task<String> addMessage(String route_id) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("route_id", route_id);

        return mFunctions
                .getHttpsCallable("nearDriverSearch")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }
}
