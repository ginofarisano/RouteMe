package com.teamrouteme.routeme.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.parse.ParseUser;
import com.teamrouteme.routeme.R;
import com.teamrouteme.routeme.fragment.CercaItinerarioFragment;
import com.teamrouteme.routeme.fragment.ItinerariScaricatiFragment;
import com.teamrouteme.routeme.fragment.ListaDesideriFragment;
import com.teamrouteme.routeme.fragment.CreaItinerarioFragment;
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
    private Bitmap profilo;
    private Bitmap copertina;
    private View mDecorView;

    @Override
    public void init(Bundle bundle) {

        mDecorView = getWindow().getDecorView();

        currentUser = ParseUser.getCurrentUser();

        //Caricare immagini copertina e profilo se loggati con facebook

        profilo = BitmapFactory.decodeResource(getResources(), R.drawable.routemelogo);
        copertina = BitmapFactory.decodeResource(getResources(), R.drawable.copertina);

        account = new MaterialAccount(this.getResources(),ParseUser.getCurrentUser().getEmail(),currentUser.getString("name"),profilo,copertina);
        this.addAccount(account);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        this.addSection(newSection("Cerca Itinerario", R.drawable.search, new CercaItinerarioFragment()));
        this.addSection(newSection("Crea Itinerario", R.drawable.marker, new CreaItinerarioFragment()));
        this.addDivisor();
        this.addSection(newSection("Miei Itinerari", R.drawable.list, new MieiItinerariFragment()));
        this.addSection(newSection("Itinerari Scaricati", R.drawable.download_icon, new ItinerariScaricatiFragment()));
        this.addSection(newSection("Lista Desideri", R.drawable.whishlist, new ListaDesideriFragment()));
        this.addDivisor();
        this.addSection(newSection("Profilo", R.drawable.profilo, new ProfiloFragment()));
        this.addDivisor();
        this.addSection(newSection("Logout", R.drawable.logout, new MaterialSectionListener() {
            @Override
            public void onClick(MaterialSection materialSection) {

                ParseUser.logOut();

                Intent intent = new Intent(HomeActivity.this, SplashActivity.class);

                startActivity(intent);

                HomeActivity.this.finish();
            }
        }));


        enableToolbarElevation();

    }

    /*public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
            mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }*/

    //Controlla se c'è connessione ad Internet
    public boolean isConnected(){
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null)
            return false;
        if (!i.isConnected())
            return false;
        if (!i.isAvailable())
            return false;
        return true;
    }
}
