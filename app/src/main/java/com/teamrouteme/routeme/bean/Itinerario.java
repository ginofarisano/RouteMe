package com.teamrouteme.routeme.bean;

import java.util.ArrayList;

/**
 * Created by massimo299 on 14/05/15.
 */
public class Itinerario {
    private ArrayList<Tappa> tappe = new ArrayList<Tappa>();

    public void aggiungiTappa(Tappa t){
        tappe.add(t);
    }

    public void rimuoviUltimaTappa(){
        tappe.remove(tappe.size()-1);
    }

    public void rimuoviTappa(Tappa t){
        tappe.remove(t);
    }
}
