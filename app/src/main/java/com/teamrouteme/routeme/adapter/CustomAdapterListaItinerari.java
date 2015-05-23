package com.teamrouteme.routeme.adapter;

/**
 * Created by ginofarisano on 16/05/15.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;


import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter;
import com.teamrouteme.routeme.R;
import com.teamrouteme.routeme.bean.Itinerario;

import java.util.List;

public class CustomAdapterListaItinerari extends ArrayAdapter<Itinerario> implements UndoAdapter {

    private final Context mContext;
    private List<Itinerario> itinerari;

    public CustomAdapterListaItinerari(Context context, int textViewResourceId,
                                        List<Itinerario> objects) {
        super(context, textViewResourceId, objects);
        mContext = context;
        itinerari= objects;

    }

    @Override
    public long getItemId(final int position) {
        return getItem(position).hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    /*
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return getViewOptimize(position, convertView, parent);
    }*/


    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.row_custom_itinerari, null);
        TextView nome = (TextView)convertView.findViewById(R.id.textViewName);
        TextView citta = (TextView)convertView.findViewById(R.id.textViewCitta);
        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
        Itinerario itinerario = getItem(position);
        nome.setText(itinerario.getNome());
        citta.setText(itinerario.getCitta());
        ratingBar.setRating(2.5f);
        return convertView;
    }

    /*
    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        LinearLayout view = (LinearLayout) convertView;
        if (view == null) {
            view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.row_custom_itinerari_creati, parent, false);
        }

        Itinerario itinerario = getItem(position);

        TextView nome = (TextView)view.findViewById(R.id.textViewName);
        TextView descrizione = (TextView)view.findViewById(R.id.textViewDescrizione);

        nome.setText(itinerario.getNome());
        descrizione.setText(itinerario.getTags() + " a " + itinerari.get(position).getCitta());

        return view;
    }
    */


    public View getViewOptimize(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_custom_itinerari_creati, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView)convertView.findViewById(R.id.textViewName);
            viewHolder.descrizione = (TextView)convertView.findViewById(R.id.textViewDescrizione);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Itinerario itinerario = getItem(position);
        viewHolder.name.setText(itinerario.getNome());
        viewHolder.descrizione.setText(itinerario.getTags() + " a " + itinerario.getCitta());
        return convertView;
    }

    public void remove(int position) {
        itinerari.remove(position);
    }

    @NonNull
    @Override
    public View getUndoView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.undo_row, parent, false);
        }
        return view;
    }

    @NonNull
    @Override
    public View getUndoClickView(@NonNull final View view) {
        return view.findViewById(R.id.undo_row_undobutton);
    }


    private class ViewHolder {
        public TextView name;
        public TextView descrizione;
    }
}