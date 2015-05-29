package com.teamrouteme.routeme.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.teamrouteme.routeme.R;
import com.teamrouteme.routeme.bean.Itinerario;
import com.teamrouteme.routeme.bean.Tappa;
import com.teamrouteme.routeme.utility.ParseCall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by daniele on 12/05/15.
 */
public class AnteprimaMieiItinerariFragment extends Fragment{

    private View view;
    private Itinerario itinerario;
    private ArrayList<String> tappeId;
    private Button btnIndietro;
    private Button btnAvviaItinerario;
    private RatingBar valutazioneBar;
    private TextView nomeItinerarioEdit;
    private ExpandableTextView descrizione;
    private TextView citta;
    private TextView tags;
    private String nomeItinerario, tagsItinerario, cittaItinerario, descrizioneItinerario;
    private int durataMinItinerario, durataMaxItinerario;
    private View autoreTextView;
    private TextView durata;
    private TextView numFeedbackText;
    private ArrayAdapter<String> adapter;
    private LinearLayout listViewRecensioni;

    public AnteprimaMieiItinerariFragment(){
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_anteprima_itinerario, container, false);

        listViewRecensioni = (LinearLayout)view.findViewById(R.id.listViewRecensioni);

        Bundle b = getArguments();
        if(b != null) {
            itinerario = (Itinerario) b.get("itinerario");

            nomeItinerario = itinerario.getNome();
            descrizioneItinerario = itinerario.getDescrizione();
            cittaItinerario = itinerario.getCitta();
            durataMinItinerario = itinerario.getDurataMin();
            durataMaxItinerario = itinerario.getDurataMax();
            tappeId = itinerario.getTappeId();

            Log.d("","Nome itinerario ricevuto: "+ nomeItinerario);
            Log.d("","Descrizione itinerario ricevuto: "+ descrizioneItinerario);
            Log.d("","Citta itinerario ricevuto: "+ cittaItinerario);
            Log.d("","Id itinerario ricevuto: "+ itinerario.getId());
            Log.d("","Durata Min itinerario ricevuto: "+ durataMinItinerario);
            Log.d("","Durata Max itinerario ricevuto: "+ durataMaxItinerario);
            Log.d("","Tags itinerario ricevuto: "+ itinerario.getTags());
            Log.d("","Tappe ID itinerario ricevuto: "+ tappeId);
        }

        //Setting delle variabili ricevute dal server
        autoreTextView = view.findViewById(R.id.autore);
        autoreTextView.setVisibility(View.GONE);

        nomeItinerarioEdit = (TextView) view.findViewById(R.id.nomeItinerarioCard);
        nomeItinerarioEdit.setText(nomeItinerario);

        valutazioneBar = (RatingBar) view.findViewById(R.id.valutazione);
        if(itinerario.getNum_feedback()!=0)
            valutazioneBar.setRating(itinerario.getRating()/itinerario.getNum_feedback());
        else
            valutazioneBar.setRating(0);

        numFeedbackText = (TextView) view.findViewById(R.id.textViewNumFeedback);
        numFeedbackText.setText("("+itinerario.getNum_feedback()+")");

        descrizione = (ExpandableTextView)view.findViewById(R.id.expand_text_view);
        descrizione.setText(descrizioneItinerario);

        durata = (TextView) view.findViewById(R.id.durata_anteprima);
        durata.setText(durataMinItinerario+"-"+durataMaxItinerario+" ore");

        citta = (TextView)view.findViewById(R.id.citta_anteprima);
        citta.setText(cittaItinerario);

        tagsItinerario = itinerario.getTags().get(0);

        for(int i=1; i<itinerario.getTags().size(); i++)
            tagsItinerario+=", " + itinerario.getTags().get(i);

        tags = (TextView)view.findViewById(R.id.tag_anteprima);
        tags.setText(tagsItinerario);

        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                "Caricamento in corso...", true);

        // Carica e mette a video le recensioni dell'itinerario
        ParseQuery query = ParseQuery.getQuery("itinerari_acquistati");
        query.whereEqualTo("idItinerario", itinerario.getId());

        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> list, com.parse.ParseException e) {

                if (e == null) {
                    if (list.size() != 0) {
                        ArrayList<String> alFeedback = new ArrayList<String>();
                        int feedbackCount = 1;
                        for (int i = 0; i < list.size(); i++) {
                            ParseObject parseObject = list.get(i);
                            String feedback = parseObject.getString("feedback");
                            if(feedback != null && feedback.length() > 0) {
                                alFeedback.add(feedbackCount + ". " + feedback);
                                Log.d("Recensione " + feedbackCount, feedback);
                                feedbackCount++;
                            }

                        }
                        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, alFeedback);
                        for(int i=0; i<alFeedback.size();i++){
                            TextView t = new TextView(getActivity());
                            t.setText(alFeedback.get(i));
                            t.setTextSize(TypedValue.COMPLEX_UNIT_PT,8);
                            t.setTextColor(getResources().getColor(R.color.black));
                            listViewRecensioni.addView(t);
                        }
                    }
                    dialog.hide();
                } else {
                    Log.d("AnteprimaItinerario", "Error: " + e.getMessage());
                }
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

                Fragment mieiItinerariFragment = new MieiItinerariFragment();
                // Set new fragment on screen
                MaterialNavigationDrawer home = (MaterialNavigationDrawer) getActivity();
                home.setFragment(mieiItinerariFragment, "Miei Itinerari");

            }
        });

        return view;
    }
}