package com.teamrouteme.routeme.utility;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.teamrouteme.routeme.bean.Itinerario;
import com.teamrouteme.routeme.bean.Tappa;

import org.json.JSONArray;

/**
 * Created by ginofarisano on 15/05/15.
 */
public class ParseCall {

    ParseUser user;

    public ParseCall(){

        user= ParseUser.getCurrentUser();
    }


    public void saveDataToParse(String citta, String [] tags, String nome, String descrizione, int min, int max, Itinerario itinerario) {

        ParseObject toUploadItinerario = new ParseObject("itinerario");

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

        toUploadItinerario.saveInBackground();


    }


    public void addWishList(String idItinerario) {

        ParseObject toAddWishList = new ParseObject("lista_desideri");

        toAddWishList.put("user", user);

        toAddWishList.put("idItinerario", idItinerario);

        toAddWishList.saveInBackground();



    }

    public void buyRoute(String idItinerario) {

        ParseObject toAddWishList = new ParseObject("itinerari_acquistati");

        toAddWishList.put("user", user);

        toAddWishList.put("idItinerario", idItinerario);

        toAddWishList.saveInBackground();

    }
}