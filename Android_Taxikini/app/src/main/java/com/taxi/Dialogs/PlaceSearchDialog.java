package com.taxi.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.taxi.Fragments.passenger.BookRideFragment;
import com.taxi.kini.HomeActivity;
import com.taxi.kini.R;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;


public class PlaceSearchDialog extends Dialog  {
    private Activity activity;
    private BookRideFragment fragment;
    public interface OnClickListner {
        void OnClickOk();
        void OnClickCancel();
    }

    public OnClickListner onClickListner = null;
    PlaceAutocompleteFragment autocompleteFragment;

    public PlaceSearchDialog(Activity _activity, BookRideFragment _fragment) {
        super(_activity, R.style.Theme_AppCompat_Dialog);
        activity = _activity;
        fragment = _fragment;
//        setContentView(R.layout.dialog_place_search);
        Window window = this.getWindow();
        window.setGravity(Gravity.CENTER);

        autocompleteFragment = (PlaceAutocompleteFragment)
                activity.getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        ((EditText)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(10.0f);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                fragment.txt_dest.setText(place.getAddress());
            }

            @Override
            public void onError(Status status) {
                Log.e("Error", status.getStatusMessage());
                Toast.makeText(activity, status.getStatusMessage(), Toast.LENGTH_LONG).show();
            }
        });


        Button btn_ok = (Button)findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListner.OnClickOk();
            }
        });
        Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListner.OnClickCancel();
            }
        });
    }


    public void removeFragment() {
        if (autocompleteFragment != null)
            activity.getFragmentManager().beginTransaction().remove(autocompleteFragment).commit();
    }
}
