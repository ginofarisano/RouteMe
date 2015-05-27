package com.teamrouteme.routeme.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by massimo299 on 14/05/15.
 */
public class Itinerario implements Parcelable{

    private ArrayList<Tappa> tappe = new ArrayList<Tappa>();

    private String nome, id, citta, descrizione;

    private ArrayList<String> tags;

    private int durataMin, durataMax, num_feedback;

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    private float rating;

    public int getNum_feedback() {
        return num_feedback;
    }

    public void setNum_feedback(int num_feedback) {
        this.num_feedback = num_feedback;
    }

    private ArrayList<String> tappeId = new ArrayList<String>();

    public ArrayList<String> getTappeId() {
        return tappeId;
    }

    public void setTappeId(ArrayList<String> tappeId) {
        this.tappeId = tappeId;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

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

    public void aggiungiTappa(Tappa t){
        tappe.add(t);
    }

    public void rimuoviUltimaTappa(){
        tappe.remove(tappe.size()-1);
    }

    public void rimuoviTappa(Tappa t){
        tappe.remove(t);
    }

    public int getTappeSize(){
        return tappe.size();
    }

    public Tappa getTappa(int position){
        return tappe.get(position);
    }

    public Tappa rimuoviTappaInPosizione(int position){
        return tappe.remove(position);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
