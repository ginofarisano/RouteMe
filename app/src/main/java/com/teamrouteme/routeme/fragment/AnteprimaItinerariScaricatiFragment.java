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

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.teamrouteme.routeme.R;
import com.teamrouteme.routeme.bean.Itinerario;
import com.teamrouteme.routeme.bean.Tappa;

import java.util.ArrayList;
import java.util.List;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by massimo299 on 26/05/15.
 */
public class AnteprimaItinerariScaricatiFragment extends  Fragment{

    private View view;
    private String nomeItinerario;
    private String valutazione;
    private String feedback;
    private Itinerario itinerario;
    private ArrayList<String> tappeId;
    private Button btnIndietro;
    private ArrayList<Itinerario> itinerari;
    private Button btnAvviaItinerario;

    public AnteprimaItinerariScaricatiFragment(){
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_anteprima_itinerario, container, false);

        /*recupero dei dati dal server
        fare richiesta al server e riempire le variabili nomeItinerario, valutazione, feedback
        */

        Bundle b = getArguments();
        if(b != null) {
            itinerario = (Itinerario) b.get("itinerario");

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

        EditText feedbackEdit = (EditText) view.findViewById(R.id.feedback);
        feedbackEdit.setText(feedback);


        Button btnFeedback= (Button) view.findViewById(R.id.btnInviaFeedback);

        btnFeedback.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // invio a server del feedback rilasciato
            }
        });

        btnAvviaItinerario = (Button) view.findViewById(R.id.btnAvviaItinerario);

        btnAvviaItinerario.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                        "Caricamento in corso...", true);

                final ArrayList<Tappa> alT = new ArrayList<Tappa>();

                for (int i = 0; i < tappeId.size(); i++) {

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("tappa");

                    query.whereEqualTo("objectId", tappeId.get(i));

                    query.findInBackground(new FindCallback<ParseObject>() {

                        @Override
                        public void done(List<ParseObject> list, com.parse.ParseException e) {

                            if (e == null) {

                                Log.d("Tappa: ", "Retrieved " + list.size());

                                Tappa t = new Tappa();

                                for (ParseObject parseObject : list) {
                                    t.setNome((String) parseObject.get("nome"));
                                    t.setDescrizione((String) parseObject.get("descrizione"));
                                    t.setCoordinate(parseObject.getParseGeoPoint("location"));

                                    ArrayList<String> tappe_objectId = new ArrayList<String>();

                                    Log.d("", "Nome tappa: " + t.getNome());
                                    Log.d("", "Descrizione tappa: " + t.getDescrizione());
                                    Log.d("", "Location tappa: " + t.getCoordinate());

                                    alT.add(t);

                                }

                                if (alT.size() == tappeId.size()) {

                                    itinerario.setTappe(alT);

                                    dialog.hide();

                                    // Avvia la navigazione dell'itinerario
                                    // Create new fragment
                                    Fragment visualizzaItinerarioFragment = new VisualizzaItinerarioFragment();

                                    Bundle b = new Bundle();
                                    b.putParcelable("itinerario", itinerario);
                                    b.putBoolean("itinerariScaricati", true);
                                    visualizzaItinerarioFragment.setArguments(b);

                                    // Set new fragment on screen
                                    MaterialNavigationDrawer home = (MaterialNavigationDrawer) getActivity();
                                    home.setFragment(visualizzaItinerarioFragment, "Naviga Itinerario");
                                }

                            } else {
                                Log.d("Itinerari", "Error: " + e.getMessage());
                            }
                        }
                    });
                }
            }
        });


        btnIndietro = (Button) view.findViewById(R.id.btn_indietro);
        btnIndietro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment itinerariScaricatiFragment = new ItinerariScaricatiFragment();
                // Set new fragment on screen
                MaterialNavigationDrawer home = (MaterialNavigationDrawer) getActivity();
                home.setFragment(itinerariScaricatiFragment, "Itinerari Scaricati");

            }
        });

        return view;
    }
}
