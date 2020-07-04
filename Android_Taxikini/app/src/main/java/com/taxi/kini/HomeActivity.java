package com.taxi.kini;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.squareup.picasso.Picasso;
import com.taxi.Fragments.AboutFragment;
import com.taxi.Fragments.HistoryFragment;
import com.taxi.Fragments.NewsFragment;
import com.taxi.Fragments.PaymentsFragment;
import com.taxi.Fragments.SettingsFragment;
import com.taxi.Fragments.SupportFragment;
import com.taxi.Fragments.driver.FindJobFragment;
import com.taxi.Fragments.passenger.BookRideFragment;
import com.taxi.common.Utils.Util;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public int AUTOCOMPLETE_REQUEST_CODE = 1;
    public int REQUEST_IMAGE_CAPTURE = 100;
    public static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyAlwVsZwxcZiZhg4Zf1kJLDJmZodI194wc";
    GeoFire geoFire = new GeoFire(Util.mDatabase.child(Util.tbl_geofire));

    NavigationView navigationView;
    FragmentTransaction transaction;
    FrameLayout frameLayout;
    TextView txt_header;
    Button btn_menu;
    Toolbar toolbar;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    public interface LocationUpdateCallback {
        void locationUpdateCallback();
    }
    public LocationUpdateCallback locationUpdateCallback;
    public interface PlaceUpdateCallback {
        void placeUpdateCallback(Place place);
    }
    public PlaceUpdateCallback placeUpdateCallback;
    public interface CameraCaptureCallback {
        void cameraCaptureCallback(Place place);
    }
    public CameraCaptureCallback cameraCaptureCallback;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        // Container View
        btn_menu = (Button)findViewById(R.id.btn_menu);
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer();
            }
        });
        txt_header = (TextView)findViewById(R.id.txt_header);
        toolbar.setVisibility(View.GONE);
        btn_menu.setVisibility(View.VISIBLE);
        frameLayout = (FrameLayout)findViewById(R.id.fl_container);

        setMenuByUserType();
        reloadFirstFragment();


        // Location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // create location request
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            Util.currentLocation = location;
                            locationUpdateCallback.locationUpdateCallback();
                        }
                    }
                });
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                Location location = locationResult.getLastLocation();
                if (location != null) {
                    Util.currentLocation = location;
                    if (Util.currentUser.type.equals(Util.DRIVER)) {
                        geoFire.setLocation(Util.currentUser.id, new GeoLocation(Util.currentLocation.getLatitude(), Util.currentLocation.getLongitude()));
                    }
//                    locationUpdateCallback.locationUpdateCallback();
                }
            }
        };
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                null /* Looper */);
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    public void setUserProfile(View header) {
        //Navigation Header Profile
        RelativeLayout ly_profile = header.findViewById(R.id.ly_profile);
        CircleImageView profileImg = header.findViewById(R.id.navProfile);
        TextView profileName = header.findViewById(R.id.profileName);

        ly_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to profile activity
//                txt_header.setText("Driver Profile");
//                toolbar.setVisibility(View.VISIBLE);
//                btn_menu.setVisibility(View.GONE);
//                selectFragment(new DriverProfileFragment());
//                closeDrawer();
            }
        });
        Picasso.get()
                .load(Util.currentUser.photo).placeholder(R.drawable.ic_user)
                .resize(80,80).noFade().into(profileImg);
        profileName.setText(Util.currentUser.firstname + Util.currentUser.lastname);
    }
    @SuppressLint("MissingPermission")
    private void reloadFirstFragment() {
        if (Util.currentUser.type.equals(Util.PASSENGER)) {
            selectFragment(new BookRideFragment());
        } else {
            selectFragment(new FindJobFragment());
        }

    }
    private void selectFragment(Fragment fragment) {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_container, fragment);
        transaction.commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
    private void setMenuByUserType() {
        Menu nav_Menu = navigationView.getMenu();
        if (Util.currentUser.type.equals(Util.PASSENGER)) {
            nav_Menu.findItem(R.id.nav_ride).setVisible(true);
            nav_Menu.findItem(R.id.nav_find_job).setVisible(false);
            nav_Menu.findItem(R.id.nav_switch_driver).setVisible(false);
            nav_Menu.findItem(R.id.nav_switch_passenger).setVisible(false);
            if (Util.currentUser.license_num != null) {
                nav_Menu.findItem(R.id.nav_switch_driver).setVisible(true);
            }
        } else {
            nav_Menu.findItem(R.id.nav_ride).setVisible(false);
            nav_Menu.findItem(R.id.nav_find_job).setVisible(true);
            nav_Menu.findItem(R.id.nav_switch_driver).setVisible(false);
            nav_Menu.findItem(R.id.nav_switch_passenger).setVisible(true);
        }
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_ride) {
            // Handle the camera action
            selectFragment(new BookRideFragment());
//            txt_header.setText("Book Ride");
            toolbar.setVisibility(View.GONE);
            btn_menu.setVisibility(View.VISIBLE);

        } else if (id == R.id.nav_find_job) {
            selectFragment(new FindJobFragment());
//            txt_header.setText("Find Job");
            toolbar.setVisibility(View.GONE);
            btn_menu.setVisibility(View.VISIBLE);

        } else if (id == R.id.nav_news) {
            selectFragment(new NewsFragment());
            txt_header.setText("News");
            toolbar.setVisibility(View.VISIBLE);
            btn_menu.setVisibility(View.GONE);

        } else if (id == R.id.nav_history) {
            selectFragment(new HistoryFragment());
            txt_header.setText("History");
            toolbar.setVisibility(View.VISIBLE);
            btn_menu.setVisibility(View.GONE);

        } else if (id == R.id.nav_payments) {
            selectFragment(new PaymentsFragment());
            txt_header.setText("PAYMENT");
            toolbar.setVisibility(View.VISIBLE);
            btn_menu.setVisibility(View.GONE);

        } else if (id == R.id.nav_support) {
            selectFragment(new SupportFragment());
            txt_header.setText("SUPPORT");
            toolbar.setVisibility(View.VISIBLE);
            btn_menu.setVisibility(View.GONE);

        } else if (id == R.id.nav_setting) {
            selectFragment(new SettingsFragment());
            txt_header.setText("SETTINGS");
            toolbar.setVisibility(View.VISIBLE);
            btn_menu.setVisibility(View.GONE);

        } else if (id == R.id.nav_about) {
            selectFragment(new AboutFragment());
            txt_header.setText("ABOUT");
            toolbar.setVisibility(View.VISIBLE);
            btn_menu.setVisibility(View.GONE);

        } else if (id == R.id.nav_switch_passenger) {
            Util.currentUser.type = Util.PASSENGER;
            Util.mDatabase.child(Util.tbl_user).child(Util.mUser.getUid()).child(Util.USER_TYPE).setValue(Util.currentUser.type );
            // reload activity
            reloadFirstFragment();
            setMenuByUserType();
        } else if (id == R.id.nav_switch_driver) {
            Util.currentUser.type = Util.DRIVER;
            Util.mDatabase.child(Util.tbl_user).child(Util.mUser.getUid()).child(Util.USER_TYPE).setValue(Util.currentUser.type );
            // reload activity
            reloadFirstFragment();
            setMenuByUserType();
        } else if (id == R.id.nav_logout) {

            Util.auth.signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "Logged out Successfully", Toast.LENGTH_LONG).show();
            finish();
        }
        closeDrawer();
        return true;
    }
    public void openDrawer(){
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
    }
    public void closeDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you going to finish app?");
            builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    ActivityCompat.finishAffinity(HomeActivity.this);
                    System.exit(0);
                }
            });
            builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Google place result
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("tag", "Place: " + place.getName() + ", " + place.getId());
                placeUpdateCallback.placeUpdateCallback(place);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("tag", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Intent intent = new Intent(this, AddFeedActivity.class);
            intent.putExtra("BitmapImage", imageBitmap);
            this.startActivity(intent);
        }
    }
    private static final int REQUEST_PHONE_CALL = 1;
    Intent call_intent;
    public void phone_call(String number) {
        call_intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+8613042661581"));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
        } else {
            startActivity(call_intent);
        }
    }
    // phone call permission result
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(call_intent);
                }
                else
                {

                }
                return;
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();

    }
}
