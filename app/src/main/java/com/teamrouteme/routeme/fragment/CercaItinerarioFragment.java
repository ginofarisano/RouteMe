package com.teamrouteme.routeme.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ogaclejapan.arclayout.ArcLayout;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.teamrouteme.routeme.activity.HomeActivity;
import com.teamrouteme.routeme.adapter.CustomAutoCompleteView;
import com.teamrouteme.routeme.R;
import com.teamrouteme.routeme.bean.Itinerario;
import com.teamrouteme.routeme.utility.ArcLayoutButton;
import com.teamrouteme.routeme.utility.ClipRevealFrame;
import com.teamrouteme.routeme.utility.MyOnOpenTagsListener;
import com.teamrouteme.routeme.utility.ParseCall;
import com.yahoo.mobile.client.android.util.RangeSeekBar;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

public class CercaItinerarioFragment extends Fragment {

    private View rootLayout;
    private ClipRevealFrame menuLayout;
    private ArcLayout arcLayout;
    private Button centerItem;
    private  ArrayList<String> listTags;

    private RangeSeekBar<Integer> rangeSeekBar;
    private LinearLayout seekbar_placeholder_layout;

    private CustomAutoCompleteView autoCompleteCitta;

    private ArrayAdapter<String> autoCompleteAdapter;

    private Button btn_cercaItinerario;


    private List myList;

    public CercaItinerarioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cerca_itinerario, container, false);

        listTags = new ArrayList<>();

        rootLayout = view.findViewById(R.id.layout_fragment_cerca_itinerario);
        menuLayout = (ClipRevealFrame) view.findViewById(R.id.menu_layout);
        arcLayout = (ArcLayout) view.findViewById(R.id.arc_layout);
        centerItem = (Button) view.findViewById(R.id.center_item);

        btn_cercaItinerario = (Button) view.findViewById(R.id.btn_cercaItinerario);

        seekbar_placeholder_layout = (LinearLayout) view.findViewById(R.id.seekbar_placeholder);


        autoCompleteAdapter = new ArrayAdapter<String>(CercaItinerarioFragment.this.getActivity(), android.R.layout.simple_dropdown_item_1line);
        autoCompleteAdapter.setNotifyOnChange(true); // This is so I don't have to manually sync whenever changed
        autoCompleteCitta = (CustomAutoCompleteView)  view.findViewById(R.id.autoCompleteCities);
        autoCompleteCitta.setHint("Inserisci qui la città da ricercare");
        autoCompleteCitta.setThreshold(3);
        autoCompleteCitta.setAdapter(autoCompleteAdapter);
        autoCompleteCitta.setText("");

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

                            HashMap<String,ParseObject> listToSet = new HashMap<String, ParseObject>();
                            for(ParseObject s : list){
                                listToSet.put(s.getString("citta"),s);
                            }


                            for(Map.Entry<String,ParseObject> entry : listToSet.entrySet()){

                                //Log.d("Città", (String) parseObject.getValue().get("citta"));
                                Log.d("Città", (String) entry.getValue().getString("citta"));
                                //autoCompleteAdapter.add((String) parseObject.getString("citta"));
                                autoCompleteAdapter.add(entry.getValue().getString("citta"));

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

        rangeSeekBar.setRangeValues(1, 10);
        rangeSeekBar.setSelectedMinValue(1);
        rangeSeekBar.setSelectedMaxValue(10);


        // Add to layout
        seekbar_placeholder_layout.addView(rangeSeekBar);

        //Setta il listener per il bottone di apertura dei tag
        ArrayList<View> vL = new ArrayList<View>();
        vL.add(rangeSeekBar);
        vL.add(autoCompleteCitta);
        vL.add(btn_cercaItinerario);
        view.findViewById(R.id.open_tags).setOnClickListener(new MyOnOpenTagsListener(rootLayout, menuLayout, arcLayout, centerItem, vL));



        final ArrayList<Integer> colours = getTagsColours();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("tags");

        final HomeActivity home = (HomeActivity) getActivity();

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
                        ArcLayoutButton b = new ArcLayoutButton(home);
                        b.setButtonAttributes(tags.get(i), colours.get(i % colours.size()));
                        b.setOnTouchListener(new tagButtonOnClick());
                        arcLayout.addView(b);
                    }


                } else {
                    //error
                    Log.d("Tags", "Error: " + e.getMessage());
                }


            }
        });

        btn_cercaItinerario.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                String citta = autoCompleteCitta.getText().toString();
                int durataMin = rangeSeekBar.getSelectedMinValue();
                int durataMax = rangeSeekBar.getSelectedMaxValue();

                Log.d("Min", ""+durataMin);
                Log.d("Max", ""+durataMax);

                ParseQuery<ParseObject> query = ParseQuery.getQuery("itinerario");
                //ParseQuery<ParseObject> listQuery = query;

                if(citta.length()!=0)
                    query = query.whereEqualTo("citta", citta);

                query = query.whereGreaterThanOrEqualTo("durata_min", durataMin);
                query = query.whereLessThanOrEqualTo("durata_max", durataMax);

                if(listTags.size()!=0)
                    query = query.whereContainedIn("tags", listTags);

                final ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                        "Caricamento in corso...", true);

                query.orderByDescending("num_feedback");
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, com.parse.ParseException e) {
                        if (e == null) {
                            Log.d("NumRis", ""+list.size());
                            myList = new LinkedList();
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
                                itinerario.setAutore(parseObject.getString("autore"));

                                ArrayList<String> tappe_objectId = new ArrayList<String>();

                                for (ParseObject tappa_object : (ArrayList<ParseObject>) parseObject.get("tappe")) {
                                    tappe_objectId.add(tappa_object.getObjectId());
                                }
                                itinerario.setTappeId(tappe_objectId);

                                myList.add(itinerario);

                            }
                            if (myList.size()==0)
                                Toast.makeText(getActivity().getBaseContext(), "Nessuna corrispondenza trovata", Toast.LENGTH_SHORT).show();
                            else{
                                dialog.hide();

                                Fragment risultatiRicercaFragment = new RisultatiRicercaFragment();
                                Bundle b = new Bundle();

                                ArrayList<Itinerario> mL = new ArrayList<Itinerario>();
                                for(int i=0;i<myList.size();i++)
                                    mL.add(i,(Itinerario)myList.get(i));

                                closeKeyboard(getActivity(), autoCompleteCitta.getWindowToken());

                                b.putParcelableArrayList("itinerari", (ArrayList<Itinerario>) mL);
                                risultatiRicercaFragment.setArguments(b);

                                // Set new fragment on screen
                                MaterialNavigationDrawer home = (MaterialNavigationDrawer) getActivity();
                                home.setFragment(risultatiRicercaFragment, "Risultato Ricerca");

                            }

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

    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

}