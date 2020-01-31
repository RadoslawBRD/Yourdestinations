package com.rbcode.yourdestinations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class Options extends AppCompatActivity {

    private static final String TAG = "Options";
    Switch sLayout,sMode;
    SharedPreferences mPrefs,oPerfs;
    SharedPreferences.Editor editor;
    String layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        oPerfs = getSharedPreferences("options",MODE_PRIVATE);
        layout = oPerfs.getString("layout","light");
        Log.d(TAG, "onCreate: "+layout);
        super.onCreate(savedInstanceState);
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
       // setTheme(R.style.Theme_AppCompat_DayNight_NoActionBar);

        setContentView(R.layout.activity_options);

        sLayout= findViewById(R.id.switch1);
        sMode= findViewById(R.id.switch2);

        mPrefs = getSharedPreferences("test",MODE_PRIVATE);

        if(layout.equals("dark"))
            sLayout.setChecked(true);
        if(oPerfs.getString("mode","lead").equals("directions"))
            sMode.setChecked(true);

        editor = oPerfs.edit();

        sLayout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    editor.putString("layout", "dark");
                    layout = "dark";
                    //setTheme(R.style.Theme_AppCompat_DayNight_NoActionBar);
                }
                if(!isChecked) {
                    editor.putString("layout", "light");
                    layout = "light";
                    //setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
                }

            }
        });

        sMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    editor.putString("mode","directions");
                if(!isChecked)
                    editor.putString("mode","lead");
            }
        });
    }

    public void onCancel(View view) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish(); }

    public void onSave(View view) {
        Toast.makeText(this, "Zapisane", Toast.LENGTH_SHORT).show();
        editor.commit();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
