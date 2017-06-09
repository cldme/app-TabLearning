package com.example.cldme.tabslearning;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
                case R.id.navigation_dashboard:
                    customFragment = DashboardFragment.newInstance();
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
    }

    private void displayLayout(Fragment displayFragment) {
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
