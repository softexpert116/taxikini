package com.taxi.common.Utils;

import android.Manifest;
import android.content.ContentProvider;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.taxi.common.User;
import com.taxi.kini.MainActivity;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;


public class Util {

//    public static String hostUrl = "http://192.168.1.105:8000";
//    public static String serverUrl = "http://192.168.1.105:8000/api/";
    public static String hostUrl = "http://3.86.34.181";
    public static String serverUrl = "http://3.86.34.181/api/";

    public static FirebaseAuth auth = FirebaseAuth.getInstance();
    public static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public static StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    public static FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
    public static FirebaseUser mUser = auth.getCurrentUser();
    public static User currentUser;
    public static Location currentLocation;
    public static String mFuncUrl = "https://us-central1-taxikini-9a743.cloudfunctions.net";

//    public static User currentUser;
    public static String tbl_user = "users";
    public static String tbl_route = "routes";
    public static String tbl_geofire = "geofire";
    public static String func_nearby = "nearDriverSearch";

    public static ArrayList<User> nearbyUsers;
    public static Boolean isDriver;

    public static String PASSENGER = "passenger";
    public static String DRIVER = "driver";

    public static String USER_ID = "id";
    public static String USER_FIRSTNAME = "firstname";
    public static String USER_LASTNAME = "lastname";
    public static String USER_TYPE = "type";
    public static String USER_EMAIL = "email";
    public static String USER_PHOTO = "photo";
    public static String USER_PHONE = "phone";
    public static String USER_PASSWORD = "password";
    public static String USER_CAR_NUM = "car_num";
    public static String USER_LICENSE_NUM = "license_num";
    public static String USER_LICENSE_PIC = "license_pic";
    public static String USER_PLATE_NUM = "plate_num";
    public static String USER_CAR_PIC = "car_pic";
    public static String USER_NRIC = "nric";
    public static String USER_TOKEN = "token";
    public static String USER_ONLINE = "0";
    public static String USER_LAT = "lat";
    public static String USER_LNG = "lng";

    public static String ROUTE_PASSENGER_ID = "passenger_id";
    public static String ROUTE_DRIVER_ID = "driver_id";
    public static String ROUTE_TIME = "time";
    public static String ROUTE_PLACE = "places";
    public static String ROUTE_ORI_PLACE = "ori_place";
    public static String ROUTE_DEST_PLACE = "dest_place";
    public static String ROUTE_DISTANCE = "distance";
    public static String ROUTE_DURATION = "duration";
    public static String ROUTE_PRICE = "price";

    public static Boolean CheckEditTextIsEmptyOrNot(EditText editText) {

        // Getting values from EditText.
        String text = editText.getText().toString().trim();

        // Checking whether EditText value is empty or not.
        if (TextUtils.isEmpty(text)) {

            // If any of EditText is empty then set variable value as False.
            return false;

        } else {

            // If any of EditText is filled then set variable value as True.
            return true;
        }
    }
    public static String convertFromBitmap(Bitmap bm) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 80, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }
    public static void showAlert(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }
    public static Uri getLocalBitmapUri(Context context, Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file =  new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
    public static String getDeviceToken(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("DEVICE_TOKEN", "");
    }
    public static Place getCurrentPlace(Context context) {
        String name = "", address = "", id = "";
        try {
            Geocoder geo = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(Util.currentLocation.getLatitude(), Util.currentLocation.getLongitude(), 1);
            if (!addresses.isEmpty()) {
                if (addresses.size() > 0) {
                    name = addresses.get(0).getFeatureName();
                    address = addresses.get(0).getAddressLine(0);
//                    yourtextboxname.setText(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Place.builder().setLatLng(new LatLng(Util.currentLocation.getLatitude(), Util.currentLocation.getLongitude())).setName(name).setAddress(address).setId(id).build();
    }

}
