package com.teamrouteme.routeme.bean;

import java.util.ArrayList;

/**
 * Created by massimo299 on 14/05/15.
 */
public class Itinerario {
    private ArrayList<Tappa> tappe = new ArrayList<Tappa>();

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    private String citta;

    public void aggiungiTappa(Tappa t){
        tappe.add(t);
    }

    public void rimuoviUltimaTappa(){
        tappe.remove(tappe.size()-1);
    }

    public void rimuoviTappa(Tappa t){
        tappe.remove(t);
    }

    public int getSize(){
        return tappe.size();
    }

    public Tappa getTappa(int position){
        return tappe.get(position);
    }

    public Tappa rimuoviTappaInPosizione(int position){
        return tappe.remove(position);
    }
}
