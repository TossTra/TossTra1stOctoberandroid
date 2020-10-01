package com.app.tosstra.fragments.dispacher;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.tosstra.adapters.VPActiveDriver;
import com.app.tosstra.R;
import com.app.tosstra.utils.CommonUtils;
import com.google.android.material.tabs.TabLayout;

import static com.app.tosstra.activities.MainActivity.refresh_active;


public class ActiverDriverFragments extends Fragment {

    private ViewPager view_pager;
    private VPActiveDriver viewPagerAdapter;
    private TabLayout tablayout;
    String map;

    public ActiverDriverFragments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_activer_driver_fragments, container, false);
        if (getArguments() != null) {
            if (getArguments().getString("map") != null) {
                map = getArguments().getString("map");
            }
        }
        view_pager = view.findViewById(R.id.view_pager);
        viewPagerAdapter = new VPActiveDriver(getFragmentManager());
        view_pager.setAdapter(viewPagerAdapter);
        tablayout = view.findViewById(R.id.tablayout);
        tablayout.setupWithViewPager(view_pager);

        if (map != null) {
            if (map.equalsIgnoreCase("1")) {
                view_pager.setCurrentItem(1, false);
            }
        }

        if (view_pager.getCurrentItem() == 0) {
            refresh_active.setVisibility(View.GONE);
        }
        if (view_pager.getCurrentItem() == 1) {
            refresh_active.setVisibility(View.VISIBLE);
        }

        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tablayout.getTabAt(0).isSelected()) {
                    refresh_active.setVisibility(View.GONE);
                }
                if (tablayout.getTabAt(1).isSelected()) {
                    refresh_active.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }
}