package com.teamrouteme.routeme.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.teamrouteme.routeme.R;

import java.text.ParseException;
import java.util.List;

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
        ParseQuery<ParseObject> query = ParseQuery.getQuery("itinerario");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> list, com.parse.ParseException e) {

                if (e == null) {
                    Log.d("Itinerari: ", "Retrieved " + list.size() + " routes");
                    for(ParseObject parseObject: list){

                    }


                } else {
                    Log.d("Itinerari", "Error: " + e.getMessage());
                }
            }

        });



        return rootView;
    }


}
