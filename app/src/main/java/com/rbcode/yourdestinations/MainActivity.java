package com.rbcode.yourdestinations;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.libraries.places.api.model.Place;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    String mode;
    String layout;
    //getPreferences(MODE_PRIVATE);
    SharedPreferences mPrefs,oPerfs;
    ArrayList<SavedPlace> arrayList = new ArrayList<SavedPlace>();

    ListViewAdapter adapter;

    public SharedPreferences getPref(){
        return mPrefs;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = getSharedPreferences("test",MODE_PRIVATE);
        oPerfs = getSharedPreferences("options",MODE_PRIVATE);

        layout = oPerfs.getString("layout","light");
        Log.d(TAG, "onCreate: "+layout);
        if(layout.equals("dark")) {
            setTheme(R.style.Theme_AppCompat_DayNight_NoActionBar);
            //theme.applyStyle(R.style.Theme_AppCompat_DayNight_NoActionBar, true);
            Log.d(TAG, "onCreate: in dark");
        }
        else {
            setTheme(R.style.Theme_AppCompat_Light_NoActionBar);

            //theme.applyStyle(R.style.Theme_AppCompat_Light_NoActionBar, true);
            Log.d(TAG, "onCreate: in light");
        }
        setContentView(R.layout.activity_main);

        //mPrefs = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        adapter = new ListViewAdapter(this, arrayList);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        final ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final SavedPlace savedPlace = (SavedPlace) listView.getItemAtPosition(position);

                if(mode.equals("lead")){
                    Uri uri = Uri.parse("google.navigation:q="+savedPlace.getLat()+","+savedPlace.getLng());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                }
                if(mode.equals("directions")) {
                    String uri = "http://maps.google.com/maps?daddr="+savedPlace.getLat()+","+savedPlace.getLng();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, int position, long id) {
                final SavedPlace savedPlace = (SavedPlace) listView.getItemAtPosition(position);
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);

                view = layoutInflater.inflate(R.layout.longclick_list,null);

                Button bt1 = view.findViewById(R.id.buttonLV);
                Button bt2 = view.findViewById(R.id.button1LV);

                final AlertDialog alertDialog = alert.create();

                bt1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, savedPlace.getNazwa(), Toast.LENGTH_SHORT).show();

                        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        final EditText input = new EditText(MainActivity.this);

                        alert.setTitle(R.string.edit_name);
                        alert.setView(input);

                        alert.setPositiveButton(R.string.zapisz, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                    JSONObject jplace = new JSONObject();
                                    try {
                                        jplace.put("id",savedPlace.getId() );
                                        jplace.put("nazwa", input.getText().toString());
                                        jplace.put("adres", savedPlace.getAdres());
                                        jplace.put("lat", savedPlace.getLat());
                                        jplace.put("lon", savedPlace.getLng());

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    SharedPreferences.Editor editor = mPrefs.edit();
                                    editor.putString(String.valueOf(savedPlace.getId()), jplace.toString()); //put new place

                                    editor.apply();
                                }
                        });

                        alert.setNegativeButton(R.string.wstecz, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {//Anulowane
                                 }
                        });
                        alert.show();

                        alertDialog.dismiss();
                    }

                });
                bt2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        remove_from_shared(savedPlace.getId());
                        alertDialog.dismiss();
                        //Toast.makeText(MainActivity.this, "Usun", Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.setView(view);

                alertDialog.show();

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this,Options.class);
        startActivity(intent);
        // startActivityforResult
        finish();
        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    public void remove_from_shared(int id){
        Log.d(TAG, "remove_from_shared: "+id);
        SharedPreferences.Editor editor = mPrefs.edit();
        //editor.putString(String.valueOf(id),"0"); //put new place
        editor.remove(String.valueOf(id));
        editor.apply();

        Toast.makeText(this, R.string.noti_removed, Toast.LENGTH_SHORT).show();
        populate_list();
    }


    public void add_destination(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);

        view = layoutInflater.inflate(R.layout.add_destination_layout,null);

        Button bt1 = view.findViewById(R.id.buttonLV);
        Button bt2 = view.findViewById(R.id.button1LV);
        Button bt3 = view.findViewById(R.id.button2LV);

        final AlertDialog alertDialog = alert.create();

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("mode","map");
                startActivity(intent);

                alertDialog.dismiss();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("mode","place");
                startActivity(intent);
                alertDialog.dismiss();
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("mode","park");
                startActivity(intent);
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(view);
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populate_list();
        mode = oPerfs.getString("mode","directions");
        String layout = oPerfs.getString("layout","light");
        if(layout.equals("dark"))
            setTheme(R.style.Theme_AppCompat_DayNight_NoActionBar);
        if(layout.equals("light"))
            setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
    }

    public void populate_list(){
        adapter.clear();
        String count = mPrefs.getString("count","0"); //0
        JSONObject array = new JSONObject();
        try {
            array = new JSONObject(mPrefs.getString("mode","error"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if(!(array.getString("mode")=="error"))
               adapter.add(new SavedPlace(array));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        for(int i=0;i<=Integer.valueOf(count);i++){
            Log.d(TAG, "populate_list: num:"+i+"|"+count);
            try {
                Log.d(TAG, "populate_list: try set array");
                array = new JSONObject(mPrefs.getString(String.valueOf(i),""));

                if(!array.getString("id").equals("0")&&!array.getString("id").equals("")
                        && !(array.getString("id") == null))
                    adapter.add(new SavedPlace(array));
                Log.d(TAG, "populate_list: got record");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String park = mPrefs.getString("park","none");
        if(park.equals("none"))
            return;
        else {
            try {
                array = new JSONObject(mPrefs.getString("park",""));
                adapter.add(new SavedPlace(array));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }




}
