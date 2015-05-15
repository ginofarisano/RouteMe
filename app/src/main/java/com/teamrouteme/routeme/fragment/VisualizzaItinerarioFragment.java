package com.teamrouteme.routeme.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.teamrouteme.routeme.R;

/**
 * Created by daniele on 12/05/15.
 */
public class VisualizzaItinerarioFragment extends Fragment{

    private static View view;
    private String nomeItinerario;
    private String valutazione;
    private String feedback;




    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_visualizza_itinerario, container, false);

        /*recupero dei dati dal server
        fare richiesta al server e riempire le variabili nomeItinerario, valutazione, feedback
        */

        //settaggio delle variabili prese dal server
        TextView nomeItinerarioEdit = (TextView) rootView.findViewById(R.id.nomeItinerario);
        nomeItinerarioEdit.setText(nomeItinerario);

        RatingBar valutazioneBar = (RatingBar) rootView.findViewById(R.id.valutazione);
        valutazioneBar.setRating(Float.parseFloat("2.0"));

        EditText feedbackEdit = (EditText) rootView.findViewById(R.id.feedback);
        feedbackEdit.setText(feedback);


        Button btnFeedback= (Button) rootView.findViewById(R.id.btnInviaFeedback);

        btnFeedback.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // invio a server del feedback rilasciato
            }
        });


        Button btnAvviaItinerario = (Button) rootView.findViewById(R.id.btnAvviaItinerario);

        btnAvviaItinerario.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // avvia la navigazione dell'itinerario
            }
        });




        return rootView;
    }
}