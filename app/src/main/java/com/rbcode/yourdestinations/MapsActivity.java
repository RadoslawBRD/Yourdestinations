package com.rbcode.yourdestinations;

import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
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
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    int AUTOCOMPLETE_REQUEST_CODE = 1;
    private GoogleMap mMap;
    TextView SearchBar;
    LatLng FromSearchBar;

    SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
       // mPrefs = MapsActivity.this.getPreferences(Context.MODE_PRIVATE);
        mPrefs = getSharedPreferences("test",MODE_PRIVATE);

        mapFragment.getMapAsync(this);
        Places.initialize(getApplicationContext(),getString(R.string.maps_api_key));
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



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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

    public void onSave(View view) {
        Geocoder geocoder = new Geocoder(getApplicationContext());
        List<Address> adresy = null;
        try {
            adresy = geocoder.getFromLocation(mMap.getCameraPosition().target.latitude,
                    mMap.getCameraPosition().target.longitude,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Log.d(TAG, "onSave: "+adresy.get(0).getThoroughfare());
//        Log.d(TAG, "onSave: "+adresy.get(0).getFeatureName());
//        Log.d(TAG, "onSave: "+adresy.get(0).getLocality());
//        Log.d(TAG, "onSave: "+adresy.get(0).getAddressLine(0));
//
        pop_up_name(adresy);


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

    public void save_to_memory(List<Address> adresy, String nazwa){
        String temp="";
        String ulica = adresy.get(0).getThoroughfare();
        String numer = adresy.get(0).getFeatureName();
        String miasto = adresy.get(0).getLocality();
        if(ulica!=null)
            temp +=ulica+" ";
        if(numer!=null)
            temp +=numer+", ";
        if(miasto!=null)
            temp +=miasto;

        String count = mPrefs.getString("count","1");

        count = String.valueOf(Integer.valueOf(count)+1);

        JSONObject jplace = new JSONObject();
        try{
            jplace.put("id",count);

            jplace.put("nazwa",nazwa);
            jplace.put("adres",temp);
            jplace.put("lat",adresy.get(0).getLatitude());
            jplace.put("lon",adresy.get(0).getLongitude());
            Log.d(TAG, "save_to_memory: "+jplace.toString());


        }catch (JSONException e){
            Toast.makeText(this, R.string.error_save, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(count,jplace.toString()); //put new place

        editor.putString("count",String.valueOf(Integer.valueOf(count)));

        editor.apply();
        finish();
    }

    public void on_return(View view) {
        finish();
    } //close activity

}
