package com.example.smartpantrymanager;
//SettingsActivity
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    Switch switchExpiry;
    RadioButton radioMetric, radioOther;

    Button btnSaveSettings;
    Button btnPantry, btnRecipes, btnSettings;

    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchExpiry = findViewById(R.id.switchExpiry);
        radioMetric = findViewById(R.id.radioMetric);
        radioOther = findViewById(R.id.radioOther);

        btnSaveSettings = findViewById(R.id.btnSaveSettings);

        btnPantry = findViewById(R.id.btnPantry);
        btnRecipes = findViewById(R.id.btnRecipes);
        btnSettings = findViewById(R.id.btnSettings);

        settings = getSharedPreferences("settings", MODE_PRIVATE);

        boolean expiry = settings.getBoolean("expiry", false);
        String unit = settings.getString("unit", "metric");

        switchExpiry.setChecked(expiry);

        if(unit.equals("metric")) {
            radioMetric.setChecked(true);
        } else {
            radioOther.setChecked(true);
        }

        btnSaveSettings.setOnClickListener(v -> {

            SharedPreferences.Editor editor = settings.edit();

            editor.putBoolean(
                    "expiry",
                    switchExpiry.isChecked()
            );

            if(radioMetric.isChecked()) {
                editor.putString("unit", "metric");
            } else {
                editor.putString("unit", "other");
            }

            editor.apply();

            Toast.makeText(
                    this,
                    "Settings saved",
                    Toast.LENGTH_SHORT
            ).show();
        });

        btnPantry.setOnClickListener(v -> {

            Intent i = new Intent(
                    SettingsActivity.this,
                    MainActivity.class
            );

            startActivity(i);
            finish();
        });

        btnRecipes.setOnClickListener(v -> {

            Intent i = new Intent(
                    SettingsActivity.this,
                    SuggestedRecipesActivity.class
            );

            startActivity(i);
            finish();
        });
    }
}
