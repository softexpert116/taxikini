package com.taxi.Fragments.passenger;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.taxi.Fragments.HistoryFragment;
import com.taxi.common.FuncResult;
import com.taxi.common.Utils.Util;
import com.taxi.common.directionhelpers.FetchURL;
import com.taxi.common.directionhelpers.TaskLoadedCallback;
import com.taxi.httpModule.RequestBuilder;
import com.taxi.httpModule.ResponseElement;
import com.taxi.httpModule.RunanbleCallback;
import com.taxi.kini.HomeActivity;
import com.taxi.kini.R;
import com.taxi.kini.TestActivity;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.taxi.kini.HomeActivity.MAP_VIEW_BUNDLE_KEY;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookRideFragment extends Fragment implements OnMapReadyCallback, TaskLoadedCallback {

    long distance = 0, duration = 0, price = 0;

    HomeActivity activity;

    private GoogleMap mMap;
    private MapView mapView;
    public TextView txt_dest, txt_price;
    private LinearLayout ly_destination;

    Button btn_dest_confirm, btn_dest_cancel, btn_searching_cancel;
    RelativeLayout ly_step1, ly_step2, nested_scroll_view, searching_background;
    LinearLayout ly_otp, ly_searching_driver;
    ProgressDialog progressDialog;
    ImageView img_search_logo;
    TextView tv_time_remaining;

    // for all
    Polyline polyline;
    Marker marker1, marker2;
    Place ori_place, dest_place;
    String routeKey = "";

    CountDownTimer driverSearchTimer;

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_book_ride, container, false);
        // Map View
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        progressDialog = new ProgressDialog(activity);

        mapView = v.findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        activity.locationUpdateCallback = new HomeActivity.LocationUpdateCallback() {
            @Override
            public void locationUpdateCallback() {
                updateMap();
            }
        };
        txt_price = (TextView)v.findViewById(R.id.txt_price);
        searching_background = (RelativeLayout)v.findViewById(R.id.searching_background);
        nested_scroll_view = (RelativeLayout)v.findViewById(R.id.nested_scroll_view);
        ly_step1 = (RelativeLayout)v.findViewById(R.id.ly_step1);
        ly_step2 = (RelativeLayout)v.findViewById(R.id.ly_step2);
        ly_otp = (LinearLayout)v.findViewById(R.id.ly_otp);
        btn_searching_cancel = (Button)v.findViewById(R.id.btn_searching_cancel);
        btn_searching_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ly_searching_driver.setVisibility(View.GONE);
                nested_scroll_view.setVisibility(View.VISIBLE);
                ly_step1.setVisibility(View.VISIBLE);
                btn_searching_cancel.setVisibility(View.GONE);
                searching_background.setVisibility(View.GONE);
                driverSearchTimer.cancel();
                Util.mDatabase.child(Util.tbl_route).child(routeKey).setValue(null);
                cancelRoute();
                Toast.makeText(activity, "Canceled searching drivers.", Toast.LENGTH_LONG).show();
            }
        });
        btn_dest_cancel = (Button)v.findViewById(R.id.btn_dest_cancel);
        btn_dest_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_dest.setText("");
            }
        });
        btn_dest_confirm = (Button)v.findViewById(R.id.btn_dest_confirm);
        btn_dest_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dest_place == null) {
                    Toast.makeText(activity, "Please set destination!", Toast.LENGTH_LONG).show();
                    return;
                }
                MarkerOptions place1, place2;
                place1 = new MarkerOptions().position(new LatLng(Util.currentLocation.getLatitude(), Util.currentLocation.getLongitude())).title("Current location");
                place2 = new MarkerOptions().position(dest_place.getLatLng()).title(dest_place.getName());
                if (marker1 != null) {
                    marker1.remove();
                }
                if (marker2 != null) {
                    marker2.remove();
                }
                marker1 = mMap.addMarker(place1);
                marker2 = mMap.addMarker(place2);
                new FetchURL(activity, BookRideFragment.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
//                ly_destination.setEnabled(false);
            }
        });

        ly_destination = (LinearLayout)v.findViewById(R.id.ly_destination);
        txt_dest = (TextView) v.findViewById(R.id.txt_dest);
        if (!Places.isInitialized()) {
            Places.initialize(activity, MAP_VIEW_BUNDLE_KEY);
        }
        ly_destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

// Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(activity);
                activity.startActivityForResult(intent, activity.AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        activity.placeUpdateCallback = new HomeActivity.PlaceUpdateCallback() {
            @Override
            public void placeUpdateCallback(Place place) {
                dest_place = place;
                txt_dest.setText(place.getName());
                ly_step1.setVisibility(View.GONE);
                btn_dest_confirm.setVisibility(View.VISIBLE);
            }
        };

        ly_searching_driver = (LinearLayout)v.findViewById(R.id.ly_searching_driver);
        img_search_logo = (ImageView)v.findViewById(R.id.img_search_logo);
        tv_time_remaining = (TextView)v.findViewById(R.id.tv_time_remaining);
        Button btn_ride_now = (Button)v.findViewById(R.id.btn_ride_now);
        btn_ride_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Please Wait...");
                progressDialog.show();

                nested_scroll_view.setVisibility(View.GONE);
                ly_step1.setVisibility(View.GONE);
//                ly_step2.setVisibility(View.VISIBLE);
                ly_otp.setVisibility(View.GONE);
                ly_destination.setEnabled(false);
                btn_dest_cancel.setVisibility(View.GONE);

                // upload to firebase routes table..
                routeKey = Util.mDatabase.child(Util.tbl_route).push().getKey();
                String place1Key = Util.mDatabase.child(Util.tbl_route).push().getKey();
                Map routeMap = new HashMap();
                routeMap.put(Util.ROUTE_PASSENGER_ID, Util.currentUser.id);
                Map plMap = new HashMap();
                plMap.put(Util.ROUTE_ORI_PLACE, Util.getCurrentPlace(activity));
                plMap.put(Util.ROUTE_DEST_PLACE, dest_place);
                plMap.put(Util.ROUTE_DISTANCE, distance);
                plMap.put(Util.ROUTE_DURATION, duration);
                plMap.put(Util.ROUTE_PRICE, price);
                Map place1Map = new HashMap();
                place1Map.put(place1Key, plMap);
                routeMap.put(Util.ROUTE_PLACE, place1Map);
                Util.mDatabase.child(Util.tbl_route).child(routeKey).setValue(routeMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            progressDialog.dismiss();
                            System.out.println("Data could not be saved " + databaseError.getMessage());
                        } else {
                            System.out.println("Data saved successfully.");
                            // send push data to server, with routeKey
                            rideNowHttpRequest();
                        }
                    }
                });

            }
        });
        Button btn_ride_later = (Button)v.findViewById(R.id.btn_ride_later);
        btn_ride_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Button btn_call_driver = (Button)v.findViewById(R.id.btn_call_driver);
        btn_call_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.phone_call("+61102742513");
            }
        });
        Button btn_cancel = (Button)v.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.mDatabase.child(Util.tbl_route).child(routeKey).setValue(null);
                ly_step2.setVisibility(View.GONE);
                ly_step1.setVisibility(View.VISIBLE);
            }
        });
        return v;
    }
    private void rideNowHttpRequest() {
        RequestBuilder requestBuilder = new RequestBuilder(Util.mFuncUrl + "/" + Util.func_nearby, "GET");
        requestBuilder
                .addParam("route_id", routeKey)
                .addParam("lat", String.valueOf(Util.currentLocation.getLatitude()))
                .addParam("lng", String.valueOf(Util.currentLocation.getLongitude()))
                .sendRequest(newsCallback);
    }
    RunanbleCallback newsCallback = new RunanbleCallback() {
        @Override
        public void finish(ResponseElement element) {
            progressDialog.dismiss();

            if (element.getData() != null) {
                JSONObject data = element.getData();
                int status = 400;
                try {
                    status = data.getInt("status");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (status == 200) {
                    ly_step1.setVisibility(View.GONE);
                    ly_searching_driver.setVisibility(View.VISIBLE);
                    tv_time_remaining.setVisibility(View.VISIBLE);
                    btn_searching_cancel.setVisibility(View.VISIBLE);
                    searching_background.setVisibility(View.VISIBLE);
                    mMap.getUiSettings().setAllGesturesEnabled(false);
                    mapView.setClickable(false);

                    Toast.makeText(activity, "Successfully sent to drivers!", Toast.LENGTH_LONG).show();

                    RotateAnimation rotateAnimation = new RotateAnimation(0, 360f,
                            Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f);
                    rotateAnimation.setInterpolator(new LinearInterpolator());
                    rotateAnimation.setDuration(3000);
                    rotateAnimation.setRepeatCount(Animation.INFINITE);
                    img_search_logo.startAnimation(rotateAnimation);

                    driverSearchTimer = new CountDownTimer(60000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            tv_time_remaining.setText("Remaining: " + millisUntilFinished / 1000 + "s");
                        }
                        public void onFinish() {
                            Toast.makeText(activity, "Sorry, not match driver. please expand radius finding.", Toast.LENGTH_LONG).show();
                            ly_searching_driver.setVisibility(View.GONE);
                            nested_scroll_view.setVisibility(View.VISIBLE);
                            btn_searching_cancel.setVisibility(View.GONE);
                            searching_background.setVisibility(View.GONE);
                            ly_step1.setVisibility(View.VISIBLE);
                            mMap.getUiSettings().setAllGesturesEnabled(true);
                            mapView.setClickable(true);
                            Util.mDatabase.child(Util.tbl_route).child(routeKey).setValue(null);
                        }
                    }.start();

                } else {
                    nested_scroll_view.setVisibility(View.VISIBLE);
                    ly_step1.setVisibility(View.VISIBLE);
                    Toast.makeText(activity, "No drivers!", Toast.LENGTH_LONG).show();
                }
            }
        }

    };
    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=" + directionMode;
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + MAP_VIEW_BUNDLE_KEY;
        return url;
    }
    @Override
    public void onTaskDone(long distanceVal, long durationVal, Object... values) {
        if (polyline != null) {
            polyline.remove();
        }
        polyline = mMap.addPolyline((PolylineOptions) values[0]);
        // go to step1
        distance = distanceVal;
        duration = durationVal;
        price = Math.round(((float)distance/1000) * 2 + 5);
        txt_price.setText("RM " + String.valueOf(price));
        btn_dest_confirm.setVisibility(View.GONE);
        ly_step1.setVisibility(View.VISIBLE);
    }
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
//        mMap.setMinZoomPreference(15);
        mMap.setIndoorEnabled(true);
        mMap.setMyLocationEnabled(true);

        UiSettings uiSettings = mMap.getUiSettings();
//        uiSettings.setIndoorLevelPickerEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

        // move user location button to position on right bottom
        View mylocationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mylocationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 20, 450);
        // hide zoom button
        View plButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("1"));
        plButton.setVisibility(View.GONE);
        // hide rotation button
//        View rtButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("0"));
//        rtButton.getSystemUiVisibility();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });

        if (Util.currentLocation != null) {
            updateMap();
        }

    }

    private void updateMap() {
        LatLng ny = new LatLng(Util.currentLocation.getLatitude(), Util.currentLocation.getLongitude());
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ny, 13));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ny, 15));
        String location = String.valueOf(Util.currentLocation.getLatitude()) + ", " + String.valueOf(Util.currentLocation.getLongitude());
        Toast.makeText(activity, location, Toast.LENGTH_SHORT).show();
    }

    public Bitmap createCustomMarker(Context context) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.driver_marker, null);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(20, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }
    private void cancelRoute() {
        if (driverSearchTimer != null) {
            Util.mDatabase.child(Util.tbl_route).child(routeKey).setValue(null);
            driverSearchTimer.cancel();
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (HomeActivity) context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelRoute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
