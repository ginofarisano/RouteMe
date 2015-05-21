package com.teamrouteme.routeme.fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.ogaclejapan.arclayout.ArcLayout;
import com.teamrouteme.routeme.utility.AnimatorUtils;
import com.teamrouteme.routeme.R;
import com.teamrouteme.routeme.utility.ArcLayoutButton;
import com.teamrouteme.routeme.utility.ClipRevealFrame;
import com.teamrouteme.routeme.utility.onOpenTagsListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CercaItinerarioFragment extends Fragment {

    private View rootLayout;
    private ClipRevealFrame menuLayout;
    private ArcLayout arcLayout;
    private Button centerItem;
    private  ArrayList<String> listTags = new ArrayList<>();;

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

        Log.e("",""+listTags.size());

        //I tag devono essere caricati dal database, questa riga è di esempio
        String [] tags = {"Musica", "Fun", "Sport", "Cultura", "Food"};
        ArrayList<Integer> colours = getTagsColours();

        //Istanzia dinamicamente i bottoni per i tag e gli assegna il listener
        for (int i = 0;i<tags.length; i++) {
            ArcLayoutButton b = new ArcLayoutButton(getActivity().getApplicationContext());
            b.setButtonAttributes(tags[i], colours.get(i % colours.size()));
            b.setOnTouchListener(new tagButtonOnClick());
            if(listTags.contains(b.getText()))
                b.setPressed(true);
            arcLayout.addView(b);
        }

        //Setta il listener per il bottone di apertura dei tag
        view.findViewById(R.id.open_tags).setOnClickListener(new onOpenTagsListener(rootLayout, menuLayout, arcLayout, centerItem));

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

