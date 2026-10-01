package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddIngredientActivity extends AppCompatActivity {

    EditText txtName, txtQuantity, txtUnit, txtExpiry;
    Button btnSave, btnCancel, btnDelete;

    DatabaseHelper db;

    int itemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        txtName = findViewById(R.id.txtName);
        txtQuantity = findViewById(R.id.txtQuantity);
        txtUnit = findViewById(R.id.txtUnit);
        txtExpiry = findViewById(R.id.txtExpiry);

        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnDelete = findViewById(R.id.btnDelete);

        db = new DatabaseHelper(this);

        itemId = getIntent().getIntExtra("id", -1);

        if(itemId != -1) {

            txtName.setText(getIntent().getStringExtra("name"));
            txtQuantity.setText(getIntent().getStringExtra("quantity"));
            txtUnit.setText(getIntent().getStringExtra("unit"));
            txtExpiry.setText(getIntent().getStringExtra("expiry"));

            btnDelete.setVisibility(View.VISIBLE);
            btnSave.setText("Update Ingredient");
        }

        txtExpiry.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> {

            String name = txtName.getText().toString().trim();
            String q = txtQuantity.getText().toString().trim();
            String unit = txtUnit.getText().toString().trim();
            String expiry = txtExpiry.getText().toString().trim();

            if(name.isEmpty()) {
                txtName.setError("Enter ingredient name");
                return;
            }

            if(q.isEmpty()) {
                txtQuantity.setError("Enter quantity");
                return;
            }

            if(unit.isEmpty()) {
                txtUnit.setError("Enter unit");
                return;
            }

            double qty;

            try {
                qty = Double.parseDouble(q);
            } catch(Exception e) {
                txtQuantity.setError("Quantity must be a number");
                return;
            }

            if(qty <= 0) {
                txtQuantity.setError("Quantity must be more than 0");
                return;
            }

            name = fixName(name);
            unit = unit.toLowerCase();

            if(itemId == -1) {

                boolean saved = db.addItem(
                        name,
                        qty,
                        unit,
                        expiry
                );

                if(saved) {

                    Toast.makeText(
                            this,
                            "Ingredient saved",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                }

            } else {

                boolean updated = db.updateItem(
                        itemId,
                        name,
                        qty,
                        unit,
                        expiry
                );

                if(updated) {

                    Toast.makeText(
                            this,
                            "Ingredient updated",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                }
            }
        });

        btnDelete.setOnClickListener(v -> {

            boolean deleted = db.deleteItem(itemId);

            if(deleted) {

                Toast.makeText(
                        this,
                        "Ingredient deleted",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
            }
        });

        btnCancel.setOnClickListener(v -> finish());
    }

    String fixName(String name) {

        name = name.toLowerCase().trim();

        if(name.length() > 0) {

            name = name.substring(0, 1).toUpperCase()
                    + name.substring(1);
        }

        return name;
    }

    void showDatePicker() {

        Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog picker = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {

                    String date =
                            selectedDay + "/" +
                                    (selectedMonth + 1) + "/" +
                                    selectedYear;

                    txtExpiry.setText(date);
                },
                year,
                month,
                day
        );

        picker.show();
    }
}