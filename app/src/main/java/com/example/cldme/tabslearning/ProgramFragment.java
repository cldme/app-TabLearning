package com.example.cldme.tabslearning;

import android.app.Fragment;
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

public class ProgramFragment extends Fragment {

    //Declare variables for the different view items on the programFragment page
    ListView itemList;
    Button addButton;
    Button saveButton;
    Button resetButton;

    //Declare the switch array list
    ArrayList<Switch> switchArrayList = new ArrayList<Switch>();
    //Make a custom switch for testing
    Switch customSwitch = new Switch("day", false, "10:00");

    //Declare the adapter for the switches
    CustomArrayAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.program_fragment, container, false);

        //Get the different elements that are in the programFragment
        addButton = (Button) view.findViewById(R.id.add_button);
        saveButton = (Button) view.findViewById(R.id.save_button);
        resetButton = (Button) view.findViewById(R.id.reset_button);
        itemList = (ListView) view.findViewById(R.id.list_view);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Get the week program
                    WeekProgram wpg = HeatingSystem.getWeekProgram();

                    switchArrayList = wpg.data.get("Monday");

                    Log.d("message", "switchArrayList Created");

                } catch(Exception e) {

                }
            }
        });

        thread.start();

        try {
            //Wait for the week program to be pulled from the servers
            thread.join();

            //Configure the array adapter
            adapter = new CustomArrayAdapter(view.getContext(), switchArrayList);

            //Set the list adapter for the listItems
            itemList.setAdapter(adapter);

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

            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultWeekProgram();
            }
        });

        return view;
    }

    public static ProgramFragment newInstance() {
        //Create a new DashboardFragment object
        ProgramFragment programFragment = new ProgramFragment();

        //Return the newly created dashboardFragment object
        return programFragment;
    }

    public static void setDefaultWeekProgram() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /* Uncomment the following parts to see how to work with the properties of the week program */
                    // Get the week program
                    WeekProgram wpg = HeatingSystem.getWeekProgram();
                    // Set the week program to default
                    wpg.setDefault();
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
