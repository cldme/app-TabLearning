package com.example.cldme.tabslearning;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;
import org.thermostatapp.util.HeatingSystem;

/**
 * Created by Claudiu Ion on 09/06/2017.
 */

public class HomeFragment extends Fragment implements View.OnClickListener {

    public static ImageButton plusButton, minusButton;
    public static ImageView flameImage, snowflakeImage;
    public static TextView serverDay, serverTime;
    public static TextView currentTemp, targetTemp;
    public static TextView dayTemp, nightTemp;
    public static Switch weekSwitch;
    public static SeekBar seekBar;

    private Double currentTempVal, targetTempVal;
    private String weekStateString;

    private static Double tempMin = 5.0;
    private static Double tempMax = 30.0;

    //Store the context of the current fragment for future use
    private Context viewContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.home_fragment, container, false);
        viewContext = view.getContext();

        plusButton = (ImageButton) view.findViewById(R.id.plus_button);
        minusButton = (ImageButton) view.findViewById(R.id.minus_button);
        flameImage = (ImageView) view.findViewById(R.id.flame_image);
        snowflakeImage = (ImageView) view.findViewById(R.id.snowflake_image);
        serverDay = (TextView) view.findViewById(R.id.server_day);
        serverTime = (TextView) view.findViewById(R.id.server_time);
        currentTemp = (TextView) view.findViewById(R.id.current_temperature);
        targetTemp = (TextView) view.findViewById(R.id.target_temperature);
        dayTemp = (TextView) view.findViewById(R.id.day_temperature);
        nightTemp = (TextView) view.findViewById(R.id.night_temperature);
        seekBar = (SeekBar) view.findViewById(R.id.temperature_seek_bar);
        weekSwitch = (Switch) view.findViewById(R.id.week_switch);

        //Add on click listeners to different UI elements present in the home fragment
        plusButton.setOnClickListener(this);
        minusButton.setOnClickListener(this);
        dayTemp.setOnClickListener(dayTempChange);
        nightTemp.setOnClickListener(nightTempChange);

        //Add action listener for the week switch
        weekSwitch.setOnCheckedChangeListener(switchWeekListener);

        //Configuring variables for communicating with the server
        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/58";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = HeatingSystem.BASE_ADDRESS + "/weekProgram";

        Thread setup = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Get all the information from the server
                    final Double currentTemperature = Double.parseDouble(HeatingSystem.get("currentTemperature"));
                    final Double targetTemperature = Double.parseDouble(HeatingSystem.get("targetTemperature"));
                    final String dayTempString = HeatingSystem.get("dayTemperature");
                    final String nightTempString = HeatingSystem.get("nightTemperature");
                    weekStateString = HeatingSystem.get("weekProgramState");

                    //Update the main variable for the current and target temperatures
                    currentTempVal = currentTemperature;
                    targetTempVal = targetTemperature;

                } catch (Exception e) {
                    System.err.println("Error occurred " + e);
                }
            }
        });

        //Start the setup thread and then join it with the main thread
        //This is done so we can wait for the thread to initialize the temperature values (got from the server)
        setup.start();

        try {

            //Wait for the setup thread to finish then continue with the program
            setup.join();

        } catch (Exception e) {
            System.err.print("Error occurred " + e);
        }

        //-------------------- FROM HERE WE CAN USE targetTemp and currentTemp VALUES --------------------\\

        /*
        Log.d("message", "current temp: " + String.valueOf(currentTempVal));
        Log.d("message", "target temp: " + String.valueOf(currentTempVal));
        */

        //SeekBar maximum can be 250 (specified in the app requirements)
        seekBar.setMax(250);
        //Set the seekBar listener
        seekBar.setOnSeekBarChangeListener(seekBarListener);
        //Set the progress of the seekBar
        updateSeekBar(targetTempVal);

        //Set the week program state switch based on the valued retrieved from the server
        if(weekStateString.equals("on")) {
            weekSwitch.setChecked(false);
        } else {
            weekSwitch.setChecked(true);
        }

        return view;
    }

    public static HomeFragment newInstance() {
        //Create a new HomeFragment object
        HomeFragment homeFragment = new HomeFragment();

        //Return the newly created homeFragment object
        return homeFragment;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.plus_button:
                addTemperature();
                break;
            case R.id.minus_button:
                decreaseTemperature();
                break;
        }
    }

    //Call this function each time the plus/minus buttons are pressed
    public void updateSeekBar(double temp) {
        seekBar.setProgress((int)((temp / 0.1) - tempMin * 10));
    }

    public SeekBar.OnSeekBarChangeListener seekBarListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    //Calculate the temperature from the seekBar then update the temperature text
                    double temp = (double)(tempMin + Math.round(progress) / 10.0);
                    //Update the targetTemp variable (!important)
                    targetTempVal = temp;
                    setTemp(temp);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
    };

    private void addTemperature() {
        //Check if temp is above 5 and below 30, only then we update the temperature
        if(targetTempVal >= 5 && targetTempVal < 30) {
            targetTempVal = Math.round((targetTempVal + 0.1) * 10.0) / 10.0 ;
            setTemp(targetTempVal);
            //Update the seekBar progress with the new temp
            updateSeekBar(targetTempVal);
        }
    }

    private void decreaseTemperature() {
        //Check if temp is above 5 and below 30, only then we update the temperature
        if(targetTempVal > 5 && targetTempVal <= 30) {
            targetTempVal = targetTempVal - 0.09;
            setTemp(targetTempVal);
            //Update the seekBar progress with the new temp
            updateSeekBar(targetTempVal);
        }
    }

    //Update the target temperature both on the server and in the fragment view
    private void setTemp(final double temp) {

        targetTemp.setText(String.valueOf(temp) + " \u2103");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HeatingSystem.put("targetTemperature", String.valueOf(temp));
                } catch(Exception e) {
                    System.err.println("Error occurred " + e);
                }
            }
        }).start();
    }

    public CompoundButton.OnCheckedChangeListener switchWeekListener =
            new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Check the status of isChecked variable
                //If isChecked is true, then we are in vacation mode, we update the weekProgramText
                if(isChecked == true) {
                    Toast.makeText(getContext(), "Week program is now disabled", Toast.LENGTH_SHORT).show();
                    //If manual mode is enabled, week program is off
                    weekStateString = "off";
                } else {
                    Toast.makeText(getContext(), "Week program is now enabled", Toast.LENGTH_SHORT).show();
                    //If manual mode is disabled, week program is on
                    weekStateString = "on";
                }

                //Update the week program state on the server
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HeatingSystem.put("weekProgramState", weekStateString);
                        } catch (Exception e) {
                            System.err.println("Error occurred " + e);
                        }
                    }
                }).start();
            }
    };

    //onClickListener for updating the day temperature
    public View.OnClickListener dayTempChange =
            new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    final Dialog tempDialog = new Dialog(viewContext);

                    tempDialog.setContentView(R.layout.temperature_dialog);

                    Button okButton = (Button) tempDialog.findViewById(R.id.temp_ok_button);
                    Button cancelButton = (Button) tempDialog.findViewById(R.id.temp_cancel_button);
                    final EditText newTemp = (EditText) tempDialog.findViewById(R.id.new_temperature);

                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Check if the newTemp field is not empty
                            if(newTemp.getText().toString().length() > 0) {
                                final String newTempString = String.valueOf(newTemp.getText());

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            HeatingSystem.put("dayTemperature", newTempString);
                                        } catch(Exception e) {
                                            System.err.println("Error occurred " + e);
                                        }
                                    }
                                }).start();
                            }

                            //Close the temperature dialog
                            tempDialog.dismiss();
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
    };

    //onClickListener for updating the night temperature
    public View.OnClickListener nightTempChange =
            new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    final Dialog tempDialog = new Dialog(viewContext);

                    tempDialog.setContentView(R.layout.temperature_dialog);

                    Button okButton = (Button) tempDialog.findViewById(R.id.temp_ok_button);
                    Button cancelButton = (Button) tempDialog.findViewById(R.id.temp_cancel_button);
                    final EditText newTemp = (EditText) tempDialog.findViewById(R.id.new_temperature);

                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Check if the newTemp field is not empty
                            if(newTemp.getText().toString().length() > 0) {
                                final String newTempString = String.valueOf(newTemp.getText());

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            HeatingSystem.put("nightTemperature", newTempString);
                                        } catch(Exception e) {
                                            System.err.println("Error occurred " + e);
                                        }
                                    }
                                }).start();
                            }

                            //Close the temperature dialog
                            tempDialog.dismiss();
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
    };

}
