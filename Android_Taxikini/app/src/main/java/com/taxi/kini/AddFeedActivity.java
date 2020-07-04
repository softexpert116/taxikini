package com.taxi.kini;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.taxi.common.Utils.Util;
import com.taxi.httpModule.RequestBuilder;
import com.taxi.httpModule.ResponseElement;
import com.taxi.httpModule.RunanbleCallback;

public class AddFeedActivity extends AppCompatActivity {

    ImageView feedImg, driverAvatar;
    EditText tvFeedTitle, tvFeedDescription;
    Button postBtn;
    String feedTitle, feedDescription, phone, encodedNewsPhoto;

    ProgressDialog progressDialog;
    private Handler mHandler;

    @RequiresApi(api = Build.VERSION_CODES.O)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed);

        getSupportActionBar().show();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add News");

        Intent intent = getIntent();
        Bitmap bitmap = intent.getParcelableExtra("BitmapImage");
        feedImg = findViewById(R.id.feedImg);
        feedImg.setImageBitmap(bitmap);

        encodedNewsPhoto = Util.convertFromBitmap(bitmap);

        phone = Util.currentUser.phone;

        driverAvatar = findViewById(R.id.driverAvatar);
        Picasso.get().load(Util.hostUrl + Util.currentUser.photo).into(driverAvatar);

        tvFeedTitle = findViewById(R.id.feedTitle);

        tvFeedDescription = findViewById(R.id.feedDescription);

        postBtn = findViewById(R.id.btnPost);

        postBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                feedTitle = tvFeedTitle.getText().toString().trim();
                feedDescription = tvFeedDescription.getText().toString().trim();
                if (feedTitle.isEmpty() || feedTitle.length() == 0 || feedTitle.equals("") || feedTitle == null) {

//                    Toast.makeText(AddFeedActivity.this, "No title", Toast.LENGTH_LONG).show();
                    feedTitle = "";
                }

                if (feedDescription.isEmpty() || feedDescription.length() == 0 || feedDescription.equals("") || feedDescription == null) {

//                    Toast.makeText(AddFeedActivity.this, "No description", Toast.LENGTH_LONG).show();
                    feedDescription = "";
                }

                postNewsRequest();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void postNewsRequest() {
        String newsPostUrl = Util.serverUrl + "newsFeed/store";
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        RequestBuilder requestBuilder = new RequestBuilder(newsPostUrl, "POST");
        requestBuilder
                .addParam("phone", phone)
                .addParam("newsPhoto", encodedNewsPhoto)
                .addParam("title", feedTitle)
                .addParam("content", feedDescription)
                .sendRequest(newsCallback);
    }
    RunanbleCallback newsCallback = new RunanbleCallback() {
        @Override
        public void finish(ResponseElement element) {
            progressDialog.dismiss();
            if (element.getResponse().equals("")) {
                Util.showAlert(AddFeedActivity.this, "Error", "Server connection error!");
                return;
            }
            if (element.getStatus()) {
                Toast.makeText(AddFeedActivity.this, "News has been posted successfully!", Toast.LENGTH_LONG).show();
                AddFeedActivity.this.finish();
            } else {
                Util.showAlert(AddFeedActivity.this, "Error", "Server error!");
                return;
            }
        }

    };
//
//        @Override
//        public void onResponse(Call call, Response response) throws IOException {
//            progressDialog.dismiss();
//            // String loginResponseString = response.body().string();
//            try {
//                JSONObject responseObj = new JSONObject(response.body().string());
//                Log.i("OKHttp", "responseObj: " + responseObj);
//
//                Boolean status = responseObj.getBoolean("status");
////
//                if (status) {
//                    // If response matched then show the toast.
//                    //String type = responseObj.getString("type");
////
////                    JSONObject userData = responseObj.getJSONObject("user_data");
////
////                    String phone = userData.getString("phone");
////                    String avatar  = Util.hostUrl + userData.getString("photo");
////                    String name = userData.getString("first_name") + userData.getString("last_name");
////
////                    Util.currentUser = new User(phone,name, avatar, true );
////                    Util.currentUser.email = userData.getString("email");
//
//                    mHandler = new Handler(Looper.getMainLooper());
//                    mHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getBaseContext(), "Posted Successfully", Toast.LENGTH_LONG).show();
//                        }
//                    });
//                }
//                else {
//                    mHandler = new Handler(Looper.getMainLooper());
//                    mHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getBaseContext(), "Failed posting", Toast.LENGTH_LONG).show();
//                        }
//                    });
//                }
//
//                Intent newsFeedIntent = new Intent(AddFeedActivity.this, NewsFeed.class);
//                AddFeedActivity.this.startActivity(newsFeedIntent);
//                finish();
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            // Log.i(TAG, "loginResponseString: " + loginResponseString);
//        }
//    };


//    @Override
//    public void onClick(View v) {
//        if (v == postBtn) {
//            this.finish();
//        }
//    }
}
