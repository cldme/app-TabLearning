package com.example.cldme.tabslearning;

import android.app.Fragment;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by Claudiu Ion on 09/06/2017.
 */

public class HomeFragment extends Fragment implements View.OnClickListener {

    //Declare variables for the script
    //temp = 20 is the default temperature when the application is started
    private static double temp = 20;
    //tempMin is the minimum temperature allowed (0)
    private static double tempMin = 0;
    //tempMax is the maximum temperature allowed (30)
    private static double tempMax = 30;
    //step is the increments in which the we will update the temperature
    private static double tempStep = 0.1;

    //Declare variables for the different view items on the homeFragment page
    private static ImageButton plusButton;
    private static ImageButton minusButton;
    private static TextView tempText;

    //Declare variables for the flameImage and snowflakeImage
    private static ImageView flameImage;
    private static ImageView snowflakeImage;

    //Declare variable for the switch that enables / disables the week program
    private static Switch weekSwitch;

    //Declare variable for the seekbar that sets the temperature
    private static SeekBar seekBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.home_fragment, container, false);

        //Get different elements that are in the homeFragment
        plusButton = (ImageButton) view.findViewById(R.id.plus_button);
        minusButton = (ImageButton) view.findViewById(R.id.minus_button);
        tempText = (TextView) view.findViewById(R.id.temperature_text);

        //Get the week switch element from the fragment
        weekSwitch = (Switch) view.findViewById(R.id.week_switch);

        //Get the seekBar element from the fragment
        seekBar = (SeekBar) view.findViewById(R.id.temperature_seek_bar);

        //Get the flameImage and snowflakeImage icons
        flameImage = (ImageView) view.findViewById(R.id.flame_image);
        snowflakeImage = (ImageView) view.findViewById(R.id.snowflake_image);

        //Hide both icons when the fragment is created (because temperature has not been modified yet)
        flameImage.setVisibility(view.GONE);
        snowflakeImage.setVisibility(view.GONE);

        //Add action listeners to different elements that are in the homeFragment
        plusButton.setOnClickListener(this);
        minusButton.setOnClickListener(this);

        //Add action listener for the week switch
        weekSwitch.setOnCheckedChangeListener(switchWeekListener);

        //Set the maximum value for the seekBar
        seekBar.setMax((int)((tempMax - tempMin) / tempStep));
        //Also set the seekBar to the default temperature of 20 degress (when the app starts)
        updateSeekBar(temp);
        //Add action listener for the seekBar element
        seekBar.setOnSeekBarChangeListener(seekBarListener);

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
        //Switch determines which of the buttons from the homeFragment was clicked
        switch(view.getId()) {
            case R.id.plus_button:
                //Check if temp is above 0 and below 30, only then we update the temperature
                if(temp >= 0 && temp < 30) {
                    temp = temp + 0.11;
                    //Update the seekBar progress with the new temp
                    updateSeekBar(temp);
                }
                setTemp(temp);
                //Show flame image, when heating is increased
                showFlame(view);
                break;
            case R.id.minus_button:
                //Check if temp is above 0 and below 30, only then we update the temperature
                if(temp > 0 && temp <= 30) {
                    temp = temp - 0.09;
                    //Update the seekBar progress with the new temp
                    updateSeekBar(temp);
                }
                setTemp(temp);
                //Show snowflake image, when heating is decreased
                showSnowflake(view);
                break;
        }
    }

    public CompoundButton.OnCheckedChangeListener switchWeekListener =
            new CompoundButton.OnCheckedChangeListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            //Check the status of isChecked variable
            //If isChecked is true, then we are in vacation mode, we update the weekProgramText
            if(isChecked == true) {
                Toast.makeText(getContext(), "Week program is now disabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Week program is now enabled", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public SeekBar.OnSeekBarChangeListener seekBarListener =
            new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //Calculate the temperature from the seekBar then update the temperature text
            double value = (double)(tempMin + Math.round(progress) / 10.0);
            setTemp(value);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    public static double getTemp() {
        return temp;
    }

    public static void setTemp(double temp) {
        //Update both the variable temp and the textView temp
        HomeFragment.temp = temp;
        //Update the textView
        String customTemp = String.valueOf(temp) + " \u2103";
        tempText.setText(customTemp);
    }

    public static void updateSeekBar(double tempValue) {
        seekBar.setProgress((int)(tempValue / tempStep));
    }

    private void showFlame(View view) {
        snowflakeImage.setVisibility(view.GONE);
        flameImage.setVisibility(view.VISIBLE);
    }

    private void showSnowflake(View view) {
        flameImage.setVisibility(view.GONE);
        snowflakeImage.setVisibility(view.VISIBLE);
    }
}
