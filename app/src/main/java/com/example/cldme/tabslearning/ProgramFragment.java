package com.example.cldme.tabslearning;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import org.thermostatapp.util.*;
import org.thermostatapp.util.Switch;

import java.util.ArrayList;

/**
 * Created by Claudiu Ion on 09/06/2017.
 */

public class ProgramFragment extends Fragment implements View.OnClickListener{

    //Declare variables for the different view items on the programFragment page
    Button addButton;
    Button saveButton;
    Button resetButton;

    //Declare an array for the text views that are in the layout
    private TextView[] timesView = new TextView[10];
    //Hours array for checking duplicate hours
    private int[] hoursArray = new int[24];
    //Minutes array for checking duplicate minutes
    private int[] minutesArray = new int[60];
    //Arrays for storing the configuration of the switches
    public static String[] daySwitch = new String[10];
    public static String[] timeSwitch = new String[10];
    public static Boolean[] stateSwitch = new Boolean[10];

    //Flag variable for marking if changes were made
    public static Boolean hasChanged;
    //Flag variable for keeping tack of week program update
    public static Boolean hasUpdated;

    //Declare the switch array list (as retrieved from the server)
    ArrayList<Switch> switchArrayList = new ArrayList<Switch>();
    //The switch array list which is going to be modified to fit our format
    ArrayList<Switch> customSwitchArrayList = new ArrayList<Switch>();

    //Declare the week program variable
    public static WeekProgram wpg;

    //Declare current fragment view
    Fragment currentFragment;

