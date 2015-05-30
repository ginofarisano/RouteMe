package com.teamrouteme.routeme.fragment;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputType;
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
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.teamrouteme.routeme.R;
import com.teamrouteme.routeme.activity.HomeActivity;

import java.util.HashMap;
import java.util.List;

import it.neokree.materialnavigationdrawer.elements.MaterialAccount;


public class ModificaProfiloDialog extends DialogFragment {

    private final String TAG = "ModificaProfiloDialog";
    private String campoProfilo, tagCampoProfilo;
    private Button btnInviaCampoProfilo;
    private EditText editTextCampoProfilo;
    private String emailPattern;

    public ModificaProfiloDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_modifica_profilo_dialog, container);

        emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


        Bundle b = getArguments();
        tagCampoProfilo = b.getString("idCampoProfilo");
        campoProfilo = b.getString("campoProfilo");


        editTextCampoProfilo = (EditText) view.findViewById(R.id.editTextCampoProfilo);

        if(tagCampoProfilo.equals("password"))
            editTextCampoProfilo.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        else
            editTextCampoProfilo.setText(campoProfilo);

        btnInviaCampoProfilo = (Button) view.findViewById(R.id.btnInviaCampoProfiloDialog);

        btnInviaCampoProfilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nuovoCampoProfilo = editTextCampoProfilo.getText().toString();

                if(nuovoCampoProfilo.equals(campoProfilo))
                    backToProfiloFragment(2);
                else {

                    final ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                            "Caricamento in corso...", true);
                    ParseUser parseUser = ParseUser.getCurrentUser();

                    if (tagCampoProfilo.equals("nome")) {
                        parseUser.put("name", nuovoCampoProfilo);
                        parseUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    backToProfiloFragment(1);
                                    MaterialAccount myAccount = ((HomeActivity)getActivity()).getCurrentAccount();
                                    myAccount.setSubTitle(nuovoCampoProfilo);
                                    ((HomeActivity)getActivity()).notifyAccountDataChanged();
                                }
                                else{
                                    backToProfiloFragment(0);
                                    Log.d(TAG, "Error: " + e.getMessage());
                                }
                                dialog.hide();
                            }
                        });
                    }

                    if (tagCampoProfilo.equals("email")) {
                        if(tagCampoProfilo.matches(emailPattern)) {
                            parseUser.put("email", nuovoCampoProfilo);
                            parseUser.put("username", nuovoCampoProfilo);
                            parseUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        backToProfiloFragment(1);
                                        MaterialAccount myAccount = ((HomeActivity)getActivity()).getCurrentAccount();
                                        myAccount.setTitle(nuovoCampoProfilo);
                                        ((HomeActivity)getActivity()).notifyAccountDataChanged();
                                    }
                                    else {
                                        backToProfiloFragment(0);
                                        Log.d(TAG, "Error: " + e.getMessage());
                                    }
                                    dialog.hide();
                                }
                            });
                        }
                        else
                            Toast.makeText(getActivity().getBaseContext(), "Formato email non corretto", Toast.LENGTH_SHORT).show();
                    }

                    if (tagCampoProfilo.equals("password")) {
                        parseUser.put("password", nuovoCampoProfilo);
                        parseUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null)
                                    backToProfiloFragment(1);
                                else {
                                    backToProfiloFragment(0);
                                    Log.d(TAG, "Error: " + e.getMessage());
                                }
                                dialog.hide();
                            }
                        });
                    }

                }
            }
        });

        return view;
    }


    private void backToProfiloFragment(int valoreMod){
        Intent i = new Intent();
        i.putExtra("modificaEffettuata", valoreMod);
        getTargetFragment().onActivityResult(getTargetRequestCode(), getTargetRequestCode(), i);
        closeKeyboard(getActivity(), editTextCampoProfilo.getWindowToken());
        getDialog().dismiss();
    }


    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

}