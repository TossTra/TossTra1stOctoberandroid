package com.app.tosstra.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.app.tosstra.fragments.dispacher.ChildActiveTruckFragment;
import com.app.tosstra.fragments.dispacher.SeniorityTruckFragment;

public class VPActiveTrucks extends FragmentStatePagerAdapter {
    public VPActiveTrucks(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
            fragment = new ChildActiveTruckFragment();
        else if (position == 1)
            fragment = new SeniorityTruckFragment();
        return fragment;    }

    @Override
    public int getCount() {
        return 2;
    }
    @Override
    public CharSequence getPageTitle(int position)
    {
        String title = null;
        if (position == 0)
            title = "All Truck";
        else if (position == 1)
            title = "Seniority Truck";
        return title;
    }

}
