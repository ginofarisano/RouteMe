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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.teamrouteme.routeme.R;
import com.teamrouteme.routeme.adapter.CustomAdapterItinerariCreati;
import com.teamrouteme.routeme.bean.Itinerario;
import com.teamrouteme.routeme.bean.Tappa;
import com.teamrouteme.routeme.utility.ParseCall;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

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

                    CustomAdapterItinerariCreati adapter = new CustomAdapterItinerariCreati(MieiItinerariFragment.this.getActivity(), R.layout.row_custom_itinerari_creati, myList);

                    listView.setAdapter(adapter);


                } else {
                    Log.d("Itinerari", "Error: " + e.getMessage());
                }
            }

        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // Create new fragment
                Fragment anteprimaItinerarioFragment = new AnteprimaItinerarioFragment();

                Bundle b = new Bundle();
                b.putParcelable("itinerario",(Itinerario) myList.get(position));
                anteprimaItinerarioFragment.setArguments(b);

                // Set new fragment on screen
                MaterialNavigationDrawer home = (MaterialNavigationDrawer) getActivity();
                home.setFragment(anteprimaItinerarioFragment, "Anteprima Itinerario");
            }
        });

        return view;


    }
}
