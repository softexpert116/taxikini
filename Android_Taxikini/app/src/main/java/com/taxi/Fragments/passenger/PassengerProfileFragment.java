package com.taxi.Fragments.passenger;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taxi.kini.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PassengerProfileFragment extends Fragment {


    public PassengerProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_passenger_profile, container, false);
    }

}
