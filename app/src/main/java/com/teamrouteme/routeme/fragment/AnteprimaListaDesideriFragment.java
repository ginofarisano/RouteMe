package com.teamrouteme.routeme.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.teamrouteme.routeme.R;
import com.teamrouteme.routeme.bean.Itinerario;
import com.teamrouteme.routeme.utility.ParseCall;

import java.util.ArrayList;
import java.util.List;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by ginofarisano on 27/05/15.
 */
public class AnteprimaListaDesideriFragment extends Fragment{

    private View view;
    private String nomeItinerario;
    private String valutazione;
    private String feedback;
    private Itinerario itinerario;
    private ArrayList<String> tappeId;
    private Button btnIndietro;
    private ArrayList<Itinerario> itinerari;
    private Button btnAvviaItinerario, btnAcquistaItinerario, btnDesideraItinerario;
    private int queryCount;
    private ParseObject listaAcquistatiObject;

    public AnteprimaListaDesideriFragment(){
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_anteprima_itinerario, container, false);

        /*recupero dei dati dal server
        fare richiesta al server e riempire le variabili nomeItinerario, valutazione, feedback
        */

        queryCount = 0;

        Bundle b = getArguments();
        if(b != null) {
            itinerario = (Itinerario) b.get("itinerario");
            itinerari = b.getParcelableArrayList("itinerari");
            nomeItinerario = itinerario.getNome();
            tappeId = itinerario.getTappeId();

            Log.d("", "Nome itinerario ricevuto: " + itinerario.getNome());
            Log.d("","Descrizione itinerario ricevuto: "+ itinerario.getDescrizione());
            Log.d("","Citta itinerario ricevuto: "+ itinerario.getCitta());
            Log.d("","Id itinerario ricevuto: "+ itinerario.getId());
            Log.d("","Durata Min itinerario ricevuto: "+ itinerario.getDurataMin());
            Log.d("","Durata Max itinerario ricevuto: "+ itinerario.getDurataMax());
            Log.d("","Tags itinerario ricevuto: "+ itinerario.getTags());
            Log.d("","Tappe ID itinerario ricevuto: "+ itinerario.getTappeId());
        }

        //settaggio delle variabili prese dal server
        TextView nomeItinerarioEdit = (TextView) view.findViewById(R.id.nomeItinerarioCard);
        nomeItinerarioEdit.setText(nomeItinerario);

        RatingBar valutazioneBar = (RatingBar) view.findViewById(R.id.valutazione);
        valutazioneBar.setRating(Float.parseFloat("2.0"));



        Button btnFeedback= (Button) view.findViewById(R.id.btnInviaFeedback);

        btnFeedback.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // invio a server del feedback rilasciato
            }
        });

        btnAvviaItinerario = (Button) view.findViewById(R.id.btnAvviaItinerario);
        btnAcquistaItinerario = (Button) view.findViewById(R.id.btnAcquistaItinerario);
        btnDesideraItinerario = (Button) view.findViewById(R.id.btn_desidera);

        btnAvviaItinerario.setVisibility(View.GONE);
        btnAcquistaItinerario.setVisibility(View.VISIBLE);
        btnDesideraItinerario.setVisibility(View.VISIBLE);
        btnDesideraItinerario.setText("Già cuoricino");


        btnAcquistaItinerario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                        "Caricamento in corso...", true);

                String idItinerario = itinerario.getId();
                ParseCall parseCall = new ParseCall();

                parseCall.buyRoute(idItinerario, dialog);

                //UNA VOLTA EFFETTUATA L'OPERAZIONE DI PAGAMENTO VENGONO DISATTIVATI I BOTTONI

                btnAcquistaItinerario.setEnabled(false);
                btnAcquistaItinerario.setText("Già tuo");

                btnDesideraItinerario.setEnabled(false);
                btnDesideraItinerario.setText("Già cuoricino");

            }
        });

        btnDesideraItinerario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idItinerario = itinerario.getId();
                ParseCall parseCall = new ParseCall();

                parseCall.addWishList(idItinerario);

                btnDesideraItinerario.setEnabled(false);
                btnDesideraItinerario.setText("Già cuoricino");
            }
        });



        btnIndietro = (Button) view.findViewById(R.id.btn_indietro);
        btnIndietro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment listaDesideriFragment = new ListaDesideriFragment();

                Bundle b = new Bundle();
                b.putParcelableArrayList("itinerari", itinerari);
                listaDesideriFragment.setArguments(b);

                // Set new fragment on screen
                MaterialNavigationDrawer home = (MaterialNavigationDrawer) getActivity();
                home.setFragment(listaDesideriFragment, "Lista Desideri");

            }
        });

        return view;
    }

}
