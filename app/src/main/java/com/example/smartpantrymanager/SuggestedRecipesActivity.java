package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SuggestedRecipesActivity extends AppCompatActivity {

    ListView listRecipes;
    TextView txtNoRecipes;

    Button btnPantry, btnRecipes, btnSettings;

    DatabaseHelper db;

    ArrayList<String> recipeNames;
    ArrayList<String> recipeIngredients;
    ArrayList<String> recipeMethods;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        listRecipes = findViewById(R.id.listRecipes);
        txtNoRecipes = findViewById(R.id.txtNoRecipes);

        btnPantry = findViewById(R.id.btnPantry);
        btnRecipes = findViewById(R.id.btnRecipes);
        btnSettings = findViewById(R.id.btnSettings);

        db = new DatabaseHelper(this);

        recipeNames = new ArrayList<>();
        recipeIngredients = new ArrayList<>();
        recipeMethods = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                recipeNames
        );

        listRecipes.setAdapter(adapter);

        listRecipes.setOnItemClickListener((parent, view, position, id) -> {

            Intent i = new Intent(
                    SuggestedRecipesActivity.this,
                    RecipeDetailActivity.class
            );

            i.putExtra(
                    "name",
                    recipeNames.get(position)
            );

            i.putExtra(
                    "ingredients",
                    recipeIngredients.get(position)
            );

            i.putExtra(
                    "method",
                    recipeMethods.get(position)
            );

            startActivity(i);
        });

        btnPantry.setOnClickListener(v -> {

            Intent i = new Intent(
                    SuggestedRecipesActivity.this,
                    MainActivity.class
            );

            startActivity(i);
            finish();
        });

        btnSettings.setOnClickListener(v -> {

            Intent i = new Intent(
                    SuggestedRecipesActivity.this,
                    SettingsActivity.class
            );

            startActivity(i);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadRecipes();
    }

    void loadRecipes() {

        recipeNames.clear();
        recipeIngredients.clear();
        recipeMethods.clear();

        Cursor recipes =
                db.getRecipes();

        while(recipes.moveToNext()) {

            String name =
                    recipes.getString(1);

            String ingredients =
                    recipes.getString(2);

            String method =
                    recipes.getString(3);

            if(canMakeRecipe(ingredients)) {

                recipeNames.add(name);
                recipeIngredients.add(ingredients);
                recipeMethods.add(method);
            }
        }

        recipes.close();

        adapter.notifyDataSetChanged();

        if(recipeNames.size() == 0) {

            txtNoRecipes.setVisibility(
                    View.VISIBLE
            );

            listRecipes.setVisibility(
                    View.GONE
            );

        } else {

            txtNoRecipes.setVisibility(
                    View.GONE
            );

            listRecipes.setVisibility(
                    View.VISIBLE
            );
        }
    }

    boolean canMakeRecipe(String recipeIngredients) {

        String[] neededItems =
                recipeIngredients.split(",");

        for(String item : neededItems) {

            String[] parts =
                    item.split(":");

            if(parts.length < 3) {
                return false;
            }

            String neededName =
                    cleanName(parts[0]);

            double neededQty =
                    Double.parseDouble(parts[1]);

            String neededUnit =
                    cleanUnit(parts[2]);

            double totalAmount = 0;

            Cursor pantry =
                    db.getItems();

            while(pantry.moveToNext()) {

                String pantryName =
                        cleanName(
                                pantry.getString(1)
                        );

                double pantryQty =
                        pantry.getDouble(2);

                String pantryUnit =
                        cleanUnit(
                                pantry.getString(3)
                        );

                if(pantryName.equals(neededName)) {

                    String pantryType =
                            unitType(pantryUnit);

                    String neededType =
                            unitType(neededUnit);

                    if(pantryType.equals(neededType)) {

                        totalAmount =
                                totalAmount
                                        + convertAmount(
                                        pantryQty,
                                        pantryUnit
                                );
                    }
                }
            }

            pantry.close();

            double neededAmount =
                    convertAmount(
                            neededQty,
                            neededUnit
                    );

            if(totalAmount < neededAmount) {
                return false;
            }
        }

        return true;
    }

    String cleanName(String name) {

        name = name.toLowerCase().trim();

        if(name.endsWith("ies")) {

            name = name.substring(
                    0,
                    name.length() - 3
            ) + "y";

        } else if(name.endsWith("oes")) {

            name = name.substring(
                    0,
                    name.length() - 2
            );

        } else if(name.endsWith("s")
                && name.length() > 3) {

            name = name.substring(
                    0,
                    name.length() - 1
            );
        }

        return name;
    }

    String cleanUnit(String unit) {

        unit = unit.toLowerCase().trim();

        if(unit.equals("grams")
                || unit.equals("gram")) {

            return "g";
        }

        if(unit.equals("kilograms")
                || unit.equals("kilogram")
                || unit.equals("kgs")) {

            return "kg";
        }

        if(unit.equals("millilitres")
                || unit.equals("milliliters")
                || unit.equals("millilitre")
                || unit.equals("milliliter")) {

            return "ml";
        }

        if(unit.equals("litres")
                || unit.equals("liters")
                || unit.equals("litre")
                || unit.equals("liter")) {

            return "l";
        }

        if(unit.equals("units")
                || unit.equals("piece")
                || unit.equals("pieces")) {

            return "unit";
        }

        if(unit.equals("slice")) {
            return "slices";
        }

        return unit;
    }

    double convertAmount(double amount, String unit) {

        if(unit.equals("kg")) {
            return amount * 1000;
        }

        if(unit.equals("l")) {
            return amount * 1000;
        }

        return amount;
    }

    String unitType(String unit) {

        if(unit.equals("g")
                || unit.equals("kg")) {

            return "weight";
        }

        if(unit.equals("ml")
                || unit.equals("l")) {

            return "liquid";
        }

        if(unit.equals("unit")) {
            return "unit";
        }

        if(unit.equals("slices")) {
            return "slices";
        }

        return unit;
    }
}