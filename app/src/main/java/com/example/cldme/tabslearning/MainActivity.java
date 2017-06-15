package com.example.cldme.tabslearning;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import org.thermostatapp.util.*;

import java.net.ConnectException;

public class MainActivity extends AppCompatActivity {

    //Declare the customFragment variable to store the current fragment view
    private Fragment customFragment;

    //Declare the fragmentManager
    private FragmentManager fragmentManager;

    //Declare the fragmentTransaction
    private FragmentTransaction fragmentTransaction;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    customFragment = HomeFragment.newInstance();
                    break;
                case R.id.navigation_program:
                    customFragment = ProgramFragment.newInstance();
                    break;
                case R.id.navigation_notifications:
                    customFragment = NotificationsFragment.newInstance();
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
                                HomeFragment.serverDay.setText(currentDay);
                                HomeFragment.serverTime.setText(currentTime);

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
}