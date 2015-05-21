package com.teamrouteme.routeme.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.ParseUser;
import com.teamrouteme.routeme.R;
import com.teamrouteme.routeme.bean.Itinerario;
import com.teamrouteme.routeme.bean.Tappa;
import com.teamrouteme.routeme.utility.DirectionsJSONParser;
import com.teamrouteme.routeme.utility.GeocodeJSONParser;
import com.teamrouteme.routeme.utility.ParseCall;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsFragment extends Fragment implements
        LocationListener{

    private final String TAG = "MapsTestLog";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private static final LatLng ITALY = new LatLng(42.5, 12.5);
    private LocationManager mLocationManager;
    private String provider;
    private boolean locationUpdatesRequested, writing;
    private AutoCompleteTextView etPlace;
    private ArrayAdapter<String> foundPlaces;
    private ArrayList<Marker> addedPlaces = new ArrayList<Marker>();
    private static View view;
    private Button btnConferma, btnAnnulla, btnFatto;
    private boolean it=false;
    private Itinerario itinerario = new Itinerario();
    private boolean canModificaCancellazione = true;

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_maps, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }

        if(Locale.getDefault().getDisplayLanguage().equalsIgnoreCase("italiano")){
            it = true;
        }


        setUpMapOnItaly();

        if(addedPlaces.size()>0){
            Marker last = addedPlaces.get(addedPlaces.size() - 1);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(last.getPosition(), 15);
            mMap.animateCamera(cameraUpdate);
        }

        //Getting reference to Buttons and setting Listener
        btnConferma = (Button) view.findViewById(R.id.btn_conferma);

        btnConferma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInserimentoTappaDialog();
            }
        });

        btnAnnulla = (Button) view.findViewById(R.id.btn_annulla);

        btnAnnulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Marker toRemove = addedPlaces.remove(addedPlaces.size()-1);
                toRemove.remove();
                btnConferma.setVisibility(View.INVISIBLE);
                btnAnnulla.setVisibility(View.INVISIBLE);
                etPlace.setText("");
                canModificaCancellazione = true;
                if(addedPlaces.size()>0)
                    btnFatto.setVisibility(View.VISIBLE);
            }
        });

        btnFatto = (Button) view.findViewById(R.id.btn_fatto);

        btnFatto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreazioneItinerarioDialog();
            }
        });

        // Getting reference to EditText and setting Listener
        etPlace = (AutoCompleteTextView) view.findViewById(R.id.et_place);

        // Setting minimum number of characters to start searching
        etPlace.setThreshold(5);

        etPlace.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence location, int start, int before, int count) {
                if (etPlace.enoughToFilter()) {
                    writing = true;
                    if (etPlace.isPerformingCompletion()) {
                        // An item has been selected from the list. Ignore.
                        return;
                    }

                    if (location == null || location.equals("")) {
                        Toast.makeText(getActivity().getBaseContext(), "Nessun indirizzo inserito", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    startDownloadTask(location.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPlace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
                writing = false;
                String location = (String) adapter.getItemAtPosition(pos);
                startDownloadTask(location);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void startDownloadTask(String location) {

        String url = "https://maps.googleapis.com/maps/api/geocode/json?";

        try {
            // encoding special characters like space in the user input place
            location = URLEncoder.encode(location, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String address = "address=" + location;

        String sensor = "sensor=false";

        String language = "";
        if(it)
            language = "language=it&";
        // url , from where the geocoding data is fetched
        url = url+language + address + "&" + sensor;

        // Instantiating DownloadTaskSearch to get places from Google Geocoding service
        // in a non-ui thread
        DownloadTaskSearch downloadTaskSearch = new DownloadTaskSearch();

        // Start downloading the geocoding places
        downloadTaskSearch.execute(url);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setUpMapOnItaly(){
        if (mMap == null)
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) ((FragmentActivity)getActivity()).getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        if (mMap != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ITALY, 5);
            mMap.animateCamera(cameraUpdate);
            setMapListener();
        }
        else
            Log.e(TAG, "setUpMapOnItaly: mMAp is null");
    }

    //Aggiunge alla mappa il listener che permette di effettuare un tap lungo sui marker
    private void setMapListener() {
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {
                if(canModificaCancellazione) {
                    for (int i = 0; i < addedPlaces.size(); i++) {
                        Marker marker = addedPlaces.get(i);
                        if (Math.abs(marker.getPosition().latitude - latLng.latitude) < 0.0009 && Math.abs(marker.getPosition().longitude - latLng.longitude) < 0.0009) {
                            showModificaCancellazioneDialog(i, itinerario.getTappa(i).getNome());
                            Log.d(TAG, "Trovato marker tappa "+itinerario.getTappa(i).getNome());
                            break;
                        }
                    }
                }

            }
        });
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception download url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /** A class, to download Places from Geocoding webservice */
    private class DownloadTaskSearch extends AsyncTask<String, Integer, String>{

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result){

            // Instantiating ParserTaskSearch which parses the json data from Geocoding webservice
            // in a non-ui thread
            ParserTaskSearch parserTaskSearch = new ParserTaskSearch();

            // Start parsing the places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTaskSearch.execute(result);
        }
    }

    /** A class to parse the Geocoding Places in non-ui thread */
    private class ParserTaskSearch extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String,String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            GeocodeJSONParser parser = new GeocodeJSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);

                /** Getting the parsed data as a an ArrayList */
                places = parser.parse(jObject);

            }catch(Exception e){
                Log.d("Excep ParserTaskSearch", e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String,String>> list){

            int size = list.size();

            if(writing) {
                if (size > 0) {

                    String[] tmpPlacesList = new String[size];
                    Log.d(TAG, "List size: " + size);
                    for (int i = 0; i < size; i++) {
                        tmpPlacesList[i] = list.get(i).get("formatted_address");
                        Log.d(TAG, "Indirizzo " + i + ": " + list.get(i).get("formatted_address"));
                    }

                    foundPlaces = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_dropdown_item_1line,
                            tmpPlacesList
                    );
                    Log.d(TAG, "FoundPlaces count: " + foundPlaces.getCount());
                    etPlace.setAdapter(foundPlaces);
                    etPlace.showDropDown();
                }
            }
            else{
                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(0);

                // Creating a marker
                MarkerOptions markerOptions = createMarkerHmPlace(hmPlace);

                // Adding the position to the addedPlaces list, if not present
                if(placeAlreadyPresent(markerOptions.getPosition()))
                    Toast.makeText(getActivity().getBaseContext(), "Tappa già presente", Toast.LENGTH_SHORT).show();
                else {
                    // Placing a marker on the touched position
                    Marker tmp = mMap.addMarker(markerOptions);

                    addedPlaces.add(tmp);
                    Log.d(TAG, "AddedPlaces " + addedPlaces.toString());
                    // Locate the location
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), 15));

                    canModificaCancellazione = false;
                    closeKeyboard(getActivity(), etPlace.getWindowToken());
                    btnConferma.setVisibility(View.VISIBLE);
                    btnAnnulla.setVisibility(View.VISIBLE);
                    btnFatto.setVisibility(View.INVISIBLE);

                }

            }
        }

    }

    //Controlla se un marker è già presente nella posizione passata come parametro
    private boolean placeAlreadyPresent(LatLng position) {
        for(int i=0;i<addedPlaces.size();i++)
            if(addedPlaces.get(i).getPosition().equals(position))
                return true;
        return false;
    }

    //Gestisce ciò che restituiscono i Dialog
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent i){
        if(resultCode==1) {
            // Inserimento tappa
            String nomeTappa = i.getStringExtra("nome_tappa");
            Tappa t = new Tappa(i.getStringExtra("nome_tappa"), i.getStringExtra("descrizione_tappa"), addedPlaces.get(addedPlaces.size() - 1));
            itinerario.aggiungiTappa(t);
            Log.d(TAG,"Itinerario contiene "+itinerario.getSize()+" tappe");
            btnConferma.setVisibility(View.INVISIBLE);
            btnAnnulla.setVisibility(View.INVISIBLE);
            etPlace.setText("");
            canModificaCancellazione = true;
            Toast.makeText(getActivity().getBaseContext(), "Aggiunta tappa " + nomeTappa, Toast.LENGTH_SHORT).show();

            if (itinerario.getSize() > 0)
                btnFatto.setVisibility(View.VISIBLE);
        }
        else if(resultCode==2){
            // Upload itinerario

            ParseCall parseCall =new ParseCall();

            String citta = i.getStringExtra("citta_itinerario");
            String nome = i.getStringExtra("nome_itinerario");
            Log.d(TAG,"Nome itinerario creato: "+ nome);
            String [] tags = i.getStringArrayExtra("tags_itinerario");
            String descrizione =i.getStringExtra("descrizione_itinerario");
            int min=i.getIntExtra("range_min_itinerario",-1);
            int max=i.getIntExtra("range_max_itinerario", -1);

                                                            //daniele sostituisci il tuo array invece di quello statico
            parseCall.saveDataToParse(citta,tags,nome,descrizione,min,max,itinerario);



            Toast.makeText(getActivity().getBaseContext(), "Itinerario creato", Toast.LENGTH_SHORT).show();
            //Call it when all is saved on the db
            resetMapsFragment();
        }
        else if(resultCode==3){
            // Cliccato modifica tappa
            int markerPosition = i.getIntExtra("markerPosition", - 1);
            Tappa t = itinerario.getTappa(markerPosition);
            showInserimentoTappaDialog(t.getNome(),t.getDescrizione(), markerPosition);
        }
        else if(resultCode==4){
            // Cliccato elimina tappa
            int markerPosition = i.getIntExtra("markerPosition", -1);
            addedPlaces.remove(markerPosition).remove();
            Tappa t = itinerario.rimuoviTappaInPosizione(markerPosition);
            Toast.makeText(getActivity().getBaseContext(), "Eliminata tappa " + t.getNome(), Toast.LENGTH_SHORT).show();
            if(addedPlaces.size()==0)
                btnFatto.setVisibility(View.INVISIBLE);
        }
        else if(resultCode==5){
            // Modifica tappa completata
            String nomeTappa = i.getStringExtra("nome_tappa");
            Tappa t = itinerario.getTappa(i.getIntExtra("markerPosition", -1));
            t.setNome(i.getStringExtra("nome_tappa"));
            t.setDescrizione(i.getStringExtra("descrizione_tappa"));
            btnConferma.setVisibility(View.INVISIBLE);
            btnAnnulla.setVisibility(View.INVISIBLE);
            etPlace.setText("");
            canModificaCancellazione = true;
            Toast.makeText(getActivity().getBaseContext(), "Modificata tappa " + nomeTappa, Toast.LENGTH_SHORT).show();

            if (itinerario.getSize() > 0)
                btnFatto.setVisibility(View.VISIBLE);
        }
    }

    private void showInserimentoTappaDialog() {
        FragmentManager fm = getFragmentManager();
        final InserimentoTappaDialog inserimentoTappaDialog = new InserimentoTappaDialog();
        inserimentoTappaDialog.show(fm, "fragment_inserimento_tappa_dialog");
        inserimentoTappaDialog.setTargetFragment(this, 1);
    }

    private void showInserimentoTappaDialog(String nomeTappa, String descrizioneTappa, int markerPosition) {
        FragmentManager fm = getFragmentManager();
        final InserimentoTappaDialog inserimentoTappaDialog = new InserimentoTappaDialog();
        Bundle b = new Bundle();
        b.putString("nomeTappa", nomeTappa);
        b.putString("descrizioneTappa", descrizioneTappa);
        b.putInt("markerPosition", markerPosition);
        inserimentoTappaDialog.setArguments(b);
        inserimentoTappaDialog.show(fm, "fragment_inserimento_tappa_dialog");
        inserimentoTappaDialog.setTargetFragment(this, 1);
    }

    private void showCreazioneItinerarioDialog() {
        FragmentManager fm = getFragmentManager();
        final UploadItinerarioDialog uploadItinerarioDialog = new UploadItinerarioDialog();
        uploadItinerarioDialog.show(fm, "fragment_upload_itinerario_dialog");
        uploadItinerarioDialog.setTargetFragment(this, 2);
    }

    private void showModificaCancellazioneDialog(int markerPosition, String nomeTappa) {
        FragmentManager fm = getFragmentManager();
        final ModificaCancellazioneDialog modificaCancellazioneDialog = new ModificaCancellazioneDialog();
        Bundle b = new Bundle();
        b.putInt("markerPosition", markerPosition);
        b.putString("nomeTappa", nomeTappa);
        modificaCancellazioneDialog.setArguments(b);
        modificaCancellazioneDialog.show(fm, "fragment_modifica_cancellazione_dialog");
        modificaCancellazioneDialog.setTargetFragment(this, 3);
    }

    private void resetMapsFragment() {
        itinerario = new Itinerario();
        for(int i =0;i<addedPlaces.size();i++)
            addedPlaces.get(i).remove();
        addedPlaces = new ArrayList<Marker>();
        btnFatto.setVisibility(View.INVISIBLE);
        canModificaCancellazione = false;
        setUpMapOnItaly();
    }

    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    private MarkerOptions createMarkerHmPlace(HashMap<String, String> hmPlace) {
        MarkerOptions markerOptions = new MarkerOptions();

        // Getting latitude of the place
        double lat = Double.parseDouble(hmPlace.get("lat"));

        // Getting longitude of the place
        double lng = Double.parseDouble(hmPlace.get("lng"));

        // Getting name
        String name = hmPlace.get("formatted_address");

        LatLng latLng = new LatLng(lat, lng);

        // Setting the position for the marker
        markerOptions.position(latLng);

        // Setting the title for the marker
        markerOptions.title(name);

        return markerOptions;
    }





    // Metodo per posizionare la mappa sulla posizione attuale, se possibile, altrimenti la posiziona sull'Italia
    private void setUpMapOnLastKnownLocation(boolean locationUpdatesRequested){
        this.locationUpdatesRequested = locationUpdatesRequested;

        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity().getBaseContext());

        // Showing status
        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getActivity(), requestCode);
            dialog.show();

        }
        else {
            if (mMap == null)
                // Try to obtain the map from the SupportMapFragment.
                mMap = ((SupportMapFragment) ((FragmentActivity)getActivity()).getSupportFragmentManager().findFragmentById(R.id.map))
                        .getMap();
            if (mMap != null) {
                // Enabling MyLocation Layer of Google Map (the blu dot for the location of the smartphone)
                mMap.setMyLocationEnabled(true);

                // Getting Current Location and setting the location updates if requested
                Location location = getLastKnownLocation();

                if (location != null) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15);
                    mMap.animateCamera(cameraUpdate);
                }
                else {
                    Log.d(TAG, "Last know location is null, map setted on Italy");
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ITALY, 5);
                    mMap.animateCamera(cameraUpdate);
                }
            }

        }
    }

    //Metodo che recupera la posiziona attuale, se disponibile
    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager)getActivity().getApplicationContext().getSystemService(getActivity().LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                this.provider = provider;
                bestLocation = l;
            }
        }

        Log.d(TAG, "BEST PROVIDER " + provider);

        if(locationUpdatesRequested && provider != null)
            mLocationManager.requestLocationUpdates(provider,5000,0,this);
        return bestLocation;
    }

    // Callback chiamata ogni tot ms (5000 come impsotato nel metodo sopra), che serve per aggiornare la posizione attuale in automatico
    @Override
    public void onLocationChanged(Location location) {

        Log.d(TAG, "OnLocationChanged called");

        location = getLastKnownLocation();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15);
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }





    //Metodo per tracciare il percorso sulla mappa da un punto A ad un punto B passati come parametro
    private void drawFromAtoB(LatLng a, LatLng b){
        if (addedPlaces.size() >= 2) {
            LatLng origin = addedPlaces.get(addedPlaces.size() - 2).getPosition();
            LatLng dest = addedPlaces.get(addedPlaces.size() - 1).getPosition();

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            DownloadTaskDirections downloadTask = new DownloadTaskDirections();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }
    }

    //Date due posizioni come parametro, origin e dest, crea l'url per tracciare il percorso tra i 2, a piedi
    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Travel mode type
        String mode = "mode=walking";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+mode+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    /** A class to download data from Google Directions URL */
    private class DownloadTaskDirections extends AsyncTask<String, Void, String>{

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTaskDirections parserTask = new ParserTaskDirections();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Directions in JSON format */
    private class ParserTaskDirections extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(4);
                lineOptions.color(Color.BLUE);
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }
}
