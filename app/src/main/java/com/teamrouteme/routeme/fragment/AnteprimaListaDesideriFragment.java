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
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.parse.FindCallback;
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
public class AnteprimaListaDesideriFragment extends BaseFragmentPayPalResult{

    private View view;
    private Itinerario itinerario;
    private ArrayList<String> tappeId;
    private Button btnIndietro;
    private ArrayList<Itinerario> itinerari;
    private Button btnAvviaItinerario, btnAcquistaItinerario;
    private TextView nomeItinerarioEdit;
    private RatingBar valutazioneBar;
    private String nomeItinerario, tagsItinerario, cittaItinerario, descrizioneItinerario, autoreItinerario;
    private int durataMinItinerario, durataMaxItinerario;
    private TextView tags;
    private ExpandableTextView descrizione;
    private TextView citta;
    private TextView durata;
    private TextView autoreItinerarioEdit;
    private TextView numFeedbackText;
    private ArrayAdapter<String> adapter;
    private LinearLayout listViewRecensioni;


    public AnteprimaListaDesideriFragment(){
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_anteprima_itinerario, container, false);

        listViewRecensioni = (LinearLayout)view.findViewById(R.id.listViewRecensioni);

        Bundle b = getArguments();
        if(b != null) {
            itinerario = (Itinerario) b.get("itinerario");
            itinerari = b.getParcelableArrayList("itinerari");

            nomeItinerario = itinerario.getNome();
            descrizioneItinerario = itinerario.getDescrizione();
            cittaItinerario = itinerario.getCitta();
            durataMinItinerario = itinerario.getDurataMin();
            durataMaxItinerario = itinerario.getDurataMax();
            tappeId = itinerario.getTappeId();
            autoreItinerario = itinerario.getAutore();

            Log.d("","Nome itinerario ricevuto: "+ nomeItinerario);
            Log.d("","Autore itinerario ricevuto: "+ autoreItinerario);
            Log.d("","Descrizione itinerario ricevuto: "+ descrizioneItinerario);
            Log.d("","Citta itinerario ricevuto: "+ cittaItinerario);
            Log.d("","Id itinerario ricevuto: "+ itinerario.getId());
            Log.d("","Durata Min itinerario ricevuto: "+ durataMinItinerario);
            Log.d("","Durata Max itinerario ricevuto: "+ durataMaxItinerario);
            Log.d("","Tags itinerario ricevuto: "+ itinerario.getTags());
            Log.d("","Tappe ID itinerario ricevuto: "+ tappeId);
        }

        //settaggio delle variabili prese dal server
        nomeItinerarioEdit = (TextView) view.findViewById(R.id.nomeItinerarioCard);
        nomeItinerarioEdit.setText(nomeItinerario);

        autoreItinerarioEdit = (TextView) view.findViewById(R.id.autore);
        autoreItinerarioEdit.setText(autoreItinerario);

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

        dialog = ProgressDialog.show(getActivity(), "",
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
                            if (feedback != null && feedback.length() > 0) {
                                alFeedback.add(feedbackCount + ". " + feedback);
                                Log.d("Recensione " + feedbackCount, feedback);
                                feedbackCount++;
                            }

                        }
                        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, alFeedback);
                        for (int i = 0; i < alFeedback.size(); i++) {
                            TextView t = new TextView(getActivity());
                            t.setText(alFeedback.get(i));
                            t.setTextSize(TypedValue.COMPLEX_UNIT_PT, 8);
                            t.setTextColor(getResources().getColor(R.color.black));
                            listViewRecensioni.addView(t);
                        }
                    }
                    dialog.hide();
                } else {
                    dialog.hide();
                    Toast.makeText(getActivity().getBaseContext(), "Impossibile caricare le recensioni", Toast.LENGTH_SHORT).show();
                    Log.d("AnteprimaItinerario", "Error: " + e.getMessage());
                }
            }

        });

        btnAvviaItinerario = (Button) view.findViewById(R.id.btnAvviaItinerario);
        btnAcquistaItinerario = (Button) view.findViewById(R.id.btnAcquistaItinerario);

        btnAvviaItinerario.setVisibility(View.GONE);
        btnAcquistaItinerario.setVisibility(View.VISIBLE);


        btnAcquistaItinerario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseCall parseCall = new ParseCall(getActivity());
                String idItinerario = itinerario.getId();

                dialog = ProgressDialog.show(getActivity(), "",
                        "Caricamento in corso...", true);


                int crediti = (int) ParseUser.getCurrentUser().get("crediti");

                int delta;

                if ((delta = crediti - AnteprimaCercaItinerarioFragment.PRICEROUTE) >= 0) {


                    //scala i crediti a chi compra
                    parseCall.scaleCredit(delta);

                    /**
                     * va aggiustato
                     */
                    Button button = null;

                    parseCall.buyRoute(idItinerario,dialog,button);

                    //UNA VOLTA EFFETTUATA L'OPERAZIONE DI PAGAMENTO VENGONO DISATTIVATI I BOTTONI

                    btnAcquistaItinerario.setEnabled(false);
                    btnAcquistaItinerario.setBackground(getResources().getDrawable(R.drawable.selector_disabled));
                    btnAcquistaItinerario.setText("Già tuo");
                } else {

                    buyCredit(delta, idItinerario, dialog, btnAcquistaItinerario);


                }


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
