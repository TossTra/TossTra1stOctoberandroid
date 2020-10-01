package com.app.tosstra.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tosstra.R;
import com.app.tosstra.activities.AppUtil;
import com.app.tosstra.interfaces.PassDriverIds;
import com.app.tosstra.interfaces.RefreshDriverList;
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

public class ChildActieTruckAdapter extends RecyclerView.Adapter<ChildActieTruckAdapter.ViewHolder> {
    Activity context;
    AllDrivers data;
    String driver_id;
    public static List<String> interestList=new ArrayList<>();


    RefreshDriverList refreshDriverList;
    PassDriverIds passDriverIds;


    public ChildActieTruckAdapter(Activity context, AllDrivers data,RefreshDriverList refreshDriverList,PassDriverIds passDriverIds) {
        this.context = context;
        this.data = data;
        this.refreshDriverList=refreshDriverList;
        this.passDriverIds=passDriverIds;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_truck_item, parent, false);
        return new ChildActieTruckAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_header1.setText(data.getData().get(position).getCompanyName());
        holder.tv_header2.setText(data.getData().get(position).getFirstName() + " " + data.getData().get(position).getLastName());
    }


    @Override
    public int getItemCount() {
        return data.getData().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_header1, tv_header2;
        private ImageView iv_fv;
        String id;
        String driver_multi_id;
        CheckBox chkBkHrt;

        public ViewHolder(View v) {
            super(v);
            tv_header1 = v.findViewById(R.id.tv_header1);
            tv_header2 = v.findViewById(R.id.tv_header2);
            iv_fv = v.findViewById(R.id.iv_fv);

            CheckBox chkBkHrt = v.findViewById(R.id.chkBkHrt);
            //chkBkHrt.setOnClickListener(this);
            iv_fv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    driver_id = data.getData().get(getAdapterPosition()).getId();
                    hitFavUnFav(driver_id);
                }
            });

            chkBkHrt.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
                    driver_id = data.getData().get(getAdapterPosition()).getId();
                    boolean checked = ((CheckBox) v).isChecked();
                    if (checked) {
                        interestList.add(driver_id+",");
                        passDriverIds.selectedDriverIdList(interestList);
                    } else {
                        interestList.remove(driver_id+",");
                        passDriverIds.selectedDriverIdList(interestList);
                    }
                }
            });
        }




    }

    private void hitFavUnFav(String dri_id) {
        final Dialog dialog = AppUtil.showProgress(context);
        Interface service = CommonUtils.retroInit();
        Call<GenricModel> call = service.favUnFav(PreferenceHandler.readString(context, PreferenceHandler.USER_ID, ""), dri_id);
        call.enqueue(new Callback<GenricModel>() {
            @Override
            public void onResponse(Call<GenricModel> call, Response<GenricModel> response) {
                GenricModel data = response.body();
                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {
                    dialog.dismiss();
                    CommonUtils.showLongToast(context, data.getMessage());
                    refreshDriverList.favClick(driver_id);
                } else {
                    dialog.dismiss();
                    CommonUtils.showLongToast(context, data.getMessage());
                }
            }

            @Override
            public void onFailure(Call<GenricModel> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(context, t.getMessage());
            }
        });
    }
}
