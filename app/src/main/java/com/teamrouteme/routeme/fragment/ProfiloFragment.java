package com.teamrouteme.routeme.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseUser;
import com.teamrouteme.routeme.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfiloFragment extends Fragment {

    private ParseUser currentUser;
    private TextView  textViewName;
    private TextView textViewEmail;
    private TextView textViewCredito;


    public ProfiloFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        currentUser = ParseUser.getCurrentUser();

        textViewName = (TextView) view.findViewById(R.id.textViewNameProf);
        textViewEmail = (TextView) view.findViewById(R.id.textViewEmailModProf);
        textViewCredito = (TextView) view.findViewById(R.id.textViewCreditoModProf);

        textViewName.setText(currentUser.getEmail());
        textViewEmail.setText(currentUser.getString("name"));

        //non recupera il campo credito
        textViewCredito.setText(""+currentUser.getInt("crediti"));






        return view;
    }


}
