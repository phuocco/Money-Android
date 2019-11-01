package com.example.money;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.money.Home.HomeActivity;

public class SettingsActivity extends AppCompatActivity {
    boolean isDark;
    Switch light_mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        light_mode =  findViewById(R.id.switch_mode);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences =  getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);
        isDark = sharedPreferences.getBoolean(Constants.ISDARK,false);

        light_mode.setChecked(isDark);
        light_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(light_mode.isChecked()){
                    isDark = true;
                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(Constants.ISDARK,isDark);
                    editor.apply();
                    Toast.makeText(SettingsActivity.this, "on", Toast.LENGTH_SHORT).show();
                } else {
                    isDark = false;
                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(Constants.ISDARK,isDark);
                    editor.apply();
                    Toast.makeText(SettingsActivity.this, "off", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp(){
        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        finish();
        return true;
    }
}
