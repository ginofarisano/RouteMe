package com.teamrouteme.routeme.bean;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by massimo299 on 14/05/15.
 */
public class Tappa {
    private String nome, descrizione;
    private Marker marker;

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
