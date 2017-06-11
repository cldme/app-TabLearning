package com.example.cldme.tabslearning;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import org.thermostatapp.util.Switch;

import java.util.ArrayList;

/**
 * Created by Claudiu Ion on 10/06/2017.
 */

public class CustomArrayAdapter extends ArrayAdapter<Switch> {

    private TextView textView;
    private ArrayList<Switch> switches;

    public CustomArrayAdapter(@NonNull Context context, ArrayList<Switch> values) {
        super(context, R.layout.custom_row ,values);
        this.switches = values;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Get the switch at the current position
        Switch customSwitch = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_row, parent, false);
        }

        //Get the elements that are in the custom_row.xml file for data population
        textView = (TextView) convertView.findViewById(R.id.cutom_text_view);

        //Set the text inside the text view (random text is placeholder)
        String randomText = customSwitch.type + " " + customSwitch.time;

        textView.setText(randomText);

        return convertView;
    }

    @Override
    public int getCount() {
        return switches.size();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
