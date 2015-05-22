package com.teamrouteme.routeme.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.teamrouteme.routeme.R;

/**
 * Created by massimo299 on 22/05/15.
 */
public class AnteprimaItinerarioFragment extends Fragment {

    private Button btnAvvia;

    public AnteprimaItinerarioFragment(){
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_anteprima_itinerario, container, false);

        btnAvvia = (Button) rootView.findViewById(R.id.btn_avvia);
        btnAvvia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return rootView;
    }
}
