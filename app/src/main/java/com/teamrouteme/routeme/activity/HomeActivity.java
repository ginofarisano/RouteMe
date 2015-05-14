package com.teamrouteme.routeme.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;
import com.teamrouteme.routeme.R;
import com.teamrouteme.routeme.fragment.CercaItinerarioFragment;
import com.teamrouteme.routeme.fragment.ItinerariScaricatiFragment;
import com.teamrouteme.routeme.fragment.ListaDesideriFragment;
import com.teamrouteme.routeme.fragment.MapsFragment;
import com.teamrouteme.routeme.fragment.MieiItinerariFragment;
import com.teamrouteme.routeme.fragment.ProfiloFragment;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialSectionListener;


public class HomeActivity extends MaterialNavigationDrawer {

    private static final int LOGIN_REQUEST = 0;

    MaterialAccount account;
    private ParseUser currentUser;

    @Override
    public void init(Bundle bundle) {

        currentUser = ParseUser.getCurrentUser();

        account = new MaterialAccount(this.getResources(),ParseUser.getCurrentUser().getEmail(),currentUser.getString("name"),null,null);
        this.addAccount(account);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        this.addSection(newSection("Cerca Itinerario", new CercaItinerarioFragment()));
        this.addSection(newSection("Crea Itinerario", new MapsFragment()));
        this.addSection(newSection("Miei Itinerari", new MieiItinerariFragment()));
        this.addSection(newSection("Itinerari Scaricati", new ItinerariScaricatiFragment()));
        this.addSection(newSection("Lista Desideri", new ListaDesideriFragment()));
        this.addSection(newSection("Profilo", new ProfiloFragment()));
        this.addSection(newSection("Logout",new MaterialSectionListener() {
            @Override
            public void onClick(MaterialSection materialSection) {

                ParseUser.logOut();

                Intent intent = new Intent(HomeActivity.this,SplashActivity.class);

                startActivity(intent);

                HomeActivity.this.finish();

            }
        }));


        enableToolbarElevation();

    }

}
