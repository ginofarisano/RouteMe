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

import com.ms.square.android.expandabletextview.ExpandableTextView;
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
    private Itinerario itinerario;
    private ArrayList<String> tappeId;
    private Button btnIndietro;
    private ArrayList<Itinerario> itinerari;
    private Button btnAvviaItinerario, btnAcquistaItinerario, btnDesideraItinerario;
    private TextView nomeItinerarioEdit;
    private RatingBar valutazioneBar;
    private String nomeItinerario, tagsItinerario, cittaItinerario, descrizioneItinerario, autoreItinerario;
    private int durataMinItinerario, durataMaxItinerario;
    private TextView tags;
    private ExpandableTextView descrizione;
    private TextView citta;
    private TextView durata;
    private TextView autoreItinerarioEdit;

    public AnteprimaListaDesideriFragment(){
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_anteprima_itinerario, container, false);

        /*recupero dei dati dal server
        fare richiesta al server e riempire le variabili nomeItinerario, valutazione, feedback
        */

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
