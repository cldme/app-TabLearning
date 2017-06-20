package com.example.cldme.tabslearning;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.thermostatapp.util.HeatingSystem;

import java.util.ArrayList;

/**
 * Created by Claudiu Ion on 09/06/2017.
 */

public class SettingsFragment extends Fragment {

    public static TextView serverDay, serverTime;
    public static TextView settingsServerDay, settingsServerTime;
    public static Spinner daySpinner;
    public String currentDay, currentTime;
    //Array for storing the days of the week
    public static String[] weekDays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    //Array List for the spinner
    ArrayList<String> spinnerArray =  new ArrayList<String>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.settings_fragment, container, false);

        daySpinner = (Spinner) view.findViewById(R.id.settings_spinner);
        serverTime = (TextView) view.findViewById(R.id.settings_time);
        settingsServerDay = (TextView) view.findViewById(R.id.settings_server_day);
        settingsServerTime = (TextView) view.findViewById(R.id.settings_server_time);

        serverTime.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        //Configure the spinner to display the week days
        for(int i = 0; i < weekDays.length; i++) {
            spinnerArray.add(weekDays[i]);
        }
        //Configure the adapter for the spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(spinnerAdapter);

        Thread setupDay = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    currentDay = HeatingSystem.get("day");
                    currentTime = HeatingSystem.get("time");
                } catch (Exception e) {
                    System.err.println("Error occurred " + e);
                }
            }
        });

        setupDay.start();

        try {
            setupDay.join();
            int position = 0;
            for(int i = 0; i < 7; i++) {
                if(weekDays[i].equals(currentDay)) {
                    position = i;
                }
            }
            daySpinner.setSelection(position);
            serverTime.setHint(currentTime);
        } catch (Exception e) {
            System.err.println("Error occurred " + e);
        }

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String daySelected = daySpinner.getSelectedItem().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HeatingSystem.put("day", daySelected);
                        } catch (Exception e) {
                            System.err.println("Error occurred " + e);
                        }
                    }
                }).start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        serverTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Generate the timerPicker dialog
                final Dialog timerDialog = new Dialog(getContext());

                timerDialog.setContentView(R.layout.timer_dialog);

                timerDialog.setTitle("Time Picker");

                final TimePicker timePicker = (TimePicker) timerDialog.findViewById(R.id.timer);
                Button okButton = (Button) timerDialog.findViewById(R.id.ok_button);
                Button cancelButton = (Button) timerDialog.findViewById(R.id.cancel_button);

                timePicker.setIs24HourView(true);

                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    }
                });

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Check for duplicate times before dismissing the timer dialog
                        int hour = timePicker.getHour();
                        int minute = timePicker.getMinute();
                        String finalHour, finalMinute;

                        if(hour < 10) {
                            finalHour = "0" + hour;
                        } else {
                            finalHour = String.valueOf(hour);
                        }

                        if(minute < 10) {
                            finalMinute = "0" + minute;
                        } else {
                            finalMinute = String.valueOf(minute);
                        }

                        final String time = finalHour + ":" + finalMinute;
                        serverTime.setHint(time);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    HeatingSystem.put("time", time);
                                } catch (Exception e) {
                                    System.err.println("Error occurred " + e);
                                }
                            }
                        }).start();

                        timerDialog.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timerDialog.dismiss();
                    }
                });

                timerDialog.show();
            }
        });

        return view;
    }

    public static SettingsFragment newInstance() {
        //Create a new SettingsFragment object
        SettingsFragment settingsFragment = new SettingsFragment();

        //Return the newly created settingsFragment object
        return settingsFragment;
    }
}
