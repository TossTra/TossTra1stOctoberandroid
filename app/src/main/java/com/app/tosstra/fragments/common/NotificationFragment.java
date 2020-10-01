package com.app.tosstra.fragments.common;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.tosstra.activities.AppUtil;
import com.app.tosstra.activities.ChangePasswordActivity;
import com.app.tosstra.activities.MainActivity;
import com.app.tosstra.adapters.RVnotificationAdapter;
import com.app.tosstra.R;
import com.app.tosstra.models.GenricModel;
import com.app.tosstra.models.NotificationModel;
import com.app.tosstra.services.Interface;
import com.app.tosstra.utils.CommonUtils;
import com.app.tosstra.utils.PreferenceHandler;

import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NotificationFragment extends Fragment {
    private RecyclerView rvNotification;
    RVnotificationAdapter rVnotificationAdapter;
    String type;
    private TextView empty_view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_notification, container, false);;
        rvNotification=view.findViewById(R.id.rvNotification);
        empty_view=view.findViewById(R.id.empty_view);
        type = PreferenceHandler.readString(getActivity(), "userType", "");
        if(type.equalsIgnoreCase("Dispatcher")){
            hitDisNotification();
        }else {
            hitNotification();
        }
        return view;
    }

    private void hitDisNotification() {
        final Dialog dialog = AppUtil.showProgress(getActivity());
        Interface service = CommonUtils.retroInit();
        Call<NotificationModel> call = service.noti_dis(PreferenceHandler.readString(
                getContext(),PreferenceHandler.USER_ID,""));
        call.enqueue(new Callback<NotificationModel>() {
            @Override
            public void onResponse(Call<NotificationModel> call, Response<NotificationModel> response) {
                NotificationModel data = response.body();
                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {
                    dialog.dismiss();
                    Collections.reverse(data.getData());
                    //    CommonUtils.showLongToast(getContext(), data.getMessage());
                    rVnotificationAdapter=new RVnotificationAdapter(getContext(),data);
                    rvNotification.setLayoutManager(new LinearLayoutManager(getContext()));
                    rvNotification.setAdapter(rVnotificationAdapter);
                    empty_view.setVisibility(View.GONE);
                    rvNotification.setVisibility(View.VISIBLE);
                } else {
                    empty_view.setVisibility(View.VISIBLE);
                    rvNotification.setVisibility(View.GONE);
                    dialog.dismiss();
                    //  CommonUtils.showLongToast(getContext(), data.getMessage());
                }
            }

            @Override
            public void onFailure(Call<NotificationModel> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(getContext(), t.getMessage());
            }
        });
    }

    private void hitNotification() {
        final Dialog dialog = AppUtil.showProgress(getActivity());
        Interface service = CommonUtils.retroInit();
        Call<NotificationModel> call = service.noti(PreferenceHandler.readString(
                getContext(),PreferenceHandler.USER_ID,""));
        call.enqueue(new Callback<NotificationModel>() {
            @Override
            public void onResponse(Call<NotificationModel> call, Response<NotificationModel> response) {
                NotificationModel data = response.body();
                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {
                    dialog.dismiss();
                    Collections.reverse(data.getData());
                //    CommonUtils.showLongToast(getContext(), data.getMessage());
                    rVnotificationAdapter=new RVnotificationAdapter(getContext(),data);
                    rvNotification.setLayoutManager(new LinearLayoutManager(getContext()));
                    rvNotification.setAdapter(rVnotificationAdapter);
                    empty_view.setVisibility(View.GONE);
                    rvNotification.setVisibility(View.VISIBLE);
                } else {
                    dialog.dismiss();
                    empty_view.setVisibility(View.VISIBLE);
                    rvNotification.setVisibility(View.GONE);
                  //  CommonUtils.showLongToast(getContext(), data.getMessage());
                }
            }

            @Override
            public void onFailure(Call<NotificationModel> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(getContext(), t.getMessage());
            }
        });
    }
}