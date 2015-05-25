package com.teamrouteme.routeme.fragment;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dexafree.materialList.controller.RecyclerItemClickListener;
import com.dexafree.materialList.model.CardItemView;
import com.dexafree.materialList.view.MaterialListView;
import com.teamrouteme.routeme.R;
import com.teamrouteme.routeme.bean.Itinerario;
import com.teamrouteme.routeme.utility.CustomCard;

import java.util.ArrayList;
import java.util.List;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

public class RisultatiRicercaFragment extends Fragment {

    private ArrayList<Itinerario> itinerari;
    private MaterialListView listviewRisultatiItinerari;
    private List myList;

    public RisultatiRicercaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_miei_itinerari, container, false);

        Bundle b = getArguments();
        if(b != null) {
            itinerari = (ArrayList<Itinerario>) b.get("itinerari");
            }

        listviewRisultatiItinerari = (MaterialListView) view.findViewById(R.id.material_listview);

        myList = new ArrayList<Itinerario>();

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

        listviewRisultatiItinerari.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(CardItemView cardItemView, int i) {
                // Create new fragment
                Fragment anteprimaItinerarioFragment = new AnteprimaItinerarioFragment();

                Bundle b = new Bundle();
                b.putParcelable("itinerario", (Itinerario) myList.get(i));
                b.putBoolean("ifRicerca", true);
                b.putParcelableArrayList("itinerari", itinerari);
                anteprimaItinerarioFragment.setArguments(b);

                // Set new fragment on screen
                MaterialNavigationDrawer home = (MaterialNavigationDrawer) getActivity();
                home.setFragment(anteprimaItinerarioFragment, "Anteprima Itinerario");
            }

            @Override
            public void onItemLongClick(CardItemView cardItemView, int i) {

            }

        });


        return view;
    }

}
