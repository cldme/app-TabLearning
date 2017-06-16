package com.example.cldme.tabslearning;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.Switch;
import org.thermostatapp.util.WeekProgram;

import java.util.ArrayList;

/**
 * Created by Claudiu Ion on 10/06/2017.
 */

public class CustomArrayAdapter extends ArrayAdapter<Switch> {

    //Declare the items that are present in the current view
    private TextView textView;
    private TextView timeText;

    //ArrayList for storing the switches that are passed to the constructor
    private ArrayList<Switch> switches;

    //Fragment Manager
    FragmentManager fragmentManager;

    //Declare strings for the minutes and hours text
    private String minutesText, hoursText;

    //Declare the timePicker
    private TimePicker timePicker;

    //Get the context for the timerDialog layout
    private static Context appContext;

    public CustomArrayAdapter(ProgramFragment programFragment, @NonNull Context context, ArrayList<Switch> values) {
        super(context, R.layout.custom_row ,values);
        this.switches = values;
        fragmentManager = programFragment.getFragmentManager();
        appContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Get the switch at the current position
        final Switch customSwitch = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_row, parent, false);
        }
        //Convert the convertView variable to final so it can be used in further methods
        final View finalConvertView = convertView;

        //Get the elements that are in the custom_row.xml file for data population
        textView = (TextView) convertView.findViewById(R.id.day_text);
        timeText = (TextView) convertView.findViewById(R.id.day_time);

        //Set the text inside the text view (random text is placeholder)
        String typeSwitch = customSwitch.type;
        String timeSwitch = customSwitch.time;

        textView.setText(typeSwitch);
        timeText.setText(timeSwitch);

        timeText.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Dialog timerDialog = new Dialog(appContext);

                timerDialog.setContentView(R.layout.timer_dialog);

                timerDialog.setTitle("Time Picker");

                timePicker = (TimePicker) timerDialog.findViewById(R.id.timer);
                Button okButton = (Button) timerDialog.findViewById(R.id.ok_button);
                Button cancelButton = (Button) timerDialog.findViewById(R.id.cancel_button);

                timePicker.setIs24HourView(true);

                getCustomTime();

                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        minutesText = String.valueOf(minute);
                        hoursText = String.valueOf(hourOfDay);
                        if(minute < 10) {
                            minutesText = "0" + minutesText;
                        }
                        //timeText.setText(hoursText + ":" + minutesText);
                        //Not sure if this is a good idea to update the data set while onChangeListener
                        customSwitch.time = hoursText + ":" + minutesText;
                        notifyDataSetChanged();
                    }
                });

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timerDialog.dismiss();
                        //Update customSwitch.time and notify data set changed (ok button)
                        customSwitch.time = hoursText + ":" + minutesText;
                        notifyDataSetChanged();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timerDialog.dismiss();
                        //Notify that data set has changed but don't update anything (cancel button)
                        //customSwitch.time = hoursText + ":" + minutesText;
                        notifyDataSetChanged();
                    }
                });

                timerDialog.show();
            }
        });

        return convertView;
    }

    //Configure the minutes to be displayed with two digits (<10) and get the hours
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getCustomTime() {

        if(timePicker.getMinute() < 10)
            minutesText = "0" + String.valueOf(timePicker.getMinute());
        else
            minutesText = String.valueOf(timePicker.getMinute());
        hoursText = String.valueOf(timePicker.getHour());
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
