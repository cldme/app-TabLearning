package com.example.cldme.tabslearning;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private LinearLayout homeLayout;
    private LinearLayout dashboardLayout;
    private LinearLayout notificationsLayout;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    displayHomeLayout();
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    displayDashboardLayout();
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    displayNotificationsLayout();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        homeLayout = (LinearLayout) findViewById(R.id.home_layout);
        dashboardLayout = (LinearLayout) findViewById(R.id.dashboard_layout);
        notificationsLayout = (LinearLayout) findViewById(R.id.notifications_layout);

        displayHomeLayout();
    }

    private void displayHomeLayout() {

        dashboardLayout.setVisibility(View.GONE);
        notificationsLayout.setVisibility(View.GONE);

        homeLayout.setVisibility(View.VISIBLE);
    }

    private void displayDashboardLayout() {

        notificationsLayout.setVisibility(View.GONE);
        homeLayout.setVisibility(View.GONE);

        dashboardLayout.setVisibility(View.VISIBLE);
    }

    private void displayNotificationsLayout() {

        dashboardLayout.setVisibility(View.GONE);
        homeLayout.setVisibility(View.GONE);

        notificationsLayout.setVisibility(View.VISIBLE);
    }

}
