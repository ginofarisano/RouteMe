package com.teamrouteme.routeme.bean;

import com.google.android.gms.maps.model.Marker;
import com.parse.ParseGeoPoint;

/**
 * Created by massimo299 on 14/05/15.
 */
public class Tappa {
    private String nome, descrizione;

    private Marker marker;

    //da eliminare/aggiustare non riesco a creare un marker senza
    //una mappa (itinerari caricati) quindi mi salvo momentaneamente le coordinate poi si vede

    public ParseGeoPoint getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(ParseGeoPoint coordinate) {
        this.coordinate = coordinate;
    }

    private ParseGeoPoint coordinate;

    public Tappa (){
    }

    public Tappa (String n, String d, Marker m){
        nome = n;
        descrizione = d;
        marker = m;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}
