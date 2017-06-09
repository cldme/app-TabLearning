package com.example.cldme.tabslearning;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Claudiu Ion on 09/06/2017.
 */

public class HomeFragment extends Fragment implements View.OnClickListener {

    //Declare variables for the script
    //temp = 20 is the default temperature when the application is started
    private static double temp = 20;

    //Declare variables for the different items on the homeFragment page
    private static ImageButton plusButton;
    private static ImageButton minusButton;
    private static TextView tempText;

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

        //Add action listeners to different elements that are in the homeFragment
        plusButton.setOnClickListener(this);
        minusButton.setOnClickListener(this);

        return view;
    }

    public static HomeFragment newInstance() {
        //Create a new HomeFragment object
        HomeFragment homeFragment = new HomeFragment();

        //Return the newly created homeFragment object
        return homeFragment;
    }

    @Override
    public void onClick(View v) {
        //Switch determines which of the buttons from the homeFragment was clicked
        switch(v.getId()) {
            case R.id.plus_button:
                //Check if temp is above 0 and below 30, only then we update the temperature
                if(temp >= 0 && temp < 30) {
                    temp = Math.round((temp + 0.1) * 10.0) / 10.0;
                }
                setTemp(temp);
                break;
            case R.id.minus_button:
                //Check if temp is above 0 and below 30, only then we update the temperature
                if(temp > 0 && temp <= 30) {
                    temp = Math.round((temp - 0.1) * 10.0) / 10.0;
                }
                setTemp(temp);
                break;
        }
    }

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
}
