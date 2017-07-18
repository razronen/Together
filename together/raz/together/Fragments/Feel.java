package com.together.raz.together.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.together.raz.together.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Feel extends Fragment {


    public Feel() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feel, container, false);
    }

}
