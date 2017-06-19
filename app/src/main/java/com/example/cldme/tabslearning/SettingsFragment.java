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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.thermostatapp.util.HeatingSystem;

/**
 * Created by Claudiu Ion on 09/06/2017.
 */

public class SettingsFragment extends Fragment {

    public static TextView serverDay, serverTime;
    public static TextView settingsServerDay, settingsServerTime;
    //Array for storing the days of the week
    public static String[] weekDays = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.settings_fragment, container, false);

        serverDay = (TextView) view.findViewById(R.id.settings_day);
        serverTime = (TextView) view.findViewById(R.id.settings_time);
        settingsServerDay = (TextView) view.findViewById(R.id.settings_server_day);
        settingsServerTime = (TextView) view.findViewById(R.id.settings_server_time);

        serverDay.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        serverTime.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        serverDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog tempDialog = new Dialog(getContext());

                tempDialog.setContentView(R.layout.day_change_dialog);

                Button okButton = (Button) tempDialog.findViewById(R.id.server_ok_button);
                Button cancelButton = (Button) tempDialog.findViewById(R.id.server_cancel_button);
                final EditText newWeekDay = (EditText) tempDialog.findViewById(R.id.new_week_day);

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Check if the newTemp field is not empty
                        if(newWeekDay.getText().toString().length() > 0) {
                            final String newTempString = String.valueOf(newWeekDay.getText());

                            final String weekDay = newTempString.toLowerCase();
                            Boolean validInput = false;

                            for(int i = 0; i < 7; i++) {
                                if(weekDays[i].equals(weekDay)) {
                                    validInput = true;
                                }
                            }

                            if(validInput) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            HeatingSystem.put("day", weekDay);
                                        } catch(Exception e) {
                                            System.err.println("Error occurred " + e);
                                        }
                                    }
                                }).start();

                                //Close the temperature dialog
                                tempDialog.dismiss();
                            } else {
                                Toast.makeText(getContext(), "Please enter a valid week day", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tempDialog.dismiss();
                    }
                });

                tempDialog.show();
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
