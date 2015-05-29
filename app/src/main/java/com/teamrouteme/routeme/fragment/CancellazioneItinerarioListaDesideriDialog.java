package com.teamrouteme.routeme.fragment;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.teamrouteme.routeme.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by massimo299 on 29/05/15.
 */
public class CancellazioneItinerarioListaDesideriDialog extends DialogFragment {

    private String idItinerario, nomeItinerario;
    private int cardItemNumber;
    private TextView cancellazioneListaDesideri;
    private Button btnCancellazioneListaDesideriDialog;

    public CancellazioneItinerarioListaDesideriDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_cancellazione_lista_desideri_dialog, container);

        cancellazioneListaDesideri = (TextView) view.findViewById(R.id.lbl_cancellazione_lista_desideri);
        btnCancellazioneListaDesideriDialog = (Button) view.findViewById(R.id.btnInviaCancellazioneListaDesideriDialog);

        Bundle b = getArguments();
        idItinerario = b.getString("idItinerario");
        nomeItinerario = b.getString("nomeItinerario");
        cardItemNumber = b.getInt("cardItemNumber");

        cancellazioneListaDesideri.setText("Eliminare '"+nomeItinerario+"' dalla lista dei desideri?");

        btnCancellazioneListaDesideriDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra("idItinerario", idItinerario);
                i.putExtra("cardItemNumber", cardItemNumber);
                getTargetFragment().onActivityResult(getTargetRequestCode(), getTargetRequestCode(), i);
                getDialog().dismiss();
            }
        });

        return view;
    }
}
