package com.teamrouteme.routeme.fragment;

import android.os.Bundle;
import android.app.Fragment;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dexafree.materialList.view.MaterialListView;
import com.teamrouteme.routeme.R;
import com.teamrouteme.routeme.adapter.CustomAdapterListaItinerari;
import com.teamrouteme.routeme.bean.Itinerario;
import com.teamrouteme.routeme.utility.CustomCard;
import com.teamrouteme.routeme.utility.CustomCardItemView;

import java.util.ArrayList;

public class RisultatiRicercaFragment extends Fragment {

    private ArrayList<Itinerario> itinerari;
    private MaterialListView listviewRisultatiItinerari;
    public RisultatiRicercaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_itinerari_caricati, container, false);

        Bundle b = getArguments();
        if(b != null) {
            itinerari = (ArrayList<Itinerario>) b.get("itinerari");
            }

        listviewRisultatiItinerari = (MaterialListView) view.findViewById(R.id.material_listview);

        ArrayList<Itinerario> myList = new ArrayList<Itinerario>();

        for (int i=0;i<itinerari.size();i++)
            myList.add(i,(Itinerario)itinerari.get(i));

        for (int i = 0; i < myList.size(); i++) {
            Itinerario it = (Itinerario) myList.get(i);
            CustomCard card = new CustomCard(getActivity().getApplicationContext());
            card.setDescription(it.getDescrizione());
            card.setTitle(it.getNome());
            card.setRatingBar(2);
            listviewRisultatiItinerari.add(card);
        }


/*
        CustomAdapterListaItinerari myAdapter = new CustomAdapterListaItinerari(RisultatiRicercaFragment.this.getActivity(),R.layout.row_custom_itinerari,myList);
        listviewRisultatiItinerari.setAdapter(myAdapter);
*/
        return view;
    }

//prelevare il bundle e stampare la lista..... (copiare da anteprimaitinerariofragment)
}
