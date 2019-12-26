package com.rbcode.yourdestinations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class Options extends AppCompatActivity {

    Switch sLayout,sMode;
    SharedPreferences mPrefs,oPerfs;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        sLayout= findViewById(R.id.switch1);
        sMode= findViewById(R.id.switch2);

        mPrefs = getSharedPreferences("test",MODE_PRIVATE);
        oPerfs = getSharedPreferences("options",MODE_PRIVATE);

        if(oPerfs.getString("layout","light").equals("dark"))
            sLayout.setChecked(true);
        if(oPerfs.getString("mode","lead").equals("directions"))
            sMode.setChecked(true);

        editor = oPerfs.edit();

        sLayout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    editor.putString("layout", "dark");
                    setTheme(R.style.Theme_AppCompat_DayNight_NoActionBar);
                }
                if(!isChecked) {
                    editor.putString("layout", "light");
                    setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
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
        finish();
    }

    public void onSave(View view) {
        Toast.makeText(this, "Zapisane", Toast.LENGTH_SHORT).show();
        editor.commit();
        finish();
    }
}
