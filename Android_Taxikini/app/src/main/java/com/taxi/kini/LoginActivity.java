package com.taxi.kini;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.hbb20.CountryCodePicker;
import com.taxi.App;
import com.taxi.common.Utils.Util;
import com.taxi.common.User;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity {

    Button loginBtn;
    TextView emailRecoverTxt;
    EditText mobileTxt;
    CountryCodePicker ccp;

    // Creating Progress dialog.
    ProgressDialog progressDialog;

    String mobileNumber;
    String ccpNumber;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    FirebaseAuth.AuthStateListener mAuthListener;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

//        Typeface typeFace = getResources().getFont(R.font.gibson_regular);

        ccp = findViewById(R.id.ccp);
//        ccp.setTypeFace(typeFace);

        mobileTxt = findViewById(R.id.mobileEdit);

        loginBtn = findViewById(R.id.btnLogin);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.CheckEditTextIsEmptyOrNot(mobileTxt)) {
                    ccpNumber = ccp.getSelectedCountryCode();
                    mobileNumber = ccpNumber + mobileTxt.getText().toString().trim();
                    sendSMS();
                } else {
                    Toast.makeText(LoginActivity.this, "Please input your mobile number.", Toast.LENGTH_LONG).show();
                }

//                Util.auth.signInWithEmailAndPassword("test@test.com", "testtest")
//                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//                                    Util.mUser = Util.auth.getCurrentUser();
//                                    if (Util.CheckEditTextIsEmptyOrNot(mobileTxt)) {
//                                        ccpNumber = ccp.getSelectedCountryCode();
//                                        mobileNumber = ccpNumber + mobileTxt.getText().toString().trim();
//                                        Util.mDatabase.child(Util.tbl_user).orderByChild(Util.EMAIL).equalTo("test@test.com")
//                                                .addListenerForSingleValueEvent(new ValueEventListener() {
//                                                    @Override
//                                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                                        if (dataSnapshot.getValue() != null) {
//                                                            // go to intro page
//                                                            App.goToIntroPage(LoginActivity.this);
//                                                        } else {
//                                                            Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
//                                                            registerIntent.putExtra("phone", mobileNumber);
//                                                            LoginActivity.this.startActivity(registerIntent);
//                                                            finish();
//                                                        }
//                                                    }
//
//                                                    @Override
//                                                    public void onCancelled(DatabaseError databaseError) {
//                                                        // Getting Post failed, log a message
//
//                                                        Log.w( "loadPost:onCancelled", databaseError.toException());
//                                                        // ...
//                                                    }
//                                                });
//                                    }
//                                    else {
//                                        Toast.makeText(LoginActivity.this, "Please input your mobile number.", Toast.LENGTH_LONG).show();
//                                    }
//                                } else {
//                                    // If sign in fails, display a message to the user.
//                                    Log.w("tag", "signInWithEmail:failure", task.getException());
//
//                                }
//                            }
//                        });

            }
        });
//        emailRecoverTxt = findViewById(R.id.txtRecover);
//        emailRecoverTxt.setTypeface(typeFace);
//        emailRecoverTxt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent recoverIntent = new Intent(LoginActivity.this, RecoverActivity.class);
//                LoginActivity.this.startActivity(recoverIntent);
//            }
//        });

//        if (Util.mUser != null) {
//            App.goToIntroPage(this);
//        }
//        mAuthListener = new FirebaseAuth.AuthStateListener(){
//            @Override
//            public  void  onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
//                FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
//                if(user!=null){
//                    Util.auth = firebaseAuth;
//                }
//            }
//
//
//        };

    }

    private void sendSMS() {
        String number = "+" + mobileNumber;
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                progressDialog.dismiss();
                Log.d("msg", "onVerificationCompleted");
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressDialog.dismiss();
                Log.d("msg", e.getLocalizedMessage());
//                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                Util.showAlert(LoginActivity.this, "Warning", e.getMessage());
            }

            @Override
            public void onCodeSent(String verificationID, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                progressDialog.dismiss();
                goToVerifyActivity(mobileNumber, verificationID);
                Toast.makeText(getApplicationContext(), "Code has sent to your phone.", Toast.LENGTH_LONG).show();
            }
        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number, 60, TimeUnit.SECONDS, this, mCallback
        );
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
    }
    private void goToVerifyActivity(String phone, String verificationID) {
        Intent testIntent = new Intent(LoginActivity.this, VerifyActivity.class);
        testIntent.putExtra("phone", phone);
        testIntent.putExtra("verificationID", verificationID);
        LoginActivity.this.startActivity(testIntent);
    }

}



























