package com.app.tosstra.fragments.dispacher;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.app.tosstra.activities.ActiveJobDetail;
import com.app.tosstra.activities.AppUtil;
import com.app.tosstra.activities.SigninActivity;
import com.app.tosstra.adapters.ChildActieTruckAdapter;
import com.app.tosstra.adapters.ListViewAdapter;
import com.app.tosstra.R;
import com.app.tosstra.interfaces.DriverIdNew;
import com.app.tosstra.interfaces.PassDriverIds;
import com.app.tosstra.interfaces.RerfreshListView;
import com.app.tosstra.models.AllDrivers;
import com.app.tosstra.models.GenricModel;
import com.app.tosstra.services.Interface;
import com.app.tosstra.utils.CommonUtils;
import com.app.tosstra.utils.PreferenceHandler;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.tosstra.adapters.ListViewAdapter.interestList_LV_new;

public class ListViewFragment extends Fragment implements View.OnClickListener {
    ListViewAdapter listViewAdapter;
    RecyclerView rvList;
    Button btn_end_job;
    public static List<String> new_interestList_LV = new ArrayList<>();
    public static List<String> new_interestList_LV_new = new ArrayList<>();
    String lst = "";
    String dri_lst;
    private TextView empty_view1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        initUI(view);
        if(interestList_LV_new!=null)
            interestList_LV_new.clear();
        hitActiveDriverAPI();
        return view;
    }

    private void initUI(View view) {
        empty_view1=view.findViewById(R.id.empty_view1);
        rvList = view.findViewById(R.id.rvList);
        btn_end_job = view.findViewById(R.id.btn_end_job);
        btn_end_job.setOnClickListener(this);
    }

    private void hitActiveDriverAPI() {
        final Dialog dialog = AppUtil.showProgress(getActivity());
        Interface service = CommonUtils.retroInit();
        Call<AllDrivers> call = service.active_drivers(PreferenceHandler.readString(getContext(), PreferenceHandler.USER_ID, ""));
        call.enqueue(new Callback<AllDrivers>() {
            @Override
            public void onResponse(Call<AllDrivers> call, Response<AllDrivers> response) {
                AllDrivers data = response.body();
                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {
                    dialog.dismiss();
                   // CommonUtils.showLongToast(getContext(), data.getMessage());
                    listViewAdapter = new ListViewAdapter(getContext(), data, passDriverIds,driverIdNew,RerfreshListView);
                    rvList.setLayoutManager(new LinearLayoutManager(getContext()));
                    rvList.setAdapter(listViewAdapter);
                    btn_end_job.setVisibility(View.VISIBLE);
                    rvList.setVisibility(View.VISIBLE);
                    empty_view1.setVisibility(View.GONE);
                } else {
                    btn_end_job.setVisibility(View.GONE);
                    rvList.setVisibility(View.GONE);
                    empty_view1.setVisibility(View.VISIBLE);
                    dialog.dismiss();
               //     CommonUtils.showLongToast(getContext(), data.getMessage());
                }
            }

            @Override
            public void onFailure(Call<AllDrivers> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(getContext(), t.getMessage());
            }
        });
    }

    /*@Override
    public void onResume() {
        super.onResume();
        hitActiveDriverAPI();
    }*/

    PassDriverIds passDriverIds = new PassDriverIds() {
        @Override
        public void selectedDriverIdList(List<String> interestList) {
            new_interestList_LV = interestList;
          //  CommonUtils.showSmallToast(getContext(), String.valueOf(new_interestList_LV.toString()));

        }
    };
    DriverIdNew driverIdNew = new DriverIdNew() {
        @Override
        public void selectedDriverIdList(List<String> drivernew) {
            new_interestList_LV_new=drivernew;
        }
    };


    private void hitEndJobAPI() {

        String mul_job_ids;
        lst = new_interestList_LV.toString();
        String StringAdd = lst.toString();
        int comma = StringAdd.lastIndexOf(',');
        String finalStr = lst.replaceAll("[\\[\\](){}(,)*$]", "");
        mul_job_ids = finalStr.replace(" ", ",");


        String mul_driver_id;
        dri_lst=new_interestList_LV_new.toString();
        String StringAdd_new = dri_lst.toString();
        int comma_new = StringAdd_new.lastIndexOf(',');
        String finalStr_new = dri_lst.replaceAll("[\\[\\](){}(,)*$]", "");
        mul_driver_id = finalStr_new.replace(" ", ",");


        Log.e("active_dri_id", mul_driver_id);
        Log.e("active_job_id", mul_job_ids);


        final Dialog dialog = AppUtil.showProgress(getActivity());
        Interface service = CommonUtils.retroInit();
        Call<GenricModel> call = service.end_driver_job(PreferenceHandler.readString(getContext(), PreferenceHandler.USER_ID, ""),
                mul_driver_id,mul_job_ids);
        call.enqueue(new Callback<GenricModel>() {
            @Override
            public void onResponse(Call<GenricModel> call, Response<GenricModel> response) {
                GenricModel data = response.body();
                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {
                    new_interestList_LV.clear();
                    new_interestList_LV_new.clear();
                    dialog.dismiss();
                    hitActiveDriverAPI();
                } else {
                    dialog.dismiss();
                    // CommonUtils.showLongToast(context, data.getMessage() + ChildActieTruckAdapter.this.driver_id);
                }
            }

            @Override
            public void onFailure(Call<GenricModel> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(getActivity(), t.getMessage());
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_end_job:
                if (new_interestList_LV.size() == 0) {
                    CommonUtils.showSmallToast(getContext(), "Please select atleast one driver to end job");
                } else {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    // alertDialog.setTitle("NKA SERVICE");
                    alertDialog.setMessage("Are you sure you want to end this job?");
                    alertDialog.setIcon(R.mipmap.call_icon);
                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            hitEndJobAPI();
                        }
                    });
                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();

                }
                break;

        }
    }


    @Override
    public void onResume() {
        super.onResume();
        hitActiveDriverAPI();
    }

    RerfreshListView RerfreshListView=new RerfreshListView() {
        @Override
        public void refresh(String job, String dri, String dis) {
           // hitActiveDriverAPI();
              /* Intent i = new Intent(getContext(), ActiveJobDetail.class);
                    i.putExtra("job_id1", job);
                    i.putExtra("dis_id1", dri);
                    i.putExtra("driver_id", dis);
                 //   Log.e("driver_id", data.getData().get(pos).getDriverId());
                   startActivity(i);*/
        }
    };
}
