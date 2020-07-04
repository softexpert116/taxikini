package com.taxi.kini;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taxi.App;
import com.taxi.common.User;
import com.taxi.common.Utils.Util;

import java.util.concurrent.TimeUnit;

public class VerifyActivity extends AppCompatActivity {
    EditText ev_verifyCode;

    String mobileNumber;
    String verificationID;

    ProgressDialog progressDialog;
    private DatabaseReference mDatabase;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("OTP Verification");
        getSupportActionBar().show();

        mobileNumber = getIntent().getExtras().getString("phone");
        verificationID = getIntent().getExtras().getString("verificationID");

//        Util.auth = FirebaseAuth.getInstance();

        ev_verifyCode = findViewById(R.id.verifyCode);

        Button btn_verify = (Button)findViewById(R.id.btnVerify);
        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input_code = ev_verifyCode.getText().toString();
                verifyPhoneNumber(verificationID, input_code);
            }
        });
    }


    // --------- When click left arrow, go back in default theme ---------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void verifyPhoneNumber(String verifyID, String input_code)
    {
        if (input_code.length() == 0 || input_code.equals("")) {
            Toast.makeText(getApplicationContext(), "Please input the security number", Toast.LENGTH_LONG).show();

        } else if (input_code.length() < 6) {

            Toast.makeText(getApplicationContext(), "Please match length of security number", Toast.LENGTH_LONG).show();
        } else {
            try {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifyID, input_code);
                signInWithPhone(credential);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // --------- OTP verification SMS ---------

    public void signInWithPhone(PhoneAuthCredential credential)
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        Util.auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Util.mUser = Util.auth.getCurrentUser();
                            // sms verify successful here
                            // check user table if exists user phone.
//                            Util.mDatabase = FirebaseDatabase.getInstance().getReference().child(Util.tbl_user);
                            Util.mDatabase.child(Util.tbl_user).orderByChild(Util.USER_PHONE).equalTo(mobileNumber)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            progressDialog.dismiss();
                                            if (dataSnapshot.getValue() != null) {
                                                // go to intro page
                                                App.goToIntroPage(VerifyActivity.this);
                                            } else {
                                                Intent registerIntent = new Intent(VerifyActivity.this, RegisterActivity.class);
                                                registerIntent.putExtra("phone", mobileNumber);
                                                startActivity(registerIntent);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // Getting Post failed, log a message
                                            Intent registerIntent = new Intent(VerifyActivity.this, RegisterActivity.class);
                                            registerIntent.putExtra("phone", mobileNumber);
                                            VerifyActivity.this.startActivity(registerIntent);
                                            finish();

                                            Log.w( "loadPost:onCancelled", databaseError.toException());
                                            // ...
                                        }
                                    });
                        } else {
                            Util.showAlert(VerifyActivity.this, "Warning", task.getException().getMessage());
//                            Toast.makeText(VerifyActivity.this, "OTP verification failed! Please try again..", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


}
