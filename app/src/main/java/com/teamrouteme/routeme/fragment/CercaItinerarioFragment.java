package com.teamrouteme.routeme.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.ogaclejapan.arclayout.ArcLayout;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.teamrouteme.routeme.adapter.CustomAdapterListaItinerari;
import com.teamrouteme.routeme.adapter.CustomAutoCompleteView;
import com.teamrouteme.routeme.R;
import com.teamrouteme.routeme.bean.Itinerario;
import com.teamrouteme.routeme.utility.ArcLayoutButton;
import com.teamrouteme.routeme.utility.ClipRevealFrame;
import com.teamrouteme.routeme.utility.ParseCall;
import com.teamrouteme.routeme.utility.onOpenTagsListener;
import com.yahoo.mobile.client.android.util.RangeSeekBar;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CercaItinerarioFragment extends Fragment {

    private View rootLayout;
    private ClipRevealFrame menuLayout;
    private ArcLayout arcLayout;
    private Button centerItem;
    private  ArrayList<String> listTags = new ArrayList<>();

    private RangeSeekBar<Integer> rangeSeekBar;
    private LinearLayout seekbar_placeholder_layout;
    private AutoCompleteTextView acmpCitta;

    private CustomAutoCompleteView autoCompleteCitta;

    private ArrayAdapter<String> autoCompleteAdapter;

    private ParseCall parseCall;

    private Button btn_cercaItinerario;

    private ListView listviewRisultatiItinerari;

    private List myList = new LinkedList();

    public CercaItinerarioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cerca_itinerario, container, false);

        rootLayout = view.findViewById(R.id.layout_fragment_cerca_itinerario);
        menuLayout = (ClipRevealFrame) view.findViewById(R.id.menu_layout);
        arcLayout = (ArcLayout) view.findViewById(R.id.arc_layout);
        centerItem = (Button) view.findViewById(R.id.center_item);


        seekbar_placeholder_layout = (LinearLayout) view.findViewById(R.id.seekbar_placeholder);

        listviewRisultatiItinerari = (ListView) view.findViewById(R.id.listviewRisultatiItinerari);

        autoCompleteAdapter = new ArrayAdapter<String>(CercaItinerarioFragment.this.getActivity(), android.R.layout.simple_dropdown_item_1line);
        autoCompleteAdapter.setNotifyOnChange(true); // This is so I don't have to manually sync whenever changed
        autoCompleteCitta = (CustomAutoCompleteView)  view.findViewById(R.id.autoCompleteCities);
        autoCompleteCitta.setHint("Country");
        autoCompleteCitta.setThreshold(3);
        autoCompleteCitta.setAdapter(autoCompleteAdapter);

        final TextWatcher textChecker = new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

                ParseQuery<ParseObject> query = ParseQuery.getQuery("itinerario");

                query.whereMatches("citta", "("+s.toString()+")", "i");

                //query.whereContains("citta", s.toString());

                query.findInBackground(new FindCallback<ParseObject>() {

                    @Override
                    public void done(List<ParseObject> list, com.parse.ParseException e) {

                        if (e == null) {

                            autoCompleteAdapter.clear();

                            for(ParseObject parseObject : list){

                                Log.d("Città", (String)parseObject.get("citta"));

                                autoCompleteAdapter.add((String)parseObject.getString("citta"));

                            }

                        } else {
                            Log.d("Itinerari", "Error: " + e.getMessage());
                        }
                    }

                });


            }
        };



        autoCompleteCitta.addTextChangedListener(textChecker);


        //btn_confermaItinerario = (Button)view.findViewById(R.id.btnCerca);


        // Setup the new range seek bar
        rangeSeekBar = new RangeSeekBar<Integer>(getActivity());
        // Set the range
        rangeSeekBar.setRangeValues(0, 10);
        rangeSeekBar.setSelectedMinValue(1);
        rangeSeekBar.setSelectedMaxValue(10);

        // Add to layout
        seekbar_placeholder_layout.addView(rangeSeekBar);

        Log.e("",""+listTags.size());

        //Setta il listener per il bottone di apertura dei tag
        ArrayList<View> vL = new ArrayList<View>();
        view.findViewById(R.id.open_tags).setOnClickListener(new onOpenTagsListener(rootLayout, menuLayout, arcLayout, centerItem, vL));



        final ArrayList<Integer> colours = getTagsColours();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("tags");


        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, com.parse.ParseException e) {
                if (e == null) {
                    //ok

                    ArrayList<String> tags = new ArrayList<String>();

                    String tagName;

                    for (ParseObject parseObjetc : list) {

                        tagName = (String) parseObjetc.get("tagname");
                        tags.add(tagName);

                    }
                    //Istanzia dinamicamente i bottoni per i tag e gli assegna il listener
                    for (int i = 0; i < tags.size(); i++) {
                        ArcLayoutButton b = new ArcLayoutButton(getActivity().getApplicationContext());
                        b.setButtonAttributes(tags.get(i), colours.get(i % colours.size()));
                        b.setOnTouchListener(new tagButtonOnClick());
                        if (listTags.contains(b.getText()))
                            b.setPressed(true);
                        arcLayout.addView(b);
                    }


                } else {
                    //error
                    Log.d("Tags", "Error: " + e.getMessage());
                }


            }
        });

        btn_cercaItinerario = (Button) view.findViewById(R.id.btn_cercaItinerario);

        btn_cercaItinerario.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                String citta = autoCompleteCitta.getText().toString();
                int durataMin = rangeSeekBar.getSelectedMinValue();
                int durataMax = rangeSeekBar.getSelectedMaxValue();

                ParseQuery<ParseObject> query = ParseQuery.getQuery("itinerario");

                if(citta.length()!=0)
                    query.whereEqualTo("citta", citta);

                query.whereLessThanOrEqualTo("durata_min", durataMin);
                query.whereGreaterThanOrEqualTo("durata_max", durataMax);

                if(listTags.size()!=0)
                    query.whereContainedIn("tags", listTags);

                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, com.parse.ParseException e) {
                        if (e == null) {

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

                            CustomAdapterListaItinerari myAdapter = new CustomAdapterListaItinerari(CercaItinerarioFragment.this.getActivity(),R.layout.row_custom_itinerari,myList);

                            listviewRisultatiItinerari.setAdapter(myAdapter);



                        } else {
                            //error
                            Log.d("Tags", "Error: " + e.getMessage());
                        }


                    }
                });


            }
        });



        return view;
    }

    //Metodo che recupera tutti i drawable per i bottoni con i tag
    private ArrayList<Integer> getTagsColours(){
        Field[] fields = R.drawable.class.getFields();
        ArrayList<Integer> drawables = new ArrayList<Integer>();
        for (Field field : fields) {
            // Take only those with name starting with "fr"
            if (field.getName().startsWith("tumblr_")) {
                try {
                    drawables.add(field.getInt(null));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return drawables;
    }

    //Listener per i bottoni con i tag
    private class tagButtonOnClick implements View.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN: v.setPressed(!v.isPressed());
                    if (v instanceof Button) {
                        String tagName = ((Button) v).getText().toString();
                        if(!listTags.contains(tagName)){
                            listTags.add(tagName);
                            Toast.makeText(getActivity().getBaseContext(), "Aggiunto "+((Button) v).getText(), Toast.LENGTH_SHORT).show();
                        } else {
                            listTags.remove(tagName);
                            Toast.makeText(getActivity().getBaseContext(), "Rimosso "+((Button) v).getText(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
            return  true;
        }
    }

}

