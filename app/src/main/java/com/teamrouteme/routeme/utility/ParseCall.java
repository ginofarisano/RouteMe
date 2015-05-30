package com.teamrouteme.routeme.utility;

import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.teamrouteme.routeme.R;
import com.teamrouteme.routeme.bean.Itinerario;
import com.teamrouteme.routeme.bean.Tappa;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by ginofarisano on 15/05/15.
 */
public class ParseCall {

    private final String TAG = "ParseCallLog";

    ParseUser user;

    public ParseCall(){
        user= ParseUser.getCurrentUser();
    }

    public void uploadRoute(String citta, String[] tags, String nome, String descrizione, int min, int max, Itinerario itinerario, String autore) {

        final ParseObject toUploadItinerario = new ParseObject("itinerario");

        toUploadItinerario.put("user", user);

        toUploadItinerario.put("citta", citta);

        JSONArray jsonTags = new JSONArray();

        for(int i=0;i<tags.length;i++){
            jsonTags.put(tags[i]);
        }

        toUploadItinerario.put("tags",jsonTags);

        toUploadItinerario.put("nome",nome);
        toUploadItinerario.put("descrizione",descrizione);
        toUploadItinerario.put("durata_min",min);
        toUploadItinerario.put("durata_max",max);
        toUploadItinerario.put("rating", 0);
        toUploadItinerario.put("num_feedback", 0);
        toUploadItinerario.put("autore", autore);


        Tappa tappa;
        ParseObject tappaItinerario;
        ParseGeoPoint point;
        JSONArray jsonTappe = new JSONArray();

        for(int i=0;i<itinerario.getTappeSize();i++){

            tappa = itinerario.getTappa(i);

            tappaItinerario = new ParseObject("tappa");
            tappaItinerario.put("nome",tappa.getNome());
            tappaItinerario.put("descrizione",tappa.getDescrizione());
            point = new ParseGeoPoint(tappa.getMarker().getPosition().latitude, tappa.getMarker().getPosition().longitude);
            tappaItinerario.put("location", point);
            jsonTappe.put(tappaItinerario);
        }

        toUploadItinerario.put("tappe",jsonTappe);

        toUploadItinerario.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                }
                else{
                    toUploadItinerario.saveEventually();
                    Log.d(TAG, "Error: " + e.getMessage());
                    Log.d(TAG, "saveEventually");
                }
            }
        });


    }

    public void addWishList(String idItinerario) {

        final ParseObject toAddWishList = new ParseObject("lista_desideri");

        toAddWishList.put("user", user);

        toAddWishList.put("idItinerario", idItinerario);

        toAddWishList.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                }
                else{
                    toAddWishList.saveEventually();
                    Log.d(TAG, "Error: " + e.getMessage());
                    Log.d(TAG, "saveEventually");
                }
            }
        });



    }

    public void buyRoute(final String idItinerario, final ProgressDialog dialog, final Button btnAcquistaItinerario) {


        ParseObject toAddWishList = new ParseObject("itinerari_acquistati");

        toAddWishList.put("user", user);

        toAddWishList.put("idItinerario", idItinerario);

        toAddWishList.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {

                    if(btnAcquistaItinerario!=null){
                        //UNA VOLTA EFFETTUATA L'OPERAZIONE DI PAGAMENTO VENGONO DISATTIVATI I BOTTONI
                        btnAcquistaItinerario.setEnabled(false);
                        btnAcquistaItinerario.setText("Già tuo");
                    }


                    ParseQuery query = ParseQuery.getQuery("lista_desideri");

                    query = query.whereEqualTo("idItinerario", idItinerario);

                    query.findInBackground(new FindCallback<ParseObject>() {

                        @Override
                        public void done(List<ParseObject> list, com.parse.ParseException e) {

                            if (e == null) {
                                if (list.size() != 0) {
                                    list.get(0).deleteInBackground(new DeleteCallback() {
                                        public void done(ParseException e) {
                                            if (e == null) {

                                            } else {
                                                Log.d("ParseCall", "Error: " + e.getMessage());
                                            }
                                        }
                                    });
                                }
                            } else {
                                Log.d("AnteprimaItinerario", "Error: " + e.getMessage());
                            }

                            dialog.hide();

                        }

                    });
                } else {
                    Log.d("ParseCall", "Error: " + e.getMessage());
                }
            }
        });

    }

    public void buyRoute(final String idItinerario, final ProgressDialog dialog, final ParseObject listaDesideriObject) {

        ParseObject toAddBuyList = new ParseObject("itinerari_acquistati");

        toAddBuyList.put("user", user);

        toAddBuyList.put("idItinerario", idItinerario);

        toAddBuyList.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    if (listaDesideriObject != null)
                        listaDesideriObject.deleteInBackground(new DeleteCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    dialog.hide();
                                } else {
                                    Log.d("ParseCall", "Error: " + e.getMessage());
                                }
                            }
                        });
                    else
                        dialog.hide();
                } else {
                    Log.d("ParseCall", "Error: " + e.getMessage());
                }
            }
        });
    }

    public void scaleCredit(int delta) {

        user.put("crediti",delta);
        user.saveInBackground();

    }

    public void increasesCredit(int result, final String idItinerario, final ProgressDialog dialog, final Button btnAcquistaItinerario) {

        user.put("crediti",result);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                buyRoute(idItinerario,dialog,btnAcquistaItinerario);
            }
        });

    }
}