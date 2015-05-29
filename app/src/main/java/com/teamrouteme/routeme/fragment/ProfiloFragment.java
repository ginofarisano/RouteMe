package com.teamrouteme.routeme.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
    private TextView textViewCredito, textViewPassword, textViewFacebook;
    private ImageButton modNome, modEmail, modPassword;


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
        textViewPassword = (TextView) view.findViewById(R.id.textViewPasswordModProf);
        textViewFacebook = (TextView) view.findViewById(R.id.textViewFacebook);
        modNome = (ImageButton) view.findViewById(R.id.imageButtonModNameProf);
        modEmail = (ImageButton) view.findViewById(R.id.imageButtonModEmailProf);
        modPassword = (ImageButton) view.findViewById(R.id.imageButtonModPasswordProf);

        textViewName.setText(currentUser.getString("name"));
        if(currentUser.getEmail()!=null)
            textViewEmail.setText(currentUser.getEmail());
        else{
            textViewEmail.setVisibility(View.GONE);
            modEmail.setVisibility(View.GONE);
            textViewPassword.setVisibility(View.GONE);
            modPassword.setVisibility(View.GONE);
            textViewFacebook.setVisibility(View.VISIBLE);
        }

        textViewCredito.setText(""+currentUser.getInt("crediti"));






        return view;
    }


}
