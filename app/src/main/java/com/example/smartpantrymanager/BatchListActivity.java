package com.example.smartpantrymanager;
//BatchListActivity
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class BatchListActivity extends AppCompatActivity {

    TextView txtTitle;
    ListView listBatches;
    Button btnBack;

    DatabaseHelper db;

    String ingredientName;
    String ingredientUnit;

    ArrayList<String> rows;
    ArrayList<Integer> ids;
    ArrayList<String> quantities;
    ArrayList<String> expiries;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_list);

        txtTitle = findViewById(R.id.txtTitle);
        listBatches = findViewById(R.id.listBatches);
        btnBack = findViewById(R.id.btnBack);

        db = new DatabaseHelper(this);

        ingredientName = getIntent().getStringExtra("name");
        ingredientUnit = getIntent().getStringExtra("unit");

        txtTitle.setText(ingredientName + " Batches");

        rows = new ArrayList<>();
        ids = new ArrayList<>();
        quantities = new ArrayList<>();
        expiries = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                rows
        );

        listBatches.setAdapter(adapter);

        listBatches.setOnItemClickListener((parent, view, position, id) -> {

            Intent i = new Intent(
                    BatchListActivity.this,
                    AddIngredientActivity.class
            );

            i.putExtra("id", ids.get(position));
            i.putExtra("name", ingredientName);
            i.putExtra("quantity", quantities.get(position));
            i.putExtra("unit", ingredientUnit);
            i.putExtra("expiry", expiries.get(position));

            startActivity(i);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadBatches();
    }

    void loadBatches() {

        rows.clear();
        ids.clear();
        quantities.clear();
        expiries.clear();

        Cursor c = db.getBatches(
                ingredientName,
                ingredientUnit
        );

        while(c.moveToNext()) {

            int id = c.getInt(0);
            double qty = c.getDouble(2);
            String expiry = c.getString(4);

            ids.add(id);
            quantities.add(String.valueOf(qty));
            expiries.add(expiry);

            if(expiry == null || expiry.isEmpty()) {

                rows.add(
                        qty + " " + ingredientUnit +
                                " - no expiry date"
                );

            } else {

                rows.add(
                        qty + " " + ingredientUnit +
                                " - expires " + expiry
                );
            }
        }

        c.close();

        adapter.notifyDataSetChanged();
    }
}
