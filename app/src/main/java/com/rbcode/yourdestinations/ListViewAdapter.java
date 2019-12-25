package com.rbcode.yourdestinations;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


import static android.content.ContentValues.TAG;

public class ListViewAdapter extends ArrayAdapter<SavedPlace> {
    protected ListViewAdapter(Context context, ArrayList<SavedPlace> places) {
        super(context,0,places);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SavedPlace savedPlace = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_place, parent, false);
        }

        TextView tvNazwa = convertView.findViewById(R.id.nazwa);
        TextView tvAdres = convertView.findViewById(R.id.adres);


        tvNazwa.setText(savedPlace.getNazwa());
        tvAdres.setText(savedPlace.getAdres());
        Log.d(TAG, "getView: "+ savedPlace.getAdres());
        return convertView;
    }
}
