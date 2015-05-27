package com.teamrouteme.routeme.fragment;


import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;


import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.teamrouteme.routeme.R;

import java.util.List;

public class FeedbackDialog extends DialogFragment {

    private String idItinerario;
    private Button btnInviaFeedback;
    private EditText editTextInvioFeedback;
    private RatingBar ratingBarInvioFeedback;
    private int queryCount;

    public FeedbackDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_feedback_dialog, container);

        Bundle b = getArguments();
        idItinerario = b.getString("idItinerario");

        editTextInvioFeedback = (EditText) view.findViewById(R.id.editTextInvioFeedback);
        ratingBarInvioFeedback = (RatingBar) view.findViewById(R.id.ratingBarInvioFeedback);

        btnInviaFeedback = (Button)view.findViewById(R.id.btnInviaFeedbackDialog);

        btnInviaFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final float rating = ratingBarInvioFeedback.getRating();
                final String recensione = editTextInvioFeedback.getText().toString();

                queryCount = 0;

                final ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                        "Caricamento in corso...", true);

                ParseQuery<ParseObject> query = ParseQuery.getQuery("itinerari_acquistati");

                query = query.whereEqualTo("idItinerario", idItinerario);

                query.whereEqualTo("user", ParseUser.getCurrentUser());

                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if (e == null) {
                            ParseObject itinerarioAcquistato = list.get(0);
                            itinerarioAcquistato.put("feedback", recensione);
                            itinerarioAcquistato.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        queryCount++;
                                        if (queryCount == 2) {
                                            dialog.hide();
                                            backToAnteprimaItinerariScaricatiFragment();
                                        }
                                    } else
                                        Log.d("FeedbackDialog", "Error: " + e.getMessage());
                                }
                            });
                        } else
                            Log.d("FeedbackDialog", "Error: " + e.getMessage());
                    }
                });


                ParseQuery<ParseObject> query2 = ParseQuery.getQuery("itinerario");

                query2.whereEqualTo("objectId", idItinerario);

                query2.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if(e==null) {
                            ParseObject itinerarioAcquistato = list.get(0);
                            itinerarioAcquistato.put("num_feedback", itinerarioAcquistato.getNumber("num_feedback").intValue()+1);
                            itinerarioAcquistato.put("rating", itinerarioAcquistato.getNumber("rating").floatValue()+rating);
                            itinerarioAcquistato.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e==null) {
                                        queryCount++;
                                        if(queryCount==2) {
                                            dialog.hide();
                                            backToAnteprimaItinerariScaricatiFragment();
                                        }
                                    }
                                    else
                                        Log.d("FeedbackDialog", "Error: " + e.getMessage());
                                }
                            });
                        }
                        else
                            Log.d("FeedbackDialog", "Error: " + e.getMessage());
                    }
                });
            }
        });

        return view;
    }

    private void backToAnteprimaItinerariScaricatiFragment(){
        getTargetFragment().onActivityResult(getTargetRequestCode(), getTargetRequestCode(), null);
        closeKeyboard(getActivity(), editTextInvioFeedback.getWindowToken());
        getDialog().dismiss();
    }

    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

}