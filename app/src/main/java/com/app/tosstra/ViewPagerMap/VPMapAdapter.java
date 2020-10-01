package com.app.tosstra.ViewPagerMap;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.app.tosstra.ViewPagerHome.ViewPagerFragment;
import com.app.tosstra.models.AllDrivers;
import com.app.tosstra.models.AllJobsToDriver;

public class VPMapAdapter extends FragmentStatePagerAdapter {

    FragmentManager fm;
    AllDrivers data;
    public VPMapAdapter(@NonNull FragmentManager fm, AllDrivers data) {
        super(fm);
        this.fm=fm;
        this.data=data;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return ViewPagerMapFragment.newInstance(position,data);
    }

    @Override
    public int getCount() {
        if(data.getData()!=null)
            return data.getData().size();
        else
            return 0;
    }
}
