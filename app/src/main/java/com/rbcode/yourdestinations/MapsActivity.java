package com.rbcode.yourdestinations;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.GetServiceRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;

import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.Provider;
import java.util.Arrays;
import java.util.List;

import static java.lang.Boolean.TRUE;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    int AUTOCOMPLETE_REQUEST_CODE = 1;
    private GoogleMap mMap;
    TextView SearchBar;
    public static boolean mLocationPermissionGranted = false;
    public static final int ERROR_DIALOG_REQUEST=9001;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS=9002;
    LatLng FromSearchBar;
    String mode = "map";
    String mapMode= "road";
    Location location;
    SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();

        mode = intent.getStringExtra("mode");
        mapMode = intent.getStringExtra("mapMpde");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        // mPrefs = MapsActivity.this.getPreferences(Context.MODE_PRIVATE);
        mPrefs = getSharedPreferences("test", MODE_PRIVATE);

        mapFragment.getMapAsync(this);
        Places.initialize(getApplicationContext(), getString(R.string.maps_api_key));
        SearchBar = findViewById(R.id.search_text);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.d(TAG, "onActivityResult: "+place.getLatLng());
                SearchBar.setText(place.getName());

                mMap.animateCamera(CameraUpdateFactory.
                        newCameraPosition(CameraPosition
                        .builder(mMap.getCameraPosition())
                        .target(place.getLatLng())
                        .zoom(15f)
                        .build()));


                Log.d(TAG, "onActivityResult: "+

                mMap.getCameraPosition().target.latitude+ " |"+
                        mMap.getCameraPosition().target.longitude

                );
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.d(TAG, "onActivityResult: " + status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) { Log.d(TAG, "onActivityResult: Canceled");}
        }
    }

    public void set_kamera(){
        switch (mode){
            case "map":{
                mMap.animateCamera(CameraUpdateFactory.
                        newCameraPosition(CameraPosition
                                .builder(mMap.getCameraPosition())
                                .target(new LatLng(52.237049, 19.057532))
                                .zoom(5.7f)
                                .build()));
                break;
            }case "place":{
                mMap.animateCamera(CameraUpdateFactory.
                        newCameraPosition(CameraPosition
                                .builder(mMap.getCameraPosition())
                                .target(new LatLng(location.getLatitude(),location.getLongitude()))
                                .zoom(16f)
                                .build()));
                break;
            }case "park":{
                double la,lo;
                la=location.getLatitude();
                lo=location.getLongitude();
                Log.d(TAG, "set_kamera: "+la +"|"+lo);

                mMap.animateCamera(CameraUpdateFactory.
                        newCameraPosition(CameraPosition
                                .builder(mMap.getCameraPosition())
                                .target(new LatLng(la,lo))
                                .zoom(18f)
                                .build()));
                break;
            }default:{
                Toast.makeText(this, "Error, mail it to dev(E01) "+mode, Toast.LENGTH_SHORT).show();
            }

        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "onMapReady: in map ready");

        mMap = googleMap;
        if(mapMode.equals("satelite"))
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if(mapMode.equals("road"))
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        location =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        Log.d(TAG, "onMapReady: in switch");
        set_kamera();






    }

    public void autoComplete(View view){
        List<Place.Field> fields = Arrays.asList(Place.Field.LAT_LNG,Place.Field.NAME);

        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .setCountry("PL") //restrict to Poland
                //.setTypeFilter(TypeFilter.GEOCODE)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    public void onSave(View view) {Geocoder geocoder = new Geocoder(getApplicationContext());
        List<Address> adresy = null;
        try {
            adresy = geocoder.getFromLocation(mMap.getCameraPosition().target.latitude,
                    mMap.getCameraPosition().target.longitude,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
//
        switch (mode){
            case "park":{
                Log.d(TAG, "onSave: "+mode);
                save_to_memory(adresy, getString(R.string.choice_park));
                break;
            }
            case "map":{
                Log.d(TAG, "onSave: "+mode);

                pop_up_name(adresy);
                break;
            }
            case "place":{
                Log.d(TAG, "onSave: "+mode);

                pop_up_name(adresy);
                break;
            }
            default:
                Log.d(TAG, "onSave: fail"+mode);
                break;
        }


        //  Log.d(TAG, "onSave: "+adresy.get(0).getThoroughfare());
//        Log.d(TAG, "onSave: "+adresy.get(0).getFeatureName());
//        Log.d(TAG, "onSave: "+adresy.get(0).getLocality());
//        Log.d(TAG, "onSave: "+adresy.get(0).getAddressLine(0));
//
        // pop_up_name(adresy);


    }

    public void pop_up_name(final List<Address> adresy){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);

        String q,w;
        try {
            q = adresy.get(0).getThoroughfare();
            w = adresy.get(0).getFeatureName();

            if (q == null)
                q = "";

            if (w == null)
                w = "";
            alert.setTitle(q+" "+w);



        //alert.setMessage();
        input.setHint(R.string.opisnazwa);
        alert.setView(input);

        alert.setPositiveButton(R.string.zapisz, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Log.d(TAG, "onClick: "+input.getText().toString());
                Log.d(TAG, "onClick: "+adresy.get(0).getThoroughfare());
                Log.d(TAG, "onClick: "+adresy.get(0).getFeatureName());
                Log.d(TAG, "onClick: "+adresy.get(0).getLocality());
                Log.d(TAG, "onClick: "+adresy.get(0).getAddressLine(0));
                save_to_memory(adresy,input.getText().toString());
            }
        });

        alert.setNegativeButton(R.string.wstecz, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Anulowane
            }

        });
        alert.show();
        }catch (Exception e){
            Log.d(TAG, "pop_up_name: "+e);
            Toast.makeText(MapsActivity.this, R.string.error_cord, Toast.LENGTH_SHORT).show();
        }
    }

    public void save_to_memory(List<Address> adresy, String nazwa) {
        String temp = "";
        String ulica = adresy.get(0).getThoroughfare();
        String numer = adresy.get(0).getFeatureName();
        String miasto = adresy.get(0).getLocality();
        if (ulica != null)
            temp += ulica + " ";
        if (numer != null)
            temp += numer + ", ";
        if (miasto != null)
            temp += miasto;

        String count = mPrefs.getString("count", "1");

        count = String.valueOf(Integer.valueOf(count) + 1);

        JSONObject jplace = new JSONObject();
        try {
            jplace.put("id", count);

            jplace.put("nazwa", nazwa);
            jplace.put("adres", temp);
            jplace.put("lat", adresy.get(0).getLatitude());
            jplace.put("lon", adresy.get(0).getLongitude());
            Log.d(TAG, "save_to_memory: " + jplace.toString());


        } catch (JSONException e) {
            Toast.makeText(this, R.string.error_save, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        if (nazwa.equals(getString(R.string.choice_park))) {
            SharedPreferences.Editor editor = mPrefs.edit();

            editor.putString("park", jplace.toString()); //put new place


            editor.apply();
        } else{

        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(count, jplace.toString()); //put new place

        editor.putString("count", String.valueOf(Integer.valueOf(count)));
        editor.apply();
    }

        finish();


    }

    public void on_return(View view) {
        finish();
    } //close activity


    @Override
    protected void onResume() {
        super.onResume();
        if (checkMapServices()) {
            if (mLocationPermissionGranted) {

            } else {
                getLocationPermission();
            }
        }
    }

    private boolean checkMapServices(){
        if(isServiceOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }
    private void buildAlertMessageNoGps(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Ta aplikacja wymaga dostępu do informacji o pozycji " +
                "Twojego telefonu\nChcesz włączyć GPS?")
                .setCancelable(true)
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,@SuppressWarnings("unused")final int id) {
                        Intent enableGpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent,PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MapsActivity.this, "Aktualnie możesz" +
                                " korzystać z aplikacji z gps", Toast.LENGTH_LONG).show();
                        }
                });
        final AlertDialog alert= builder.create();
        alert.show();
    }
    
    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission(){
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                ==PackageManager.PERMISSION_GRANTED){
            mLocationPermissionGranted=true;
        }else {
            Log.d(TAG, "getLocationPermission: 1");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServiceOK(){
        Log.d(TAG, "isServiceOK: chceck service version");
        int avaliable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapsActivity.this);

        if(avaliable == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServiceOK: Google play ok");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(avaliable)){
            Log.d(TAG, "isServiceOK: problem, ale do ogarniecia");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapsActivity.this, avaliable,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "Nie możesz przejść dalej\n" +
                    "Zaktualizuj mapy Google", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permission[],
                                           @NonNull int[] grantResults){

        mLocationPermissionGranted= false;
        switch (requestCode){
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    mLocationPermissionGranted=true;
                }
            }
        }
    }
}





