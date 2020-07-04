package com.taxi.kini;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.taxi.common.User;
import com.taxi.common.Utils.Util;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button registerBtn;
    private EditText firstNameEdit, lastNameEdit, emailEdit, nricEdit, passwordEdit;
    private ImageView iv_userPhoto;

    String userPhoto, mobileNumber, firstName, lastName, email, nric, password;

    // Creating Progress dialog.
    ProgressDialog progressDialog;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Registration");
        getSupportActionBar().show();

        mobileNumber = getIntent().getExtras().getString("phone");

        userPhoto = "";
        iv_userPhoto = findViewById(R.id.photoCamera);
        iv_userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });


        registerBtn = findViewById(R.id.btnSignUp);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firstNameEdit = findViewById(R.id.firstNameEdit);
                firstName = firstNameEdit.getText().toString().trim();

                lastNameEdit = findViewById(R.id.lastNameEdit);
                lastName = lastNameEdit.getText().toString().trim();

                emailEdit = findViewById(R.id.emailEdit);
                email = emailEdit.getText().toString().trim();

                nricEdit = findViewById(R.id.nricEdit);
                nric = nricEdit.getText().toString().trim();

                passwordEdit = findViewById(R.id.passwordEdit);
                password = passwordEdit.getText().toString().trim();

                if (userPhoto.isEmpty() || userPhoto.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please take your photo.", Toast.LENGTH_LONG).show();

                } else if (firstName.equals("") || firstName.length() < 2 || firstName.length() > 50) {
                    Toast.makeText(getApplicationContext(), "Please type your first name exact.", Toast.LENGTH_LONG).show();

                } else if (lastName.equals("") || lastName.length() < 2 || lastName.length() > 50) {
                    Toast.makeText(getApplicationContext(), "Please type your last name exact.", Toast.LENGTH_LONG).show();

                } else if (email.equals("") || email.length() < 7 || email.length() > 50 || !email.contains("@") || !email.contains(".com")) {
                    Toast.makeText(getApplicationContext(), "Please type your email exact.", Toast.LENGTH_LONG).show();

                } else if (nric.equals("") || nric.length() < 2 || nric.length() > 50) {
                    Toast.makeText(getApplicationContext(), "Please type the NRIC exact.", Toast.LENGTH_LONG).show();

                } else if (password.equals("") || password.length() < 5 || password.length() > 50) {
                    Toast.makeText(getApplicationContext(), "Please type password exact. not less than 5 and more than 50 of the length.", Toast.LENGTH_LONG).show();

                } else if (mobileNumber.length() < 5 || mobileNumber.length() > 30) {
                    Toast.makeText(getApplicationContext(), "Please type your number exact.", Toast.LENGTH_LONG).show();

                } else {
                    passengerRegister();

                }
            }
        });

    }


    // --------- When click left arrow, go back in default theme ---------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // --------- Camera for photo ---------
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            filePath = Util.getLocalBitmapUri(this, imageBitmap);
            iv_userPhoto.setImageBitmap(imageBitmap);
            userPhoto = Util.convertFromBitmap(imageBitmap);
        }
    }



    // --------- User register ---------
    public void passengerRegister() {

        // Showing progress dialog at user registration time.
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        // check if exists email in user table
        Util.mDatabase.child(Util.tbl_user).orderByChild(Util.USER_EMAIL).equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            progressDialog.dismiss();
                            Util.showAlert(RegisterActivity.this, "Warning", "Email already exists!");
                            return;
                        } else {
                            // file upload
                            uploadPhotoToFirebase();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        progressDialog.dismiss();
                        Log.w( "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });
    }
    private void uploadPhotoToFirebase() {

        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();

        // Upload file and metadata to the path 'images/mountains.jpg'
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        final StorageReference file_refer = Util.mStorage.child("users/"+ts);

        // Listen for state changes, errors, and completion of the upload.
        file_refer.putFile(filePath, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                file_refer.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                     @Override
                     public void onSuccess(Uri uri) {
                         progressDialog.dismiss();
                         String downloadUrl = uri.toString();
                         Util.mDatabase.child(Util.tbl_user).child(Util.mUser.getUid()).child(Util.USER_PHOTO).setValue(downloadUrl);
                         Util.mDatabase.child(Util.tbl_user).child(Util.mUser.getUid()).child(Util.USER_PHONE).setValue(mobileNumber);
                         Util.mDatabase.child(Util.tbl_user).child(Util.mUser.getUid()).child(Util.USER_EMAIL).setValue(email);
                         Util.mDatabase.child(Util.tbl_user).child(Util.mUser.getUid()).child(Util.USER_PASSWORD).setValue(password);
                         Util.mDatabase.child(Util.tbl_user).child(Util.mUser.getUid()).child(Util.USER_FIRSTNAME).setValue(firstName);
                         Util.mDatabase.child(Util.tbl_user).child(Util.mUser.getUid()).child(Util.USER_LASTNAME).setValue(lastName);
                         Util.mDatabase.child(Util.tbl_user).child(Util.mUser.getUid()).child(Util.USER_TYPE).setValue(Util.PASSENGER);
                         Util.mDatabase.child(Util.tbl_user).child(Util.mUser.getUid()).child(Util.USER_TOKEN).setValue(Util.getDeviceToken(RegisterActivity.this));

                         Util.currentUser = new User(Util.mUser.getUid(), firstName, lastName, Util.PASSENGER, email, downloadUrl.toString(), mobileNumber, password, "", "", "", "", "" , "", "", "0", 0, 0);

                         Toast.makeText(RegisterActivity.this, "Successfully registered!", Toast.LENGTH_LONG).show();
                         Intent introIntent = new Intent(RegisterActivity.this, IntroActivity.class);
                         startActivity(introIntent);
                         finish();
                         //Do what you want with the url
                     }
                 });
            }
        });
    }

}
