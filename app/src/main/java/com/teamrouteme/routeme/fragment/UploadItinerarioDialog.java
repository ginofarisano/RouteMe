package com.teamrouteme.routeme.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ogaclejapan.arclayout.Arc;
import com.ogaclejapan.arclayout.ArcLayout;
import com.teamrouteme.routeme.R;
import com.teamrouteme.routeme.utility.AnimatorUtils;
import com.teamrouteme.routeme.utility.ArcLayoutButton;
import com.teamrouteme.routeme.widget.ClipRevealFrame;
import com.yahoo.mobile.client.android.util.RangeSeekBar;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by massimo299 on 14/05/15.
 */
public class UploadItinerarioDialog extends DialogFragment{
                                                                                                        //da cambiare con l'autocomplete di daniele
    private EditText nomeItinerarioEditText, descrizioneItinerarioEditText, cittaItinerarioEditText;
    RangeSeekBar<Integer> rangeSeekBar;

    private Toast toast = null;
    private TextView campiVuotiTextView;
    private View rootLayout;
    private ClipRevealFrame menuLayout;
    private ArcLayout arcLayout;
    private Button centerItem;
    private  ArrayList<String> listTags;

    public UploadItinerarioDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_itinerario_dialog, container);

        cittaItinerarioEditText = (EditText) view.findViewById(R.id.editText_citta_itinerario);
        nomeItinerarioEditText = (EditText) view.findViewById(R.id.editText_nome_itinerario);
        descrizioneItinerarioEditText = (EditText) view.findViewById(R.id.editText_descrizione_itinerario);
        campiVuotiTextView = (TextView) view.findViewById(R.id.lbl_campi_vuoti_itinerario);

        rootLayout = view.findViewById(R.id.layout_dialog_creazione_itinerario);
        menuLayout = (ClipRevealFrame) view.findViewById(R.id.menu_layout);
        arcLayout = (ArcLayout) view.findViewById(R.id.arc_layout);
        centerItem = (Button) view.findViewById(R.id.center_item);
        listTags = new ArrayList<>();


        String [] tags = {"Musica", "Fun", "Sport", "Cultura", "Food"};
        ArrayList<Integer> colours = getTagsColours();


        //setCenterButtonAttributes(centerItem, tags[0], colours.get(0));

        for (int i = 0;i<tags.length; i++) {
            ArcLayoutButton b = new ArcLayoutButton(getActivity().getApplicationContext());
            b.setButtonAttributes(tags[i], colours.get(i % colours.size()));
            arcLayout.addView(b);
        }

        for (int i = 0, size = arcLayout.getChildCount(); i < size; i++) {
            arcLayout.getChildAt(i).setOnTouchListener(new tagButtonOnClick());
        }

        view.findViewById(R.id.open_tags).setOnClickListener(new View.OnClickListener (){

            @Override
            public void onClick(View v) {
                onOpenTagsClick(v);
                return;

            }
        });

        // Setup the new range seek bar
        rangeSeekBar = new RangeSeekBar<Integer>(getActivity());
        // Set the range
        rangeSeekBar.setRangeValues(0, 10);
        rangeSeekBar.setSelectedMinValue(1);
        rangeSeekBar.setSelectedMaxValue(10);

        // Add to layout
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.seekbar_placeholder);
        layout.addView(rangeSeekBar);


        Button btn_confermaItinerario = (Button)view.findViewById(R.id.btn_conferma_itinerario);
        btn_confermaItinerario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEmpty()) {
                    Intent i = new Intent();
                    i.putExtra("citta_itinerario", cittaItinerarioEditText.getText().toString());;
                    i.putExtra("tags_itinerario", listTags.toArray(new String [listTags.size()]));
                    i.putExtra("nome_itinerario", nomeItinerarioEditText.getText().toString());
                    i.putExtra("descrizione_itinerario", descrizioneItinerarioEditText.getText().toString());
                    i.putExtra("range_min_itinerario", rangeSeekBar.getSelectedMinValue());
                    i.putExtra("range_max_itinerario", rangeSeekBar.getSelectedMaxValue());


                    getTargetFragment().onActivityResult(getTargetRequestCode(), getTargetRequestCode(), i);
                    closeKeyboard(getActivity(), nomeItinerarioEditText.getWindowToken());
                    closeKeyboard(getActivity(), descrizioneItinerarioEditText.getWindowToken());
                    getDialog().dismiss();
                } else
                    campiVuotiTextView.setVisibility(View.VISIBLE);
            }
        });



        return view;
    }

    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    private boolean isEmpty(){
        if(cittaItinerarioEditText.getText().toString().equals("") || nomeItinerarioEditText.getText().toString().equals("") || descrizioneItinerarioEditText.getText().toString().equals("")){
            return true;
        }
        if(listTags.size()==0)
            return true;
        return false;
    }

    private void onOpenTagsClick(View v) {
        int x = (v.getLeft() + v.getRight()) / 2;
        int y = (v.getTop() + v.getBottom()) / 2;
        float radiusOfFab = 1f * v.getWidth() / 2f;
        float radiusFromFabToRoot = (float) Math.hypot(
                Math.max(x, rootLayout.getWidth() - x),
                Math.max(y, rootLayout.getHeight() - y));

        if (v.isSelected()) {
            hideMenu(x, y, radiusFromFabToRoot, radiusOfFab);
        } else {
            showMenu(x, y, radiusOfFab, radiusFromFabToRoot);
        }
        v.setSelected(!v.isSelected());
    }

    private void showMenu(int cx, int cy, float startRadius, float endRadius) {
        menuLayout.setVisibility(View.VISIBLE);

        List<Animator> animList = new ArrayList<>();

        Animator revealAnim = createCircularReveal(menuLayout, cx, cy, startRadius, endRadius);
        revealAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        revealAnim.setDuration(200);

        animList.add(revealAnim);
        animList.add(createShowItemAnimator(centerItem));

        for (int i = 0, len = arcLayout.getChildCount(); i < len; i++) {
            animList.add(createShowItemAnimator(arcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.playSequentially(animList);
        animSet.start();
    }

    private void hideMenu(int cx, int cy, float startRadius, float endRadius) {
        List<Animator> animList = new ArrayList<>();

        for (int i = arcLayout.getChildCount() - 1; i >= 0; i--) {
            animList.add(createHideItemAnimator(arcLayout.getChildAt(i)));
        }

        animList.add(createHideItemAnimator(centerItem));

        Animator revealAnim = createCircularReveal(menuLayout, cx, cy, startRadius, endRadius);
        revealAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        revealAnim.setDuration(200);
        revealAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                menuLayout.setVisibility(View.INVISIBLE);
            }
        });

        animList.add(revealAnim);

        AnimatorSet animSet = new AnimatorSet();
        animSet.playSequentially(animList);
        animSet.start();

    }

    private Animator createShowItemAnimator(View item) {
        float dx = centerItem.getX() - item.getX();
        float dy = centerItem.getY() - item.getY();

        item.setScaleX(0f);
        item.setScaleY(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.scaleX(0f, 1f),
                AnimatorUtils.scaleY(0f, 1f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f)
        );

        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(50);
        return anim;
    }

    private Animator createHideItemAnimator(final View item) {
        final float dx = centerItem.getX() - item.getX();
        final float dy = centerItem.getY() - item.getY();

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.scaleX(1f, 0f),
                AnimatorUtils.scaleY(1f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy)
        );

        anim.setInterpolator(new DecelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });
        anim.setDuration(50);
        return anim;
    }

    private Animator createCircularReveal(final ClipRevealFrame view, int x, int y, float startRadius,
                                          float endRadius) {
        final Animator reveal;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            reveal = ViewAnimationUtils.createCircularReveal(view, x, y, startRadius, endRadius);
        } else {
            view.setClipOutLines(true);
            view.setClipCenter(x, y);
            reveal = ObjectAnimator.ofFloat(view, "ClipRadius", startRadius, endRadius);
            reveal.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setClipOutLines(false);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        return reveal;
    }

    private class tagButtonOnClick implements View.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN: v.setPressed(!v.isPressed());
                    if (v instanceof Button) {
                        String tagName = ((Button) v).getText().toString();
                        if(!listTags.contains(tagName)){
                            listTags.add(((Button) v).getText().toString());
                            showToastAdd((Button) v);
                        } else {
                            listTags.remove(tagName);
                            showToastRemove((Button) v);
                        }
                    }
                    break;
            }
            return  true;
        }
    }

    private void showToastAdd(Button btn) {
        if (toast != null) {
            toast.cancel();
        }

        String text = "Aggiunto: " + btn.getText();
        toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void showToastRemove(Button btn) {
        if (toast != null) {
            toast.cancel();
        }

        String text = "Rimosso: " + btn.getText();
        toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        toast.show();
    }


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

    private void setCenterButtonAttributes(Button b, String text, int color){
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(90, 90);
        b.setLayoutParams(params);
        b.setTextSize(15);
        b.setTextColor(getResources().getColor(R.color.tumblr_primary));
        b.setBackgroundResource(color);
        b.setText(text);
    }
}
