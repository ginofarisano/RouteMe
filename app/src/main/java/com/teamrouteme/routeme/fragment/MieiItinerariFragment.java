/*
 * Copyright 2014 Niek Haarman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.teamrouteme.routeme.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dexafree.materialList.controller.RecyclerItemClickListener;
import com.dexafree.materialList.model.CardItemView;
import com.dexafree.materialList.view.MaterialListView;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.teamrouteme.routeme.R;
import com.teamrouteme.routeme.bean.Itinerario;
import com.teamrouteme.routeme.utility.CustomCard;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

public class MieiItinerariFragment extends Fragment {

    private MaterialListView listView;
    private List myList;

    public MieiItinerariFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.fragment_lista_itinerari, container, false);

        listView = (MaterialListView) view.findViewById(R.id.material_listview);

        myList = new LinkedList();

        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                "Caricamento in corso...", true);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("itinerario");

        query.whereEqualTo("user", ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> list, com.parse.ParseException e) {

                if (e == null) {

                    Log.d("Itinerari: ", "Retrieved " + list.size() + " routes");

                    Itinerario itinerario;

                    for (ParseObject parseObject : list) {
                        itinerario = new Itinerario();
                        itinerario.setNome((String) parseObject.get("nome"));
                        //tags
                        itinerario.setTags((ArrayList) parseObject.get("tags"));
                        itinerario.setDescrizione((String) parseObject.get("descrizione"));
                        itinerario.setCitta((String) parseObject.get("citta"));
                        itinerario.setDurataMin((Integer) parseObject.get("durata_min"));
                        itinerario.setDurataMax((Integer) parseObject.get("durata_max"));
                        itinerario.setId(parseObject.getObjectId());

                        ArrayList<String> tappe_objectId = new ArrayList<String>();

                        for (ParseObject tappa_object : (ArrayList<ParseObject>) parseObject.get("tappe")) {
                            tappe_objectId.add(tappa_object.getObjectId());
                        }
                        itinerario.setTappeId(tappe_objectId);

                        myList.add(itinerario);

                    }

                    dialog.hide();

/*
                    CustomAdapterListaItinerari adapter = new CustomAdapterListaItinerari(MieiItinerariFragment.this.getActivity(), R.layout.row_custom_itinerari_creati, myList);


                    listView.setAdapter(adapter);*/
                    for (int i = 0; i < myList.size(); i++) {
                        Itinerario it = (Itinerario) myList.get(i);
                        CustomCard card = new CustomCard(getActivity().getApplicationContext());
                        card.setTitle(it.getNome());
                        card.setDescription(it.getDescrizione());
                        card.setListTags(it.getTags());
                        card.setRatingBar(2);
                        listView.add(card);
                    }


                } else {
                    Log.d("Itinerari", "Error: " + e.getMessage());
                }
            }

        });

        listView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(CardItemView cardItemView, int i) {
                // Create new fragment

                CardView c = (CardView)cardItemView.getChildAt(0);
                c.setBackgroundColor(getResources().getColor(R.color.testo));
                Fragment anteprimaMieiItinerariFragment = new AnteprimaMieiItinerariFragment();

                Bundle b = new Bundle();
                b.putParcelable("itinerario", (Itinerario) myList.get(i));
                anteprimaMieiItinerariFragment.setArguments(b);

                // Set new fragment on screen
                MaterialNavigationDrawer home = (MaterialNavigationDrawer) getActivity();
                home.setFragment(anteprimaMieiItinerariFragment, "Anteprima Itinerario");
            }

            @Override
            public void onItemLongClick(CardItemView cardItemView, int i) {
            }

        });


        return view;


    }
}
