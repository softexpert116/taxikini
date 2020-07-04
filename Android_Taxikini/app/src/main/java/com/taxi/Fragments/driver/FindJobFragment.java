package com.taxi.Fragments.driver;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
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
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.taxi.common.Utils.Util;
import com.taxi.kini.HomeActivity;
import com.taxi.kini.R;

import static com.taxi.kini.HomeActivity.MAP_VIEW_BUNDLE_KEY;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindJobFragment extends Fragment implements OnMapReadyCallback {

    HomeActivity activity;

    private GoogleMap mMap;
    private MapView mapView;

    RelativeLayout rl_accept, rl_decline, ly_step1, ly_step1_passenger_img, ly_step2;
    LinearLayout ly_goto_offline, ly_goto_online, ly_searching;
    Button btn_goto_offline, btn_goto_online;
    ImageView img_search_logo;

//    for all
    public FindJobFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_find_job, container, false);
        // Map View
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = v.findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        activity.locationUpdateCallback = new HomeActivity.LocationUpdateCallback() {
            @Override
            public void locationUpdateCallback() {
                updateMap();
            }
        };

        rl_accept = (RelativeLayout)v.findViewById(R.id.rl_accept);
        rl_decline = (RelativeLayout)v.findViewById(R.id.rl_decline);
        ly_step1 = (RelativeLayout)v.findViewById(R.id.ly_step1);
        ly_step1_passenger_img = (RelativeLayout) v.findViewById(R.id.ly_step1_passenger_img);
        ly_step2 = (RelativeLayout)v.findViewById(R.id.ly_step2);
        ly_goto_offline = (LinearLayout)v.findViewById(R.id.ly_goto_offline);
        ly_goto_online = (LinearLayout)v.findViewById(R.id.ly_goto_online);
        img_search_logo = (ImageView)v.findViewById(R.id.img_search_logo);
        ly_searching = (LinearLayout)v.findViewById(R.id.ly_searching);

        ly_goto_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ly_goto_online.setVisibility(View.GONE);
                ly_goto_offline.setVisibility(View.VISIBLE);
                Util.currentUser.online = "1";
                Util.mDatabase.child(Util.tbl_user).child(Util.mUser.getUid()).child(Util.USER_ONLINE).setValue(Util.currentUser.online);

                ly_searching.setVisibility(View.VISIBLE);

                RotateAnimation rotateAnimation = new RotateAnimation(0, 360f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setInterpolator(new LinearInterpolator());
                rotateAnimation.setDuration(3000);
                rotateAnimation.setRepeatCount(Animation.INFINITE);
                img_search_logo.startAnimation(rotateAnimation);
            }
        });

        ly_goto_offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ly_goto_online.setVisibility(View.VISIBLE);
                ly_goto_offline.setVisibility(View.GONE);
                ly_searching.setVisibility(View.GONE);
                Util.currentUser.online = "0";
                Util.mDatabase.child(Util.tbl_user).child(Util.mUser.getUid()).child(Util.USER_ONLINE).setValue(Util.currentUser.online);
            }
        });

        rl_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ly_step1.setVisibility(View.GONE);
                ly_step1_passenger_img.setVisibility(View.GONE);
                ly_step2.setVisibility(View.VISIBLE);
            }
        });

        return v;
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
        rlp.setMargins(0, 0, 20, 300);
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ny, 13));
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
    public void onDetach() {
        super.onDetach();
    }
}
