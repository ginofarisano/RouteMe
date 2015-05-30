package com.teamrouteme.routeme.fragment;


import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

        textViewCredito.setText("" + currentUser.getInt("crediti"));


        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getFragmentManager();
                final ModificaProfiloDialog modificaProfiloDialog = new ModificaProfiloDialog();

                LinearLayout ll = (LinearLayout) v.getParent();
                TextView campoProfilo  = (TextView) ll.getChildAt(0);

                Bundle b = new Bundle();
                b.putString("idCampoProfilo", (String) campoProfilo.getTag());
                b.putString("campoProfilo", campoProfilo.getText().toString());
                modificaProfiloDialog.setArguments(b);

                modificaProfiloDialog.show(fm, "fragment_modifica_profilo_dialog");
                modificaProfiloDialog.setTargetFragment(ProfiloFragment.this, 1);

            }
        };

        modNome.setOnClickListener(ocl);
        modEmail.setOnClickListener(ocl);
        modPassword.setOnClickListener(ocl);
        return view;
    }



    //Gestisce ciò che restituisce il Dialog
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent i){
        if(resultCode==1) {
            Boolean modificaEffettuata = i.getExtras().getBoolean("modificaEffettuata");
            if (modificaEffettuata)
                Toast.makeText(getActivity().getBaseContext(), "Nessuna modifica effettuata", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity().getBaseContext(), "Modifica effettuata", Toast.LENGTH_SHORT).show();
        }
    }


}
