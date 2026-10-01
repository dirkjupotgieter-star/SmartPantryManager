package com.example.smartpantrymanager;
//PantryAdapter
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PantryAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> names;
    ArrayList<String> quantities;
    ArrayList<String> units;

    public PantryAdapter(Context context, ArrayList<String> names,
                         ArrayList<String> quantities, ArrayList<String> units) {

        this.context = context;
        this.names = names;
        this.quantities = quantities;
        this.units = units;
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Object getItem(int position) {
        return names.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if(view == null) {
            view = LayoutInflater.from(context).inflate(
                    R.layout.pantry_item,
                    parent,
                    false
            );
        }

        TextView txtName = view.findViewById(R.id.txtItemName);
        TextView txtQty = view.findViewById(R.id.txtItemQty);

        txtName.setText(names.get(position));

        txtQty.setText(
                quantities.get(position) + " " + units.get(position)
        );

        return view;
    }
}
