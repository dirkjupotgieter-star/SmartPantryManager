package com.example.smartpantrymanager;
//MainActivity
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button btnAdd, btnPantry, btnRecipes, btnSettings;

    ListView listPantry;
    ListView listExpiry;

    TextView txtExpiryMessage;

    DatabaseHelper db;

    ArrayList<String> names;
    ArrayList<String> quantities;
    ArrayList<String> units;

    ArrayList<String> expiryItems;

    PantryAdapter adapter;
    ArrayAdapter<String> expiryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        btnPantry = findViewById(R.id.btnPantry);
        btnRecipes = findViewById(R.id.btnRecipes);
        btnSettings = findViewById(R.id.btnSettings);

        listPantry = findViewById(R.id.listPantry);
        listExpiry = findViewById(R.id.listExpiry);

        txtExpiryMessage = findViewById(R.id.txtExpiryMessage);

        db = new DatabaseHelper(this);

        names = new ArrayList<>();
        quantities = new ArrayList<>();
        units = new ArrayList<>();

        expiryItems = new ArrayList<>();

        adapter = new PantryAdapter(
                this,
                names,
                quantities,
                units
        );

        expiryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                expiryItems
        );

        listPantry.setAdapter(adapter);
        listExpiry.setAdapter(expiryAdapter);

        btnAdd.setOnClickListener(v -> {

            Intent i = new Intent(
                    MainActivity.this,
                    AddIngredientActivity.class
            );

            startActivity(i);
        });

        btnRecipes.setOnClickListener(v -> {

            Intent i = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );

            startActivity(i);
        });

        btnSettings.setOnClickListener(v -> {

            Intent i = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );

            startActivity(i);
        });

        listPantry.setOnItemClickListener((parent, view, position, id) -> {

            Intent i = new Intent(
                    MainActivity.this,
                    BatchListActivity.class
            );

            i.putExtra(
                    "name",
                    names.get(position)
            );

            i.putExtra(
                    "unit",
                    units.get(position)
            );

            startActivity(i);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadItems();
        loadExpiryItems();
    }

    void loadItems() {

        names.clear();
        quantities.clear();
        units.clear();

        LinkedHashMap<String, Double> totals =
                new LinkedHashMap<>();

        LinkedHashMap<String, String> realNames =
                new LinkedHashMap<>();

        LinkedHashMap<String, String> realUnits =
                new LinkedHashMap<>();

        Cursor c = db.getItems();

        while(c.moveToNext()) {

            String name = c.getString(1);
            double qty = c.getDouble(2);
            String unit = c.getString(3);

            String key =
                    name.toLowerCase().trim()
                            + "|"
                            + unit.toLowerCase().trim();

            if(totals.containsKey(key)) {

                double old = totals.get(key);

                totals.put(
                        key,
                        old + qty
                );

            } else {

                totals.put(
                        key,
                        qty
                );

                realNames.put(
                        key,
                        name
                );

                realUnits.put(
                        key,
                        unit
                );
            }
        }

        c.close();

        for(Map.Entry<String, Double> item : totals.entrySet()) {

            String key = item.getKey();

            names.add(
                    realNames.get(key)
            );

            quantities.add(
                    String.valueOf(item.getValue())
            );

            units.add(
                    realUnits.get(key)
            );
        }

        adapter.notifyDataSetChanged();
    }

    void loadExpiryItems() {

        expiryItems.clear();

        SharedPreferences settings =
                getSharedPreferences(
                        "settings",
                        MODE_PRIVATE
                );

        boolean expiryAlerts =
                settings.getBoolean(
                        "expiry",
                        false
                );

        if(!expiryAlerts) {

            txtExpiryMessage.setText(
                    "Expiry alerts are turned off"
            );

            txtExpiryMessage.setVisibility(
                    View.VISIBLE
            );

            listExpiry.setVisibility(
                    View.GONE
            );

            return;
        }

        SimpleDateFormat format =
                new SimpleDateFormat(
                        "d/M/yyyy",
                        Locale.getDefault()
                );

        Calendar today =
                Calendar.getInstance();

        Calendar limit =
                Calendar.getInstance();

        limit.add(
                Calendar.DAY_OF_YEAR,
                3
        );

        Cursor c = db.getItems();

        while(c.moveToNext()) {

            String name = c.getString(1);
            double qty = c.getDouble(2);
            String unit = c.getString(3);
            String expiry = c.getString(4);

            if(expiry == null || expiry.isEmpty()) {
                continue;
            }

            try {

                Date date =
                        format.parse(expiry);

                if(date != null
                        && !date.before(today.getTime())
                        && !date.after(limit.getTime())) {

                    expiryItems.add(
                            name
                                    + " - "
                                    + qty
                                    + " "
                                    + unit
                                    + " - expires "
                                    + expiry
                    );
                }

            } catch(Exception e) {

            }
        }

        c.close();

        expiryAdapter.notifyDataSetChanged();

        if(expiryItems.size() == 0) {

            txtExpiryMessage.setText(
                    "No items expiring soon"
            );

            txtExpiryMessage.setVisibility(
                    View.VISIBLE
            );

            listExpiry.setVisibility(
                    View.GONE
            );

        } else {

            txtExpiryMessage.setVisibility(
                    View.GONE
            );

            listExpiry.setVisibility(
                    View.VISIBLE
            );
        }
    }
}
