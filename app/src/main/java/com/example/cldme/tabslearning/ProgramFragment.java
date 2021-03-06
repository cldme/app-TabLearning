package com.example.cldme.tabslearning;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Vibrator;
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

public class ProgramFragment extends Fragment implements View.OnClickListener {

    //Declare variables for the different view items on the programFragment page
    Button removeButton, saveButton, resetButton;
    //Declare variables for the server day and time
    public static TextView progServerDay, progServerTime;
    public static ImageButton progLeft, progRight;
    public static TextView weekDay;

    //Declare variables for the time picker
    private AlertDialog.Builder builder;
    private TimePicker timer;

    //Declare an array for the text views that are in the layout
    private TextView[] timesView = new TextView[10];
    //Declare an array for the close images that are in the layout
    private ImageView[] closeImg = new ImageView[10];
    //Array for storing the days of the week
    public static String[] weekDays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    //Variable for keeping track of the current day
    public static int currentDayIndex = 0;
    //Hours array for checking duplicate hours
    private int[][] hoursArray = new int[24][60];
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
    //Flag variable for keeping track of the close images being open/close
    public static Boolean imgAreShown;

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

    //Declare a variable to make the device vibrate on clicks
    Vibrator vibe;

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
        //When the program fragment is loaded close images are hidden
        imgAreShown = false;
        //Get the context for the vibrate variable
        vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

        //Get the different elements that are in the programFragment
        removeButton = (Button) view.findViewById(R.id.remove_button);
        saveButton = (Button) view.findViewById(R.id.save_button);
        resetButton = (Button) view.findViewById(R.id.reset_button);
        weekDay = (TextView) view.findViewById(R.id.week_day);
        progLeft = (ImageButton) view.findViewById(R.id.program_left);
        progRight = (ImageButton) view.findViewById(R.id.program_right);
        progServerDay = (TextView) view.findViewById(R.id.program_day);
        progServerTime = (TextView) view.findViewById(R.id.program_time);

        //Set the weekday based on the current day index
        weekDay.setText(weekDays[currentDayIndex] + " Program");

