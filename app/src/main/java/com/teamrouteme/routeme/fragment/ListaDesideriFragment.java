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
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dexafree.materialList.controller.MaterialListAdapter;
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
import com.teamrouteme.routeme.utility.CustomCard;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

public class ListaDesideriFragment extends Fragment {

    private MaterialListView listView;
    private List myList;
    private TextView nessunItineario;
    private ArrayList<Card> cardsList;

    public ListaDesideriFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.fragment_lista_itinerari, container, false);

        nessunItineario = (TextView) view.findViewById(R.id.nessunItinerario);

        listView = (MaterialListView) view.findViewById(R.id.material_listview);

        myList = new LinkedList();

        cardsList = new ArrayList<>();

        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                "Caricamento in corso...", true);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("lista_desideri");

        query = query.whereEqualTo("user", ParseUser.getCurrentUser());

        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("itinerario");

        query2.whereMatchesKeyInQuery("objectId","idItinerario",query);

        query2.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> list, com.parse.ParseException e) {

                if (e == null) {

                    Log.d("Itinerari: ", "Retrieved " + list.size() + " routes");

                    Itinerario itinerario;

                    for (ParseObject parseObject : list) {
                        itinerario = new Itinerario();

                        String idItinerario = (String) parseObject.get("idItinerario");

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
                        itinerario.setAutore(parseObject.getString("autore"));

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
                        cardsList.add(card);
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
                Fragment anteprimaListaDesideriFragment = new AnteprimaListaDesideriFragment();

                Bundle b = new Bundle();
                b.putParcelable("itinerario", (Itinerario) myList.get(i));
                anteprimaListaDesideriFragment.setArguments(b);

                // Set new fragment on screen
                MaterialNavigationDrawer home = (MaterialNavigationDrawer) getActivity();
                home.setFragment(anteprimaListaDesideriFragment, "Anteprima Itinerario");
            }

            @Override
            public void onItemLongClick(CardItemView cardItemView, int i) {
                FragmentManager fm = getFragmentManager();
                final CancellazioneItinerarioListaDesideriDialog cancellazioneItinerarioListaDesideriDialog = new CancellazioneItinerarioListaDesideriDialog();
                Bundle b = new Bundle();
                Itinerario it = (Itinerario)myList.get(i);
                b.putString("idItinerario", it.getId());
                b.putString("nomeItinerario", it.getNome());
                b.putInt("cardItemNumber", i);
                cancellazioneItinerarioListaDesideriDialog.setArguments(b);
                cancellazioneItinerarioListaDesideriDialog.show(fm, "fragment_cancellazione_lista_desideri_dialog");
                cancellazioneItinerarioListaDesideriDialog.setTargetFragment(ListaDesideriFragment.this, 0);
            }

        });

        return view;

    }

    public void onActivityResult(int requestCode, int resultCode, Intent i) {
        if (resultCode == 0) {
            // Cancellazione itinerario dalla lista dei desideri
            String idItinerario = i.getStringExtra("idItinerario");
            final int cardItemNumber = i.getIntExtra("cardItemNumber", -1);

            Log.d("ListaDesideriFragment", "Elimino itinerario con id: " + idItinerario);

            final ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                    "Caricamento in corso...", true);

            ParseQuery query = ParseQuery.getQuery("lista_desideri");
            query = query.whereEqualTo("idItinerario", idItinerario);
            query.whereEqualTo("user", ParseUser.getCurrentUser());

            query.findInBackground(new FindCallback<ParseObject>() {

                @Override
                public void done(final List<ParseObject> list, com.parse.ParseException e) {

                    if (e == null) {
                        if (list.size() == 1) {
                            list.get(0).deleteInBackground(new DeleteCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {

                                        // BUG QUI RISOLVERE
                                        MaterialListAdapter mla = (MaterialListAdapter) listView.getAdapter();
                                        mla.remove(cardsList.get(cardItemNumber), true);
                                        mla.notifyDataSetChanged();
                                        /*Log.e("", "" + listView.getAdapter());
                                        listView.remove(cardsList.get(cardItemNumber));
                                        listView.getAdapter().notifyDataSetChanged();*/
                                        /*listView.removeAllViewsInLayout();
                                        cardsList.remove(cardItemNumber);
                                        for(int i=0;i<cardsList.size();i++)
                                            listView.add(cardsList.get(i));
                                        listView.getAdapter().notifyDataSetChanged();*/
                                        dialog.hide();
                                        Toast.makeText(getActivity().getBaseContext(), "Itinerario eliminato", Toast.LENGTH_SHORT).show();
                                    } else
                                        Log.d("ListaDesideriFragment", "Error: " + e.getMessage());
                                }
                            });
                        }
                    } else {
                        Log.d("ListaDesideriFragment", "Error: " + e.getMessage());
                    }
                }

            });
        }
    }
}
