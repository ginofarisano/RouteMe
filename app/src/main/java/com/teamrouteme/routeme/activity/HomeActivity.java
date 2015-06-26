package com.teamrouteme.routeme.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.parse.ParseUser;
import com.teamrouteme.routeme.R;
import com.teamrouteme.routeme.bean.Itinerario;
import com.teamrouteme.routeme.fragment.CercaItinerarioFragment;
import com.teamrouteme.routeme.fragment.ItinerariAcquistatiFragment;
import com.teamrouteme.routeme.fragment.ListaDesideriFragment;
import com.teamrouteme.routeme.fragment.CreaItinerarioFragment;
import com.teamrouteme.routeme.fragment.MieiItinerariFragment;
import com.teamrouteme.routeme.fragment.ProfiloFragment;
import com.teamrouteme.routeme.fragment.RisultatiRicercaFragment;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

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
    private MaterialSection cercaItinerarioSection;
    private MaterialSection toReturn;

    @Override
    public void init(Bundle bundle) {

        mDecorView = getWindow().getDecorView();

        currentUser = ParseUser.getCurrentUser();

        //is a facebook login
        if(currentUser.get("authData")!=null){
            HashMap<Object,Object> socialNetwork= ( HashMap<Object,Object>)currentUser.get("authData");

            if(socialNetwork.containsKey("facebook")){

                HashMap<Object,Object> fb= ( HashMap<Object,Object>)socialNetwork.get("facebook");

                String fbId= (String)fb.get("id");
                String access_token = (String) fb.get("access_token");


                getProfileImage(fbId,access_token);
            }

        }

        profilo = BitmapFactory.decodeResource(getResources(), R.drawable.com_parse_ui_app_logo);

        copertina = changeCopertinaAtRandom();



        account = new MaterialAccount(this.getResources(),ParseUser.getCurrentUser().getEmail(),currentUser.getString("name"),profilo,copertina);
        this.addAccount(account);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        this.addSection(newSection("Cerca Itinerario", R.drawable.search, new CercaItinerarioFragment()));
        this.addSection(newSection("Crea Itinerario", R.drawable.marker, new CreaItinerarioFragment()));
        this.addDivisor();
        this.addSection(newSection("Miei Itinerari", R.drawable.list, new MieiItinerariFragment()));
        this.addSection(newSection("Itinerari Acquistati", R.drawable.download_icon, new ItinerariAcquistatiFragment()));
        this.addSection(newSection("Lista Desideri", R.drawable.wishlist, new ListaDesideriFragment()));
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

        this.addSection(newSection("Offrimi una birra!", R.drawable.beer, new MaterialSectionListener() {
            @Override
            public void onClick(MaterialSection materialSection) {

                /**
                 *  X L'ACQUISTO DI CREDITI AGGIUNGERE QUESTO CODICE SENZA intent.putExtra(PayPalActivity.KEY_DONATE,"BEEEER"); CAMBIANDO 1 CON
                 *  IL NUMERO DI CREDITI DA ACQUISTARE
                 */

                Intent intent = new Intent(HomeActivity.this, PayPalActivity.class);


                //esempio pagamento un euro
                intent.putExtra(PayPalActivity.KEY_BUNDLE, 1);

                intent.putExtra(PayPalActivity.KEY_DONATE, "BEEEER");

                startActivity(intent);


                /**
                 *  X L'ACQUISTO DI CREDITI AGGIUNGERE QUESTO CODICE SENZA intent.putExtra(PayPalActivity.KEY_DONATE,"BEEEER"); CAMBIANDO 1 CON
                 *  IL NUMERO DI CREDITI DA ACQUISTARE
                 */


            }
        }));

    }

    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        HomeActivity.super.onBackPressed();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Sei sicuro di voler uscire dall'app?").setPositiveButton("Si", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
    }

    private Bitmap changeCopertinaAtRandom()  {

        InputStream bitmap=null;
        Bitmap bit =null;

        try {

            String[] n=getAssets().list("Copertina");

            Random random = new Random();

            int myRandom = random.nextInt(n.length) +1 ;

            bitmap=getAssets().open("Copertina/Copertina"+myRandom+".jpg");

            bit=BitmapFactory.decodeStream(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(bitmap!=null)
                try {
                    bitmap.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return bit;

    }

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


    public void getProfileImage(final String userFacebookId, final String access_token){

        new AsyncTask<Void, Void, Bitmap[]>()
        {
            @Override
            protected Bitmap[] doInBackground(Void... params)
            {

                Bitmap [] bitmaps = new Bitmap[2];
                // safety check
                if (userFacebookId == null)
                    return null;

                String urlProfile = String.format(
                        "https://graph.facebook.com/%s/picture?type=large",
                        userFacebookId);

                /*

                decomment this code to view cover's image

                String urlCover = String.format(
                        "https://graph.facebook.com/%s?fields=cover&access_token=%s",
                        userFacebookId,access_token);


                 */
                // you'll need to wrap the two method calls
                // which follow in try-catch-finally blocks
                // and remember to close your input stream

                InputStream inputStream = null;

                try {
                    inputStream = new URL(urlProfile).openStream();
                } catch (IOException e) {
                    Toast.makeText(HomeActivity.this, "Caricamento immagine del profilo non riuscito", Toast.LENGTH_SHORT).show();
                }


                bitmaps[0] = BitmapFactory.decodeStream(inputStream);


                try {
                    inputStream.close();
                } catch (IOException e) {
                    Toast.makeText(HomeActivity.this, "Caricamento immagine del profilo non riuscito", Toast.LENGTH_SHORT).show();
                }

                /*

                decomment this code for view cover's image

                try {
                    String urlFinalCover =  (String) ((JSONObject)((JSONObject)JsonReader.readJsonFromUrl(urlCover)).get("cover")).get("source");

                    try {
                        inputStream = new URL(urlFinalCover).openStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    bitmaps[1] = BitmapFactory.decodeStream(inputStream);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                */


                return bitmaps;
            }

            @Override
            protected void onPostExecute(Bitmap[] bitmaps)
            {
                // safety check
                if (bitmaps[0] != null
                        //decomment this code for view cover's image
                        //&& bitmaps[1] != nul
                        && !isChangingConfigurations()
                        && !isFinishing()){

                    profilo = bitmaps[0];


                    account.setPhoto(profilo);


                    notifyAccountDataChanged();


                    //account.setBackground(bitmaps[1]);

                }
                // do what you need to do with the bitmap :)
            }
        }.execute();


    }

}
