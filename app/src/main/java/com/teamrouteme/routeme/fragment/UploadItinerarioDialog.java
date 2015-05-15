package com.teamrouteme.routeme.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teamrouteme.routeme.R;
import com.yahoo.mobile.client.android.util.RangeSeekBar;

/**
 * Created by massimo299 on 14/05/15.
 */
public class UploadItinerarioDialog extends DialogFragment{

    private EditText nomeItinerarioEditText, descrizioneItinerarioEditText, cittaItinerarioEditText, tagsItinerarioEditText;
    private TextView campiVuotiTextView;

    public UploadItinerarioDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_itinerario_dialog, container);

        cittaItinerarioEditText = (EditText) view.findViewById(R.id.editText_citta_itinerario);
        tagsItinerarioEditText = (EditText) view.findViewById(R.id.editText_tags_itinerario);
        nomeItinerarioEditText = (EditText) view.findViewById(R.id.editText_nome_itinerario);
        descrizioneItinerarioEditText = (EditText) view.findViewById(R.id.editText_descrizione_itinerario);
        campiVuotiTextView = (TextView) view.findViewById(R.id.lbl_campi_vuoti_itinerario);

        // Setup the new range seek bar
        RangeSeekBar<Integer> rangeSeekBar = new RangeSeekBar<Integer>(getActivity());
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
                    i.putExtra("nome_itinerario", nomeItinerarioEditText.getText().toString());
                    i.putExtra("descrizione_itinerario", descrizioneItinerarioEditText.getText().toString());
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
        if(nomeItinerarioEditText.getText().toString().equals("") || descrizioneItinerarioEditText.getText().toString().equals("")){
            return true;
        }
        return false;
    }
}