        //Add action listeners to the program buttons
        progLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Make device vibrate in order to give user a feedback
                vibe.vibrate(100);
                //We check to see if unsaved changes are being lost
                checkWeekProgramStatus(currentDayIndex, daySwitch, stateSwitch, timeSwitch);
                //Decrease the currentDayIndex to display the previous day of the week
                currentDayIndex -= 1;
                //If we reach the start of the week we return to the end
                if(currentDayIndex < 0)
                    currentDayIndex = 6;
                //Set the correct day of the week in the program fragment
                weekDay.setText(weekDays[currentDayIndex] + " Program");
                getWeekProgram();
            }
        });

        progRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Make device vibrate in order to give user a feedback
                vibe.vibrate(100);
                //We check to see if unsaved changes are being lost
                checkWeekProgramStatus(currentDayIndex, daySwitch, stateSwitch, timeSwitch);
                //Increase the currentDayIndex to display the next day of the week
                currentDayIndex += 1;
                //If we reach the end of the week we return to the beginning
                if(currentDayIndex > 6)
                    currentDayIndex = 0;
                //Set the correct day of the week in the program fragment
                weekDay.setText(weekDays[currentDayIndex] + " Program");
                getWeekProgram();
            }
        });

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

        //Get all the image view from the fragment view
        closeImg[0] = (ImageView) view.findViewById(R.id.night_close1);
        closeImg[1] = (ImageView) view.findViewById(R.id.night_close2);
        closeImg[2] = (ImageView) view.findViewById(R.id.night_close3);
        closeImg[3] = (ImageView) view.findViewById(R.id.night_close4);
        closeImg[4] = (ImageView) view.findViewById(R.id.night_close5);
        closeImg[5] = (ImageView) view.findViewById(R.id.day_close1);
        closeImg[6] = (ImageView) view.findViewById(R.id.day_close2);
        closeImg[7] = (ImageView) view.findViewById(R.id.day_close3);
        closeImg[8] = (ImageView) view.findViewById(R.id.day_close4);
        closeImg[9] = (ImageView) view.findViewById(R.id.day_close5);


        for(int i = 0; i < 10; i++) {
            timesView[i].setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            timesView[i].setOnClickListener(this);
            closeImg[i].setOnClickListener(closeImageListener);
            closeImg[i].setVisibility(view.GONE);
        }

        //Get the week program and save the switches in the switchArrayList
        getWeekProgram();

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0; i < 10; i++) {
                    if(!imgAreShown)
                        closeImg[i].setVisibility(view.VISIBLE);
                    else
                        closeImg[i].setVisibility(view.GONE);
                }
                //Toggle the flag to keep track of the close images
                imgAreShown = !imgAreShown;
                //If flag is false, update the week program with the new switches
                updateWeekProgram();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWeekProgram();
                Toast.makeText(getContext(), "The program is now saved", Toast.LENGTH_SHORT).show();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibe.vibrate(500);

                final AlertDialog.Builder theDialog = new AlertDialog.Builder(getActivity());

                // Set the title for the Dialog
                theDialog.setTitle("Reset Program");

                // Set the message
                theDialog.setMessage("This will reset the weekly program. All switches will be removed and you will lose all changes. Are you sure ?");

                //Add the positive button
                theDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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
                            for(int i = 0; i < 24; i++)
                                for(int j = 0; j < 60; j++)
                                    hoursArray[i][j] = 0;
                        } catch (Exception e) {
                            System.err.println("Error occurred " + e);
                        }
                    }
                });

                theDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                // Returns the created dialog
                theDialog.create().show();
            }
        });

        return view;
    }

    @Override
    public void onClick(final View view) {

        // custom dialog
        builder = new AlertDialog.Builder(viewContext);
        builder.setTitle("Time Picker");

        // set dialog message
        builder
                .setMessage("Select the time for the new switch")
                .setView(R.layout.timer_dialog)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        updateTime(view);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.dismiss();
                    }
                });

        //Get the time picker from the dialog
        timer = (TimePicker) builder.show().findViewById(R.id.timer);
    }

    public void updateTime(View view) {

        int hour = timer.getHour();
        int minute = timer.getMinute();

        if(hoursArray[hour][minute] == 1) {
            Toast.makeText(getContext(), "There is already a switch for this time combination", Toast.LENGTH_SHORT).show();
        } else {
            hoursArray[hour][minute] = 1;
            updateTextView(hour, minute, view);
        }
    }

    //Update the arrays which are used for determining duplicates when removing a switch
    public void updateDuplicatesArray(String time) {
        String hoursString, minutesString;
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

        hoursArray[hours][minutes] = 0;
    }

    //Update the switch arrays (type/state/time arrays)
    public void updateSwitches(int pos, String type, Boolean state, String time) {
        daySwitch[pos] = type;
        stateSwitch[pos] = state;
        timeSwitch[pos] = time;
        timesView[pos].setText(time);
    }

    //Update the text views with the new time
    public void updateTextView(int hours, int minutes, View view) {
        String hoursString = String.valueOf(hours);
        String minutesString = String.valueOf(minutes);

        //Mark that changes were made
        hasChanged = true;
        //Mark that the changes were not updated on the server
        hasUpdated = false;

        //Convert single digit hours into double digits
        if(hours < 10) {
            hoursString = "0" + hoursString;
        }

        //Convert single digit minutes to two digits 7 - 07
        if(minutes < 10) {
            minutesString = "0" + minutesString;
        }

        //Make the final time string
        String time = hoursString + ":" + minutesString;

        switch(view.getId()) {
            case R.id.night_time1:
                updateSwitches(0, "night", true, time);
                break;
            case R.id.night_time2:
                updateSwitches(1, "night", true, time);
                break;
            case R.id.night_time3:
                updateSwitches(2, "night", true, time);
                break;
            case R.id.night_time4:
                updateSwitches(3, "night", true, time);
                break;
            case R.id.night_time5:
                updateSwitches(4, "night", true, time);
                break;
            case R.id.day_time1:
                updateSwitches(5, "day", true, time);
                break;
            case R.id.day_time2:
                updateSwitches(6, "day", true, time);
                break;
            case R.id.day_time3:
                updateSwitches(7, "day", true, time);
                break;
            case R.id.day_time4:
                updateSwitches(8, "day", true, time);
                break;
            case R.id.day_time5:
                updateSwitches(9, "day", true, time);
                break;
        }
    }

    public View.OnClickListener closeImageListener =
            new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Mark that changes were made
                hasChanged = true;
                //Mark that the changes were not updated on the server
                hasUpdated = false;

                //Make the final time string for reseting the switches (default time)
                String time = "00:00";

                switch (view.getId()) {
                    case R.id.night_close1:
                        updateDuplicatesArray(timesView[0].getText().toString());
                        updateSwitches(0, "night", false, time);
                        break;
                    case R.id.night_close2:
                        updateDuplicatesArray(timesView[1].getText().toString());
                        updateSwitches(1, "night", false, time);
                        break;
                    case R.id.night_close3:
                        updateDuplicatesArray(timesView[2].getText().toString());
                        updateSwitches(2, "night", false, time);
                        break;
                    case R.id.night_close4:
                        updateDuplicatesArray(timesView[3].getText().toString());
                        updateSwitches(3, "night", false, time);
                        break;
                    case R.id.night_close5:
                        updateDuplicatesArray(timesView[4].getText().toString());
                        updateSwitches(4, "night", false, time);
                        break;
                    case R.id.day_close1:
                        updateDuplicatesArray(timesView[5].getText().toString());
                        updateSwitches(5, "day", false, time);
                        break;
                    case R.id.day_close2:
                        updateDuplicatesArray(timesView[6].getText().toString());
                        updateSwitches(6, "day", false, time);
                        break;
                    case R.id.day_close3:
                        updateDuplicatesArray(timesView[7].getText().toString());
                        updateSwitches(7, "day", false, time);
                        break;
                    case R.id.day_close4:
                        updateDuplicatesArray(timesView[8].getText().toString());
                        updateSwitches(8, "day", false, time);
                        break;
                    case R.id.day_close5:
                        updateDuplicatesArray(timesView[9].getText().toString());
                        updateSwitches(9, "day", false, time);
                        break;
                }
            }
    };

    public static ProgramFragment newInstance() {
        //Create a new DashboardFragment object
        ProgramFragment programFragment = new ProgramFragment();

        //Return the newly created dashboardFragment object
        return programFragment;
    }

    //Get the week program from the server, based on the weekDays[currentDayIndex] day
    public void getWeekProgram() {

        Thread getWeekThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Get the week program
                    wpg = HeatingSystem.getWeekProgram();

                    switchArrayList = wpg.data.get(weekDays[currentDayIndex]);

                } catch(Exception e) {

                }
            }
        });

        getWeekThread.start();

        try {
            //Wait for the week program to be retrieved from the server
            getWeekThread.join();

            //Reset the arrays which are checking for doubles
            for(int i = 0; i < 24; i++) {
                for(int j = 0; j < 60; j++) {
                    hoursArray[i][j] = 0;
                }
            }

            //Configure the UI layout to properly display the week program retrieved from the server
            int dayIndex = 9, nightIndex = 4;
            String hoursString, minutesString;
            int pos = 0;
            Boolean doneDayEmptySwitches = false;
            Boolean doneNightEmptySwitches = false;

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

                if(hours != 0 || minutes != 0) {
                    hoursArray[hours][minutes] = 1;
                    if(type.equals("day") && doneDayEmptySwitches == false) {
                        doneDayEmptySwitches = true;
                        dayIndex = 5;
                    } else if(type.equals("night") && doneNightEmptySwitches == false){
                        doneNightEmptySwitches = true;
                        nightIndex = 0;
                    }
                }

                if(type.equals("day")) {
                    timesView[dayIndex].setText(time);
                    updateSwitches(dayIndex, type, state, time);
                    //Log.d("custom", daySwitch[dayIndex] + " " + stateSwitch[dayIndex] + " " + timeSwitch[dayIndex]);
                    if(doneDayEmptySwitches) {
                        dayIndex += 1;
                    } else {
                        dayIndex -= 1;
                    }
                } else {
                    timesView[nightIndex].setText(time);
                    updateSwitches(nightIndex, type, state, time);
                    //Log.d("custom", daySwitch[nightIndex] + " " + stateSwitch[nightIndex] + " " + timeSwitch[nightIndex]);
                    if(doneNightEmptySwitches) {
                        nightIndex += 1;
                    } else {
                        nightIndex -= 1;
                    }
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Put the new week program on the server, based on the weekDays[currentDayIndex] day
    public void updateWeekProgram() {
        Thread updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Set the switches for the new week program according to UI layout
                    for(int i = 0; i < 10; i++) {
                        wpg.data.get(weekDays[currentDayIndex]).set(i, new Switch(daySwitch[i], stateSwitch[i], timeSwitch[i]));
                        //Log.d("custom", daySwitch[i] + " " + stateSwitch[i] + " " + timeSwitch[i]);
                    }

                    //Check for duplicates (it should not happen)
                    //If it does do not update the program and promt the user with instructions
                    boolean duplicates = wpg.duplicates(wpg.data.get(weekDays[currentDayIndex]));
                    //If no duplicates are found, update the week program
                    if(!duplicates) {
                        //Send the week program to be SAVED on the server
                        HeatingSystem.setWeekProgram(wpg);
                    } else {
                        Toast.makeText(getContext(), "There was an error with the program. Please check for duplicates", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {

                }
            }
        });

        updateThread.start();

        try {
            //Wait for the update thread to finish
            updateThread.join();
            //Mark that the program was updated on the server
            hasUpdated = true;
        } catch (Exception e) {

        }
    }

    //Reset the week program on the server (default program has all switches turned off and set to 0:00)
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

    //Checks whether or not the program was changed but not updated on the server, in which case it updates the program
    public void checkWeekProgramStatus(final int pos, final String[] daySwitch, final Boolean[] stateSwitch, final String[] timeSwitch) {
        if(ProgramFragment.hasChanged && !ProgramFragment.hasUpdated) {
            final String customDay = weekDays[pos];
            final String[] days = new String[10];
            final Boolean[] state = new Boolean[10];
            final String[] time = new String[10];

            for(int i = 0; i < 10; i++) {
                Log.d("custom", daySwitch[i] + " " + stateSwitch[i] + " " + timeSwitch[i]);
                days[i] = daySwitch[i];
                state[i] = stateSwitch[i];
                time[i] = timeSwitch[i];
            }

            AlertDialog.Builder theDialog = new AlertDialog.Builder(getActivity());

            // Set the title for the Dialog
            theDialog.setTitle("Save Changes");

            // Set the message
            theDialog.setMessage("You have unsaved changes. Do you want to save them ?");

            // Add text for a positive button
            theDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //Set the switches for the new week program according to UI layout
                                for(int i = 0; i < 10; i++) {
                                    wpg.data.get(customDay).set(i, new Switch(days[i], state[i], time[i]));
                                    Log.d("custom1", days[i] + " " + state[i] + " " + time[i]);
                                }

                                //Check for duplicates (it should not happen)
                                //If it does do not update the program and promt the user with instructions
                                boolean duplicates = wpg.duplicates(wpg.data.get(customDay));
                                //If no duplicates are found, update the week program
                                if(!duplicates) {
                                    //Send the week program to be SAVED on the server
                                    HeatingSystem.setWeekProgram(wpg);
                                } else {
                                    Toast.makeText(getContext(), "There was an error with the program. Please check for duplicates", Toast.LENGTH_LONG);
                                }

                                //Mark that the program was updated on the server
                                hasUpdated = true;

                            } catch (Exception e) {

                            }
                        }
                    }).start();

                    Toast.makeText(getContext(), "The changes were saved", Toast.LENGTH_SHORT).show();
                }
            });

            // Add text for a negative button
            theDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    Toast.makeText(getContext(), "The changes were not saved", Toast.LENGTH_SHORT).show();
                }
            });

            // Returns the created dialog
            theDialog.create().show();
        }
    }
}
