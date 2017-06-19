package com.example.cldme.tabslearning;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;

import org.thermostatapp.util.*;

import static com.example.cldme.tabslearning.ProgramFragment.daySwitch;
import static com.example.cldme.tabslearning.ProgramFragment.stateSwitch;
import static com.example.cldme.tabslearning.ProgramFragment.timeSwitch;
import static com.example.cldme.tabslearning.ProgramFragment.wpg;
import static com.example.cldme.tabslearning.ProgramFragment.hasUpdated;

public class MainActivity extends AppCompatActivity {

    //Declare the customFragment variable to store the current fragment view
    private Fragment customFragment;

    //Declare the fragmentManager
    private FragmentManager fragmentManager;

    //Declare the fragmentTransaction
    private FragmentTransaction fragmentTransaction;

    //Store the activity of the app for future use
    Activity appActivity = this;

    //Flag for marking if the program fragment is active
    Boolean isProgramFragActive;

    //Flag for marking if the settings fragment is active
    Boolean isSettingsFragActive;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //Function for checking if the changes that were made in the week program had been sent to the server
                    if(isProgramFragActive)
                        checkWeekProgramStatus();
                    customFragment = HomeFragment.newInstance();
                    //Mark the program fragment as being inactive
                    isProgramFragActive = false;
                    isSettingsFragActive = false;
                    break;
                case R.id.navigation_program:
                    customFragment = ProgramFragment.newInstance();
                    //Mark the program fragment as being active
                    isProgramFragActive = true;
                    isSettingsFragActive = false;
                    break;
                case R.id.navigation_settings:
                    //Function for checking if the changes that were made in the week program had been sent to the server
                    if(isProgramFragActive)
                        checkWeekProgramStatus();
                    customFragment = SettingsFragment.newInstance();
                    //Mark the program fragment as being inactive
                    isProgramFragActive = false;
                    isSettingsFragActive = true;
                    break;
            }
            displayLayout(customFragment);
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Display the home page when the application is created (only done one time)
        displayLayout(HomeFragment.newInstance());

        //For selecting a menu item via a script use the following (2 - index of selected item)
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);

        //Configuring variables for communicating with the server
        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/58";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = HeatingSystem.BASE_ADDRESS + "/weekProgram";

        //Set the flag for the program fragment to false (since the app opens on the home fragment)
        isProgramFragActive = false;

        //Set the flag for the settings fragment to false (since the app opens on the home fragment)
        isSettingsFragActive = false;

        new Thread() {

            @Override
            public void run() {
                try {
                    //In here we can update the UI elements while retrieving new data from the server
                    while(!isInterrupted()) {
                        //Get all the information from the server
                        final String currentDay = HeatingSystem.get("day");
                        final String currentTime = HeatingSystem.get("time");
                        final String dayTempString = HeatingSystem.get("dayTemperature");
                        final String nightTempString = HeatingSystem.get("nightTemperature");
                        final String weekStateString = HeatingSystem.get("weekProgramState");
                        final Double currentTemperature = Double.parseDouble(HeatingSystem.get("currentTemperature"));
                        final Double targetTemperature = Double.parseDouble(HeatingSystem.get("targetTemperature"));

                        //When we update UI elements we use runOnUiThread
                        //(this thread runs all the time)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Set the text for different UI elements on the home screen

                                //Set the text for the day and time on the home page
                                HomeFragment.serverDay.setText(currentDay);
                                HomeFragment.serverTime.setText(currentTime);
                                //Set the text for the day and time on the program page (only if program fragment is active)
                                if(isProgramFragActive) {
                                    ProgramFragment.progServerDay.setText(currentDay);
                                    ProgramFragment.progServerTime.setText(currentTime);
                                }

                                //Set the text for the server day and time on the settings page(only if the settings page is active)
                                if(isSettingsFragActive) {
                                    SettingsFragment.serverDay.setHint(currentDay);
                                    SettingsFragment.serverTime.setHint(currentTime);
                                    SettingsFragment.settingsServerDay.setText(currentDay);
                                    SettingsFragment.settingsServerTime.setText(currentTime);
                                }

                                HomeFragment.currentTemp.setText(String.valueOf(currentTemperature) + " \u2103");
                                //targetTemp.setText(String.valueOf(targetTemperature) + " \u2103");

                                //Update the night/day temperatures input fields
                                HomeFragment.dayTemp.setHint(dayTempString + " \u2103");
                                HomeFragment.nightTemp.setHint(nightTempString + " \u2103");
                                HomeFragment.dayTemp.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                                HomeFragment.nightTemp.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

                                if(targetTemperature > currentTemperature) {
                                    HomeFragment.flameImage.setVisibility(View.VISIBLE);
                                    HomeFragment.snowflakeImage.setVisibility(View.GONE);
                                } else if(targetTemperature < currentTemperature) {
                                    HomeFragment.flameImage.setVisibility(View.GONE);
                                    HomeFragment.snowflakeImage.setVisibility(View.VISIBLE);
                                } else {
                                    HomeFragment.flameImage.setVisibility(View.GONE);
                                    HomeFragment.snowflakeImage.setVisibility(View.GONE);
                                }
                            }
                        });
                        //Wait some time until new information is generated on the server
                        Thread.sleep(100);
                    }
                } catch(Exception e) {
                    System.err.println("Error occured " + e);
                }
            }
        }.start();
    }

    public void displayLayout(Fragment displayFragment) {
        //Get a new fragmentManager
        fragmentManager = getFragmentManager();

        //Initialize a new transaction to be made
        fragmentTransaction = fragmentManager.beginTransaction();

        //Update the view with the fragment that needs to display
        fragmentTransaction.replace(R.id.content, displayFragment);

        //Commit the changes to the view
        fragmentTransaction.commit();
    }

    public void checkWeekProgramStatus() {
        if(ProgramFragment.hasChanged && !ProgramFragment.hasUpdated) {

            AlertDialog.Builder theDialog = new AlertDialog.Builder(appActivity);

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
                                    wpg.data.get(ProgramFragment.weekDays[ProgramFragment.currentDayIndex]).set(i, new Switch(daySwitch[i], stateSwitch[i], timeSwitch[i]));
                                    //Log.d("custom", daySwitch[i] + " " + stateSwitch[i] + " " + timeSwitch[i]);
                                }

                                //Check for duplicates (it should not happen)
                                //If it does do not update the program and promt the user with instructions
                                boolean duplicates = wpg.duplicates(wpg.data.get(ProgramFragment.weekDays[ProgramFragment.currentDayIndex]));
                                //If no duplicates are found, update the week program
                                if(!duplicates) {
                                    //Send the week program to be SAVED on the server
                                    HeatingSystem.setWeekProgram(wpg);
                                } else {
                                    Toast.makeText(getApplicationContext(), "There was an error with the program. Please check for duplicates", Toast.LENGTH_LONG);
                                }

                                //Mark that the program was updated on the server
                                hasUpdated = true;

                            } catch (Exception e) {

                            }
                        }
                    }).start();

                    Toast.makeText(getApplicationContext(), "The changes were saved", Toast.LENGTH_SHORT).show();
                }
            });

            // Add text for a negative button
            theDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    Toast.makeText(getApplicationContext(), "The changes were not saved", Toast.LENGTH_SHORT).show();
                }
            });

            // Returns the created dialog
            theDialog.create().show();
        }
    }
}