    //Store the context of the current fragment for future use
    private Context viewContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.program_fragment, container, false);
        currentFragment = this;
        viewContext = view.getContext();

        //When the program fragment is loaded no changes were made
        hasChanged = false;
        //When the program fragment is loaded no updates were made
        hasUpdated = false;

        //Get the different elements that are in the programFragment
        addButton = (Button) view.findViewById(R.id.add_button);
        saveButton = (Button) view.findViewById(R.id.save_button);
        resetButton = (Button) view.findViewById(R.id.reset_button);

        //Get all the text views from the fragment view
        timesView[0] = (TextView) view.findViewById(R.id.night_time1);
        timesView[1] = (TextView) view.findViewById(R.id.night_time2);
        timesView[2] = (TextView) view.findViewById(R.id.night_time3);
        timesView[3] = (TextView) view.findViewById(R.id.night_time4);
        timesView[4] = (TextView) view.findViewById(R.id.night_time5);
        timesView[5] = (TextView) view.findViewById(R.id.day_time1);
        timesView[6] = (TextView) view.findViewById(R.id.day_time2);
        timesView[7] = (TextView) view.findViewById(R.id.day_time3);
        timesView[8] = (TextView) view.findViewById(R.id.day_time4);
        timesView[9] = (TextView) view.findViewById(R.id.day_time5);

        for(int i = 0; i < 10; i++) {
            timesView[i].setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            timesView[i].setOnClickListener(this);
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Get the week program
                    wpg = HeatingSystem.getWeekProgram();

                    switchArrayList = wpg.data.get("Monday");

                } catch(Exception e) {

                }
            }
        });

        thread.start();

        try {
            //Wait for the week program to be retrieved from the server
            thread.join();

            //Configure the UI layout to properly display the week program retrieved from the server
            int dayIndex = 9, nightIndex = 4;
            String hoursString, minutesString;
            int pos = 0;

            for(int i = 0; i < switchArrayList.size(); i++) {
                String type = switchArrayList.get(i).type;
                Boolean state = switchArrayList.get(i).state;
                String time = switchArrayList.get(i).time;

                //Get the hours from the time string
                if(time.length() < 5) {
                    hoursString = "" + time.charAt(0);
                    minutesString = "" + time.charAt(1) + time.charAt(2);
                } else {
                    hoursString = "" + time.charAt(0) + time.charAt(1);
                    minutesString = "" + time.charAt(3) + time.charAt(4);
                }

                //Mark the existent hours and minutes to check for duplicates in the future
                int hours = Integer.parseInt(hoursString);
                int minutes = Integer.parseInt(minutesString);

                if(hours != 0 || minutes != 0)
                    hoursArray[hours] = minutesArray[minutes] = 1;

                if(type.equals("day")) {
                    timesView[dayIndex].setText(time);
                    updateSwitches(dayIndex, type, state, time);
                    //Log.d("custom", daySwitch[dayIndex] + " " + stateSwitch[dayIndex] + " " + timeSwitch[dayIndex]);
                    dayIndex -= 1;
                } else {
                    timesView[nightIndex].setText(time);
                    updateSwitches(nightIndex, type, state, time);
                    //Log.d("custom", daySwitch[nightIndex] + " " + stateSwitch[nightIndex] + " " + timeSwitch[nightIndex]);
                    nightIndex -= 1;
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //Get the new week program from the UI layout
                            for(int i = 0; i < 10; i++) {
                                wpg.data.get("Monday").set(i, new Switch(daySwitch[i], stateSwitch[i], timeSwitch[i]));
                                //Log.d("custom", daySwitch[i] + " " + stateSwitch[i] + " " + timeSwitch[i]);
                            }

                            //Check for duplicates (it should not happen)
                            //If it does do not update the program and promt the user with instructions
                            boolean duplicates = wpg.duplicates(wpg.data.get("Monday"));
                            //If no duplicates are found, update the week program
                            if(!duplicates) {
                                //Send the week program to be SAVED on the server
                                HeatingSystem.setWeekProgram(wpg);
                            } else {
                                Toast.makeText(getContext(), "There was an error with the program. Please check for duplicates", Toast.LENGTH_LONG).show();
                            }

                            //Mark that the program was updated on the server
                            hasUpdated = true;

                        } catch (Exception e) {

                        }
                    }
                }).start();
                Toast.makeText(getContext(), "The program is now saved", Toast.LENGTH_SHORT).show();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread resetThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        setDefaultWeekProgram();
                        FragmentTransaction frgTransaction = getFragmentManager().beginTransaction();
                        frgTransaction.detach(currentFragment).attach(currentFragment).commit();

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });

                resetThread.start();

                try {
                    //Wait for the thread to finish update the data on the server
                    resetThread.join();
                    //Prompt user with message that the week program is now reset
                    Toast.makeText(getContext(), "The program was reset to default", Toast.LENGTH_SHORT).show();
                    //Reset the hours and minutes array to be used for duplicate checking again
                    for(int i = 0; i < hoursArray.length; i++)
                        hoursArray[i] = 0;
                    for(int i = 0; i < minutesArray.length; i++)
                        minutesArray[i] = 0;
                } catch (Exception e) {
                    System.err.println("Error occurred " + e);
                }
            }
        });

        return view;
    }

    @Override
    public void onClick(final View viewMain) {

        //Generate the timerPicker dialog
        final Dialog timerDialog = new Dialog(viewContext);

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

                if(hoursArray[hour] == 1 && minutesArray[minute] == 1) {
                    Toast.makeText(getContext(), "There is already a switch for this time combination", Toast.LENGTH_SHORT).show();
                } else {
                    hoursArray[hour] = minutesArray[minute] = 1;
                    updateTextView(hour, minute, viewMain);
                    timerDialog.dismiss();
                }
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

    //Update the switch arrays (type/state/time arrays)
    public void updateSwitches(int pos, String type, Boolean state, String time) {
        daySwitch[pos] = type;
        stateSwitch[pos] = state;
        timeSwitch[pos] = time;
    }

    //Update the text views with the new time
    public void updateTextView(int hours, int minutes, View view) {
        String hoursString = String.valueOf(hours);
        String minutesString = String.valueOf(minutes);

        //Mark that changes were made
        hasChanged = true;

        //Convert single digit minutes to two digits 7 - 07
        if(minutes < 10) {
            minutesString = "0" + minutesString;
        }

        //Make the final time string
        String time = hoursString + ":" + minutesString;

        switch(view.getId()) {
            case R.id.night_time1:
                timesView[0].setText(time);
                updateSwitches(0, "night", true, time);
                break;
            case R.id.night_time2:
                timesView[1].setText(time);
                updateSwitches(1, "night", true, time);
                break;
            case R.id.night_time3:
                timesView[2].setText(time);
                updateSwitches(2, "night", true, time);
                break;
            case R.id.night_time4:
                timesView[3].setText(time);
                updateSwitches(3, "night", true, time);
                break;
            case R.id.night_time5:
                timesView[4].setText(time);
                updateSwitches(4, "night", true, time);
                break;
            case R.id.day_time1:
                timesView[5].setText(time);
                updateSwitches(5, "day", true, time);
                break;
            case R.id.day_time2:
                timesView[6].setText(time);
                updateSwitches(6, "day", true, time);
                break;
            case R.id.day_time3:
                timesView[7].setText(time);
                updateSwitches(7, "day", true, time);
                break;
            case R.id.day_time4:
                timesView[8].setText(time);
                updateSwitches(8, "day", true, time);
                break;
            case R.id.day_time5:
                timesView[9].setText(time);
                updateSwitches(9, "day", true, time);
                break;
        }
    }

    public static ProgramFragment newInstance() {
        //Create a new DashboardFragment object
        ProgramFragment programFragment = new ProgramFragment();

        //Return the newly created dashboardFragment object
        return programFragment;
    }

    public void setDefaultWeekProgram() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /* Uncomment the following parts to see how to work with the properties of the week program */
                    // Get the week program
                    WeekProgram wpg = HeatingSystem.getWeekProgram();
                    // Set the week program to default
                    wpg.setDefault();

                    //Send the week program to the server to be updated
                    HeatingSystem.setWeekProgram(wpg);
                    /*
                    wpg.data.get("Monday").set(5, new Switch("day", true, "07:30"));
                    wpg.data.get("Monday").set(1, new Switch("night", true, "08:30"));
                    wpg.data.get("Monday").set(6, new Switch("day", true, "18:00"));
                    wpg.data.get("Monday").set(7, new Switch("day", true, "12:00"));
                    wpg.data.get("Monday").set(8, new Switch("day", true, "18:00"));
                    boolean duplicates = wpg.duplicates(wpg.data.get("Monday"));
                    System.out.println("Duplicates found "+duplicates);
                    */
                    //Upload the updated program
                    //HeatingSystem.setWeekProgram(wpg);
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }
}
