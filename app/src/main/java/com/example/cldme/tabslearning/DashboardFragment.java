package com.example.cldme.tabslearning;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Claudiu Ion on 09/06/2017.
 */

public class DashboardFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dashboard_fragment, container, false);
    }

    public static DashboardFragment newInstance() {
        //Create a new DashboardFragment object
        DashboardFragment dashboardFragment = new DashboardFragment();

        //Return the newly created dashboardFragment object
        return dashboardFragment;
    }
}
