package com.example.smartpantrymanager;
//datanasehelper
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    static final String DBNAME = "pantry.db";

    public DatabaseHelper(Context context) {
        super(context, DBNAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE pantry (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "quantity REAL," +
                "unit TEXT," +
                "expiry TEXT)");

        db.execSQL("CREATE TABLE recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "ingredients TEXT," +
                "method TEXT)");

        addRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if(oldVersion < 2) {
            db.execSQL("DELETE FROM recipes");
            addRecipes(db);
        }
    }

    public boolean addItem(String name, double qty, String unit, String expiry) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("quantity", qty);
        values.put("unit", unit);
        values.put("expiry", expiry);

        long x = db.insert("pantry", null, values);

        return x != -1;
    }

    public Cursor getItems() {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM pantry ORDER BY name",
                null
        );
    }

    public Cursor getBatches(String name, String unit) {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM pantry " +
                        "WHERE LOWER(name)=LOWER(?) " +
                        "AND LOWER(unit)=LOWER(?) " +
                        "ORDER BY id",
                new String[]{name, unit}
        );
    }

    public boolean updateItem(int id, String name, double qty, String unit, String expiry) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("quantity", qty);
        values.put("unit", unit);
        values.put("expiry", expiry);

        int x = db.update(
                "pantry",
                values,
                "id=?",
                new String[]{String.valueOf(id)}
        );

        return x > 0;
    }

    public boolean deleteItem(int id) {

        SQLiteDatabase db = getWritableDatabase();

        int x = db.delete(
                "pantry",
                "id=?",
                new String[]{String.valueOf(id)}
        );

        return x > 0;
    }

    private void addRecipes(SQLiteDatabase db) {

        addRecipe(db, "Scrambled Eggs",
                "egg:2:unit,butter:10:g",
                "Beat eggs and cook them in butter.");

        addRecipe(db, "Egg Toast",
                "egg:1:unit,bread:2:slices",
                "Cook the egg and put it on toast.");

        addRecipe(db, "Cheese Toast",
                "bread:2:slices,cheese:50:g",
                "Put cheese on bread and toast it.");

        addRecipe(db, "Tomato Toast",
                "bread:2:slices,tomato:1:unit",
                "Slice tomato and put it on toast.");

        addRecipe(db, "Simple Omelette",
                "egg:2:unit,milk:30:ml",
                "Mix eggs and milk then cook in a pan.");

        addRecipe(db, "Cheese Omelette",
                "egg:2:unit,cheese:30:g",
                "Mix eggs and cheese then cook.");

        addRecipe(db, "Chicken Rice",
                "chicken:200:g,rice:100:g",
                "Cook the chicken and rice and serve together.");

        addRecipe(db, "Chicken Pasta",
                "chicken:200:g,pasta:100:g",
                "Cook chicken and pasta then mix.");

        addRecipe(db, "Tomato Pasta",
                "pasta:100:g,tomato:2:unit",
                "Cook pasta and add chopped tomato.");

        addRecipe(db, "Cheese Pasta",
                "pasta:100:g,cheese:50:g",
                "Cook pasta and mix in cheese.");

        addRecipe(db, "Rice and Egg",
                "rice:100:g,egg:2:unit",
                "Cook rice and serve with egg.");

        addRecipe(db, "Chicken Sandwich",
                "bread:2:slices,chicken:100:g",
                "Cook chicken and put it between bread.");

        addRecipe(db, "Cheese Sandwich",
                "bread:2:slices,cheese:40:g",
                "Put cheese between two slices of bread.");

        addRecipe(db, "Tomato Sandwich",
                "bread:2:slices,tomato:1:unit",
                "Put sliced tomato between bread.");

        addRecipe(db, "Egg Sandwich",
                "bread:2:slices,egg:2:unit",
                "Cook eggs and put them between bread.");

        addRecipe(db, "Milk Rice",
                "rice:100:g,milk:200:ml",
                "Cook rice and mix with warm milk.");

        addRecipe(db, "Chicken Tomato Rice",
                "chicken:150:g,tomato:1:unit,rice:100:g",
                "Cook all ingredients and mix together.");

        addRecipe(db, "Egg Cheese Toast",
                "egg:1:unit,bread:2:slices,cheese:30:g",
                "Cook egg and serve on toast with cheese.");

        addRecipe(db, "Tomato Omelette",
                "egg:2:unit,tomato:1:unit",
                "Mix eggs and tomato and cook.");

        addRecipe(db, "Butter Rice",
                "rice:100:g,butter:10:g",
                "Cook rice and stir in butter.");
    }

    private void addRecipe(SQLiteDatabase db, String name, String ingredients, String method) {

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("ingredients", ingredients);
        values.put("method", method);

        db.insert("recipes", null, values);
    }

    public Cursor getRecipes() {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM recipes",
                null
        );
    }
}
