package com.taxi.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taxi.kini.HomeActivity;
import com.taxi.kini.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SupportFragment extends Fragment {

    HomeActivity activity;

    public SupportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_support, container, false);
        return v;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (HomeActivity) context;
    }

}
