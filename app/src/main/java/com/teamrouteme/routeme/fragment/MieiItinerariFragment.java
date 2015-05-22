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
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.teamrouteme.routeme.R;
import com.teamrouteme.routeme.adapter.CustomAdapterItinerariCreati;
import com.teamrouteme.routeme.bean.Itinerario;
import com.teamrouteme.routeme.utility.ParseCall;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MieiItinerariFragment extends Fragment {

    private ListView listView;
    private List myList;

    public MieiItinerariFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_itinerari_caricati, container, false);


        listView = (ListView) view.findViewById(R.id.dynamiclistview);

        myList = new LinkedList();

        ParseCall parseCall = new ParseCall();

        final ArrayList<Itinerario> itinerari = new ArrayList<Itinerario>();

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
                        itinerario.setDurataMin((Integer) parseObject.get("durata_max"));

                        String tappa_objectId;

                       /* for (ParseObject tappa_object : (ArrayList<ParseObject>) parseObject.get("tappe")) {
                            tappa_objectId = (String) tappa_object.getObjectId();
                            tappa = returnDataCreatesStagesToParse(tappa_objectId);
                            tappe.add(tappa);
                        }*/

                        myList.add(itinerario);

                    }

                    final CustomAdapterItinerariCreati adapter = new CustomAdapterItinerariCreati(MieiItinerariFragment.this.getActivity(), R.layout.row_custom_itinerari_creati, myList);

                    listView.setAdapter(adapter);


                } else {
                    Log.d("Itinerari", "Error: " + e.getMessage());
                }
            }

        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // Create new fragment and transaction
                Fragment anteprimaItinerarioFragment = new AnteprimaItinerarioFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack if needed
                transaction.replace(R.layout.fragment_itinerari_caricati, anteprimaItinerarioFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });

        return view;


    }
}

