package com.teamrouteme.routeme.bean;

import java.util.ArrayList;

/**
 * Created by massimo299 on 14/05/15.
 */
public class Itinerario {

    private ArrayList<Tappa> tappe = new ArrayList<Tappa>();

    private String nome;

    private ArrayList<String> tags;

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    private String descrizione;

    private int durataMin;

    public int getDurataMin() {
        return durataMin;
    }

    public void setDurataMin(int durataMin) {
        this.durataMin = durataMin;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public ArrayList<Tappa> getTappe() {
        return tappe;
    }

    public void setTappe(ArrayList<Tappa> tappe) {
        this.tappe = tappe;
    }

    private int durataMax;

    public int getDurataMax() {
        return durataMax;
    }

    public void setDurataMax(int durataMax) {
        this.durataMax = durataMax;
    }

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
