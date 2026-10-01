package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    TextView txtRecipeName, txtIngredients, txtMethod;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        txtRecipeName = findViewById(R.id.txtRecipeName);
        txtIngredients = findViewById(R.id.txtIngredients);
        txtMethod = findViewById(R.id.txtMethod);
        btnBack = findViewById(R.id.btnBack);

        String name = getIntent().getStringExtra("name");
        String ingredients = getIntent().getStringExtra("ingredients");
        String method = getIntent().getStringExtra("method");

        txtRecipeName.setText(name);
        txtIngredients.setText(makeIngredientsNice(ingredients));
        txtMethod.setText(method);

        btnBack.setOnClickListener(v -> finish());
    }

    String makeIngredientsNice(String ingredients) {

        String[] items = ingredients.split(",");
        String result = "";

        for(String item : items) {

            String[] parts = item.split(":");

            String name = parts[0];
            String qty = parts[1];
            String unit = parts[2];

            result = result + name + " - " + qty + " " + unit + "\n";
        }

        return result;
    }
}