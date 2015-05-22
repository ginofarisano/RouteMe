package com.teamrouteme.routeme.fragment;

import android.app.DialogFragment;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.teamrouteme.routeme.R;

import java.util.ArrayList;

/**
 * Created by massimo299 on 15/05/15.
 */
public class ModificaCancellazioneDialog extends DialogFragment {

    private int markerPosition;

    public ModificaCancellazioneDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_modifica_cancellazione_dialog, container);

        Bundle b = getArguments();
        markerPosition = b.getInt("markerPosition");
        String[] operazioni = new String[] { "Modifica tappa", "Cancella tappa" };

        final ArrayList<String> alOperazioni = new ArrayList<String>();
        for (int i = 0; i < operazioni.length; ++i) {
            alOperazioni.add(operazioni[i]);
        }

        final ListView mylist = (ListView) view.findViewById(R.id.listView_modifica_cancellazione);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, alOperazioni);

        TextView title = new TextView(getActivity());
        title.setText(b.getString("nomeTappa"));

        mylist.addHeaderView(title);

        mylist.setAdapter(adapter);

        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adattatore, final View componente, int pos, long id) {
                if (pos == 0) {
                    Intent i = new Intent();
                    i.putExtra("markerPosition", markerPosition);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), getTargetRequestCode(), i);
                    getDialog().dismiss();
                } else {
                    Intent i = new Intent();
                    i.putExtra("markerPosition", markerPosition);
                    getTargetFragment().onActivityResult(getTargetRequestCode() + 1, getTargetRequestCode() + 1, i);
                    getDialog().dismiss();
                }
            }
        });

        return view;
    }

}