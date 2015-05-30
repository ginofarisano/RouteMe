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

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dexafree.materialList.controller.OnDismissCallback;
import com.dexafree.materialList.controller.RecyclerItemClickListener;
import com.dexafree.materialList.model.Card;
import com.dexafree.materialList.model.CardItemView;
import com.dexafree.materialList.view.MaterialListView;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.teamrouteme.routeme.R;
import com.teamrouteme.routeme.bean.Itinerario;
import com.teamrouteme.routeme.bean.Tappa;
import com.teamrouteme.routeme.utility.CustomCard;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

public class MieiItinerariFragment extends Fragment {

    private MaterialListView listView;
    private List myList;
    private TextView nessunItineario;
    private int queryCount, subQueryDesideri, subQueryAcquistati, subQueryTappe;

    public MieiItinerariFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.fragment_lista_itinerari, container, false);

        nessunItineario = (TextView) view.findViewById(R.id.nessunItinerario);

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
                        itinerario.setNum_feedback(parseObject.getNumber("num_feedback").intValue());
                        itinerario.setRating(parseObject.getNumber("rating").floatValue());

                        ArrayList<String> tappe_objectId = new ArrayList<String>();

                        for (ParseObject tappa_object : (ArrayList<ParseObject>) parseObject.get("tappe")) {
                            tappe_objectId.add(tappa_object.getObjectId());
                        }
                        itinerario.setTappeId(tappe_objectId);

                        myList.add(itinerario);

                    }

                    dialog.hide();

                    if (myList.size() == 0)
                        nessunItineario.setVisibility(View.VISIBLE);

                    for (int i = 0; i < myList.size(); i++) {
                        Itinerario it = (Itinerario) myList.get(i);
                        CustomCard card = new CustomCard(getActivity().getApplicationContext());
                        card.setTitle(it.getNome());
                        card.setDescription(it.getDescrizione());
                        card.setListTags(it.getTags());
                        if (it.getNum_feedback() != 0)
                            card.setRatingBar(it.getRating() / it.getNum_feedback());
                        else
                            card.setRatingBar(0);
                        card.setNumFeedback(it.getNum_feedback());
                        card.setDismissible(true);
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

                CardView c = (CardView) cardItemView.getChildAt(0);
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

        listView.setOnDismissCallback(new OnDismissCallback() {
            @Override
            public void onDismiss(final Card card, final int i) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                cancella(i);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                Itinerario it = (Itinerario) myList.remove(i);
                                myList.add(it);
                                listView.add(card);
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Eliminare dai miei itinerari?").setPositiveButton("Si", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
            }
        });

        return view;

    }

    private void cancella(int position){
        // Cancellazione itinerario dalla lista dei desideri

        queryCount = 0;
        subQueryAcquistati = 0;
        subQueryDesideri = 0;
        subQueryTappe = 0;

        Itinerario it = (Itinerario) myList.remove(position);
        if (myList.size() == 0)
            nessunItineario.setVisibility(View.VISIBLE);

        String idItinerario = it.getId();

        Log.d("MieiItinerariFragment", "Elimino itinerario con id: " + idItinerario);

        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                "Caricamento in corso...", true);

        ParseQuery query = ParseQuery.getQuery("lista_desideri");
        query.whereEqualTo("idItinerario", idItinerario);


        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(final List<ParseObject> list, com.parse.ParseException e) {

                if (e == null) {
                    if (list.size() == 0) {
                        queryCount++;
                        if (queryCount == 4)
                            dialog.hide();
                    }
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    subQueryDesideri++;
                                    if (subQueryDesideri == list.size()) {
                                        queryCount++;
                                        if (queryCount == 4)
                                            dialog.hide();
                                    }
                                } else {
                                    Log.d("MieiItinerariFragment", "Error: " + e.getMessage());
                                    subQueryDesideri++;
                                    if (subQueryDesideri == list.size()) {
                                        queryCount++;
                                        if (queryCount == 4)
                                            dialog.hide();
                                    }
                                }
                            }
                        });
                    }
                } else {
                    queryCount++;
                    if (queryCount == 4)
                        dialog.hide();
                    Log.d("MieiItinerariFragment", "Error: " + e.getMessage());
                }
            }

        });

        ParseQuery query2 = ParseQuery.getQuery("itinerari_acquistati");
        query2.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(final List<ParseObject> list, com.parse.ParseException e) {

                if (e == null) {
                    if (list.size() == 0) {
                        queryCount++;
                        if (queryCount == 4)
                            dialog.hide();
                    }
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    subQueryAcquistati++;
                                    if (subQueryAcquistati == list.size()) {
                                        queryCount++;
                                        if (queryCount == 4)
                                            dialog.hide();
                                    }
                                } else {
                                    Log.d("MieiItinerariFragment", "Error: " + e.getMessage());
                                    subQueryAcquistati++;
                                    if (subQueryAcquistati == list.size()) {
                                        queryCount++;
                                        if (queryCount == 4)
                                            dialog.hide();
                                    }
                                }
                            }
                        });
                    }
                } else {
                    queryCount++;
                    if (queryCount == 4)
                        dialog.hide();
                    Log.d("MieiItinerariFragment", "Error: " + e.getMessage());
                }
            }

        });

        final ArrayList<Tappa> alT = new ArrayList<Tappa>();
        final ArrayList<String> tappeId = it.getTappeId();

        for (int i = 0; i < tappeId.size(); i++) {

            ParseObject parseObject = ParseObject.createWithoutData("tappa", tappeId.get(i));
            parseObject.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        subQueryTappe++;
                        if (subQueryTappe == tappeId.size()){
                            queryCount++;
                            if (queryCount == 4)
                                dialog.hide();
                        }
                    } else {
                        subQueryTappe++;
                        if (subQueryTappe == tappeId.size()){
                            queryCount++;
                            if (queryCount == 4)
                                dialog.hide();
                        }
                        Log.d("MieiItinerariFragment", "Error: " + e.getMessage());
                    }
                }
            });
        }

        ParseObject parseObject = ParseObject.createWithoutData("itinerario", idItinerario);
        parseObject.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    queryCount++;
                    if (queryCount == 4)
                        dialog.hide();
                } else {
                    queryCount++;
                    if (queryCount == 4)
                        dialog.hide();
                    Log.d("MieiItinerariFragment", "Error: " + e.getMessage());
                }
            }
        });

    }

}
