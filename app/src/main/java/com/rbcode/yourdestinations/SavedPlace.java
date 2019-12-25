package com.rbcode.yourdestinations;

import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class SavedPlace {
    private int id;
    private double lat;
    private double lng;
    private String nazwa;
    private String adres;

    protected SavedPlace(JSONObject object){
        //Log.d(TAG, "SavedPlace: json="+object);

        try{
        this.id = Integer.valueOf(object.getInt("id"));
        this.lat = Double.valueOf(object.getString("lat"));
        this.lng = Double.valueOf(object.getString("lon"));
        this.nazwa = object.getString("nazwa");
        this.adres = object.getString("adres");
            Log.d(TAG, "SavedPlace: created " + nazwa);
        } catch (JSONException e) {
            Log.d(TAG, "SavedPlace: error"+e.getMessage());
            e.printStackTrace();
        }
    }

    double getLat() {
        if(String.valueOf(lat) == null || String.valueOf(lat) == ""){
            return 0;
        }
        else
            return lat;
    }

    double getLng() {
        if(String.valueOf(lng) == null || String.valueOf(lng) == ""){
            return 0;
        }
        else
            return lng;
    }


    public static ArrayList<SavedPlace> fromJson(JSONArray jsonArray){
        ArrayList<SavedPlace> places = new ArrayList<SavedPlace>();
        for(int i=0;i<=jsonArray.length();i++) {
            try {
                places.add(new SavedPlace(jsonArray.getJSONObject(i)));
            } catch (JSONException e){
                e.printStackTrace();
        }
        }
        return places;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getNazwa() {
        if(nazwa == null || nazwa == ""){
            return "Nie zapisałeś żadnych adresów";
        }
        else
            return nazwa;
    }

    int getId(){
        if(String.valueOf(id) == null || id == 0){
            return 0;
        }
        else
            return id;
    }

    String getAdres() {
        if(adres == null || adres == ""){
            return "";
        }
        else
            return adres;
    }
}
