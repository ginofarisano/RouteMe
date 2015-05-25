package com.teamrouteme.routeme.fragment;

import android.os.Bundle;
import android.app.Fragment;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.teamrouteme.routeme.R;
import com.teamrouteme.routeme.adapter.CustomAdapterListaItinerari;
import com.teamrouteme.routeme.bean.Itinerario;

import java.util.ArrayList;

public class RisultatiRicercaFragment extends Fragment {

    private ArrayList<Itinerario> itinerari;
    private ListView listviewRisultatiItinerari;
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

        listviewRisultatiItinerari = (ListView) view.findViewById(R.id.dynamiclistview);

        ArrayList<Itinerario> myList = new ArrayList<Itinerario>();

        for (int i=0;i<itinerari.size();i++)
            myList.add(i,(Itinerario)itinerari.get(i));

        CustomAdapterListaItinerari myAdapter = new CustomAdapterListaItinerari(RisultatiRicercaFragment.this.getActivity(),R.layout.row_custom_itinerari,myList);
        listviewRisultatiItinerari.setAdapter(myAdapter);

        return view;
    }

//prelevare il bundle e stampare la lista..... (copiare da anteprimaitinerariofragment)
}
