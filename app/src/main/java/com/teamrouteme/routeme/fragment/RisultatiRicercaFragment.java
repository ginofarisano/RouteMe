package com.teamrouteme.routeme.fragment;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
    private Button btnIndietro;

    public RisultatiRicercaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lista_itinerari, container, false);

        Bundle b = getArguments();
        if(b != null) {
            itinerari = b.getParcelableArrayList("itinerari");
        }

        btnIndietro = (Button) view.findViewById(R.id.btn_indietro);
        btnIndietro.setVisibility(View.VISIBLE);

        btnIndietro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment cercaItinerarioFragment = new CercaItinerarioFragment();
                // Set new fragment on screen
                MaterialNavigationDrawer home = (MaterialNavigationDrawer) getActivity();
                home.setFragment(cercaItinerarioFragment, "Cerca Itinerario");
            }
        });


        listviewRisultatiItinerari = (MaterialListView) view.findViewById(R.id.material_listview);

        for (int i = 0; i < itinerari.size(); i++) {
            Itinerario it = itinerari.get(i);
            CustomCard card = new CustomCard(getActivity().getApplicationContext());
            card.setDescription(it.getDescrizione());
            card.setTitle(it.getNome());
            if(it.getNum_feedback()!=0)
                card.setRatingBar(it.getRating()/it.getNum_feedback());
            else
                card.setRatingBar(0);
            card.setNumFeedback(it.getNum_feedback());
            card.setListTags(it.getTags());
            listviewRisultatiItinerari.add(card);
        }

        listviewRisultatiItinerari.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(CardItemView cardItemView, int i) {

                CardView c = (CardView) cardItemView.getChildAt(0);
                c.setBackgroundColor(getResources().getColor(R.color.testo));
                // Create new fragment
                Fragment anteprimaCercaItinerarioFragment = new AnteprimaCercaItinerarioFragment();

                Bundle b = new Bundle();
                b.putParcelable("itinerario", (Itinerario) itinerari.get(i));
                b.putParcelableArrayList("itinerari", itinerari);
                anteprimaCercaItinerarioFragment.setArguments(b);

                // Set new fragment on screen
                MaterialNavigationDrawer home = (MaterialNavigationDrawer) getActivity();
                home.setFragment(anteprimaCercaItinerarioFragment, "Anteprima Itinerario");
            }

            @Override
            public void onItemLongClick(CardItemView cardItemView, int i) {

            }

        });


        return view;
    }

}
