package com.teamrouteme.routeme.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;

import com.teamrouteme.routeme.activity.PayPalActivity;
import com.teamrouteme.routeme.utility.ParseCall;

/**
 * Created by ginofarisano on 29/05/15.
 * menage the data getted for paypall
 * the same request is by the same for AnteprimaListaDesideriFragment
 *
 */
public class BaseFragmentPayPalResult extends Fragment {

    static final int PAYPAL_REQUEST = 2;  // The request code
    ProgressDialog dialog;

    //credit to add at credit purchased
    private int delta=0;
    private String idItinerario;
    private Button btnAcquistaItinerario;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYPAL_REQUEST) {
            if (resultCode == getActivity().RESULT_OK) {

                int result=data.getIntExtra(PayPalActivity.INCREASE_CREDIT,0);

                ParseCall parseCall = new ParseCall(getActivity());
                parseCall.increasesCredit((result + delta), idItinerario, dialog,btnAcquistaItinerario);

            }

            if (resultCode == getActivity().RESULT_CANCELED) {
                dialog.hide();
            }

            onDestroy();



        }
    }

    public void buyCredit(int delta, String idItinerario, ProgressDialog dialog, Button btnAcquistaItinerario) {

        this.delta = delta;

        this.idItinerario = idItinerario;

        this.btnAcquistaItinerario = btnAcquistaItinerario;

        Toast.makeText(getActivity().getBaseContext(), "Non hai abbastanza crediti (" + delta + ") per concludere l'acquisto!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), PayPalActivity.class);


        intent.putExtra(PayPalActivity.KEY_BUNDLE, -delta);

        startActivityForResult(intent, PAYPAL_REQUEST);

    }

}
