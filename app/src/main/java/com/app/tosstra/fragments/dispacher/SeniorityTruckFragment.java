package com.app.tosstra.fragments.dispacher;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.tosstra.activities.AddANewJobActivity;
import com.app.tosstra.activities.AppUtil;
import com.app.tosstra.adapters.SeniorityAdapter;
import com.app.tosstra.R;
import com.app.tosstra.interfaces.PassDriverIds;
import com.app.tosstra.interfaces.RefreshDriverList;
import com.app.tosstra.models.AllDrivers;
import com.app.tosstra.models.GenricModel;
import com.app.tosstra.services.Interface;
import com.app.tosstra.utils.CommonUtils;
import com.app.tosstra.utils.MyGps;
import com.app.tosstra.utils.PreferenceHandler;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SeniorityTruckFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    RecyclerView rvSeniority;
    SeniorityAdapter seniorityAdapter;
    FloatingActionButton fab;
    private SwipeRefreshLayout swiperefresh;
    private TextView tvEmptyView, tvSelected;
    public static List<String> new_interestList_seniority = new ArrayList<>();
    Dialog dialog;
    private MyGps gpsTracker;
    private String lat,lon;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seniority_truck, container, false);
        init(view);

        hitFavAPI(refreshDriverList, "onCreate");
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (new_interestList_seniority != null) {
                new_interestList_seniority.clear();
                tvSelected.setText("Total " + "0" + " Selected");
            }
            hitFavAPI(refreshDriverList, "onCreate");
        }
    }

    private void init(View view) {
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        rvSeniority = view.findViewById(R.id.rvSeniority);
        swiperefresh = view.findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(this);
        tvEmptyView = view.findViewById(R.id.empty_view1);
        tvSelected = view.findViewById(R.id.tvSelected);
        if (new_interestList_seniority != null) {
            new_interestList_seniority.clear();
            tvSelected.setText("Total " + "0" + " Selected");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (new_interestList_seniority != null)
                    if (new_interestList_seniority.size() == 0) {
                        CommonUtils.showSmallToast(getContext(), "Please select at least one driver");
                    } else {
                        Intent i = new Intent(getContext(), AddANewJobActivity.class);
                        i.putExtra("f_type", "sen");
                        startActivityForResult(i, 1);
                    }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data != null) {
                String sen = data.getStringExtra("refresh_allTruck");
                if (sen.equalsIgnoreCase("1")) {
                    hitFavAPI(refreshDriverList, "hjks");
                    if (new_interestList_seniority != null) {
                        new_interestList_seniority.clear();
                        tvSelected.setText("Total " + "0" + " Selected");
                    }
                }
            }
        }
    }
    public void getLocation(){
        gpsTracker = new MyGps(getContext());
        if(gpsTracker.canGetLocation()){
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            lat=(String.valueOf(latitude));
            lon=(String.valueOf(longitude));
        }else{
            gpsTracker.showSettingsAlert();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        gpsTracker.stopUsingGPS();
    }

    private void hitFavAPI(final RefreshDriverList refreshDriverList, String key) {
        getLocation();
        if (key.equalsIgnoreCase("onCreate")) {
            dialog = AppUtil.showProgress(getActivity());
        }
        Interface service = CommonUtils.retroInit();
        Call<AllDrivers> call = service.onlyFav(PreferenceHandler.readString(getContext(), PreferenceHandler.USER_ID, ""),lat,lon);
        call.enqueue(new Callback<AllDrivers>() {
            @Override
            public void onResponse(Call<AllDrivers> call, Response<AllDrivers> response) {
                AllDrivers data = response.body();
                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {

                    swiperefresh.setRefreshing(false);
                    dialog.dismiss();
                    //   CommonUtils.showLongToast(getContext(), data.getMessage());
                    rvSeniority.setVisibility(View.VISIBLE);
                    tvEmptyView.setVisibility(View.GONE);
                    seniorityAdapter = new SeniorityAdapter(getActivity(), data, refreshDriverList, passDriverIds);
                    rvSeniority.setLayoutManager(new LinearLayoutManager(getContext()));
                    rvSeniority.setAdapter(seniorityAdapter);
                } else {
                    dialog.dismiss();
                    tvEmptyView.setVisibility(View.VISIBLE);
                    rvSeniority.setVisibility(View.GONE);
                    swiperefresh.setRefreshing(false);
                    //  CommonUtils.showLongToast(getContext(), data.getMessage());
                }
            }

            @Override
            public void onFailure(Call<AllDrivers> call, Throwable t) {
                dialog.dismiss();
                swiperefresh.setRefreshing(false);
                CommonUtils.showSmallToast(getContext(), t.getMessage());
            }
        });
    }

    private void hitFavUnFav(String dri_id) {

        final Dialog dialog = AppUtil.showProgress(getActivity());
        Interface service = CommonUtils.retroInit();
        Call<GenricModel> call = service.favUnFav(PreferenceHandler.readString(getContext(), PreferenceHandler.USER_ID, ""), dri_id);
        call.enqueue(new Callback<GenricModel>() {
            @Override
            public void onResponse(Call<GenricModel> call, Response<GenricModel> response) {
                GenricModel data = response.body();
                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {
                    CommonUtils.showLongToast(getContext(), data.getMessage());
                    hitFavAPI(refreshDriverList, "hjkl");
                    //refreshDriverList.favClick(driver_id);
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    CommonUtils.showLongToast(getContext(), data.getMessage());
                }
            }

            @Override
            public void onFailure(Call<GenricModel> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(getContext(), t.getMessage());
            }
        });
    }

    @Override
    public void onRefresh() {
        if (new_interestList_seniority != null) {
            new_interestList_seniority.clear();
            tvSelected.setText("Total " + "0" + " Selected");
        }
        hitFavAPI(refreshDriverList, "onFav");
    }

    PassDriverIds passDriverIds = new PassDriverIds() {
        @Override
        public void selectedDriverIdList(List<String> interestList) {
            new_interestList_seniority = interestList;
            String s = String.valueOf(new_interestList_seniority.size());
            tvSelected.setText("Total " + s + " Selected");
        }
    };


    RefreshDriverList refreshDriverList = new RefreshDriverList() {
        @Override
        public void favClick(String driver_id) {
            // hitFavAPI(refreshDriverList, "fav");
            hitFavUnFav(driver_id);
        }
    };
}