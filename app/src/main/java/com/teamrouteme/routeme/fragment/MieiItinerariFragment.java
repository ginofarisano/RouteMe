package com.teamrouteme.routeme.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teamrouteme.routeme.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MieiItinerariFragment extends Fragment {


    public MieiItinerariFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_itinerari_caricati, container, false);
        return rootView;
    }


}
