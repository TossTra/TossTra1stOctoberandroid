package com.app.tosstra.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.app.tosstra.activities.AppUtil;
import com.app.tosstra.activities.JobDetailActivity;
import com.app.tosstra.R;
import com.app.tosstra.activities.JobOfferForMyJob;
import com.app.tosstra.interfaces.RefreshMyJobs;
import com.app.tosstra.interfaces.StartJobInterface;
import com.app.tosstra.models.AllJobsToDriver;
import com.app.tosstra.models.GenricModel;
import com.app.tosstra.services.Interface;
import com.app.tosstra.utils.CommonUtils;
import com.app.tosstra.utils.PreferenceHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RVMyJobAdapter extends RecyclerView.Adapter<RVMyJobAdapter.MyJobAdapterHolder> {
    FragmentActivity context;
    AllJobsToDriver data;
    String job_id,disp_id,dri_id;
    RefreshMyJobs refreshMyJobs;
    public static String Job_id_static,dis_id_static;
    StartJobInterface startJobInterface;


    public RVMyJobAdapter(FragmentActivity context, AllJobsToDriver data, RefreshMyJobs refreshMyJobs, StartJobInterface startJobInterface) {
        this.context = context;
        this.data = data;
        this.refreshMyJobs=refreshMyJobs;
        this.startJobInterface=startJobInterface;
    }

    @NonNull
    @Override
    public MyJobAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_job_item, parent, false);
        return new MyJobAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyJobAdapterHolder holder, int position) {
        holder.tv_header1.setText(data.getData().get(position).getFirstName() + " " + data.getData().get(position).getLastName());
        holder.tv_header2.setText(data.getData().get(position).getCompanyName());
        if (data.getData().get(position).getWorkStartStatus().equalsIgnoreCase("1")) {
            holder.bt_start.setText("Started");
        } else {
            holder.bt_start.setText("Start");
            holder.bt_start.setTextColor(context.getResources().getColor(R.color.design_default_color_primary_dark));
            holder.bt_start.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.rounded_blue_stroke));
        }
    }

    @Override
    public int getItemCount() {
        if (data.getData() != null)
            return data.getData().size();
        else
            return 0;
    }

    public class MyJobAdapterHolder extends RecyclerView.ViewHolder {
        TextView tv_header1, tv_header2;
        Button bt_start;
        RelativeLayout rl_top;

        public MyJobAdapterHolder(@NonNull View itemView) {
            super(itemView);
            tv_header1 = itemView.findViewById(R.id.tv_header1);
            tv_header2 = itemView.findViewById(R.id.tv_header2);
            bt_start = itemView.findViewById(R.id.bt_start);
            rl_top=itemView.findViewById(R.id.rl_top);
            rl_top.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(bt_start.getText().toString().equalsIgnoreCase("Started")){
                        job_id=data.getData().get(getAdapterPosition()).getJobId();
                        disp_id=data.getData().get(getAdapterPosition()).getDispatcherId();
                        dri_id=data.getData().get(getAdapterPosition()).getDriverId();
                        Intent i=new Intent(context,JobDetailActivity.class);
                        i.putExtra("job_id",job_id);
                        i.putExtra("disp_id",disp_id);
                        i.putExtra("dri_id",dri_id);
                        i.putExtra("status_accept","1");
                        context.startActivity(i);
                       // CommonUtils.showSmallToast(context,job_id);
                    }else {
                        job_id=data.getData().get(getAdapterPosition()).getJobId();
                        disp_id=data.getData().get(getAdapterPosition()).getDispatcherId();
                        dri_id=data.getData().get(getAdapterPosition()).getDriverId();
                        Intent i=new Intent(context, JobOfferForMyJob.class);
                        i.putExtra("job_id",job_id);
                        i.putExtra("disp_id",disp_id);
                        i.putExtra("dri_id",dri_id);
                        i.putExtra("status_accept","1");
                        context.startActivity(i);
                        //CommonUtils.showSmallToast(context,job_id);
                    }

                }
            });

            bt_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(bt_start.getText().toString().equalsIgnoreCase("Started"))
                    {
                        String job_id,disp_id,dri_id;
                        job_id=data.getData().get(getAdapterPosition()).getJobId();
                        disp_id=data.getData().get(getAdapterPosition()).getDispatcherId();
                        dri_id=data.getData().get(getAdapterPosition()).getDriverId();
                        Intent i=new Intent(context,JobDetailActivity.class);
                        i.putExtra("job_id",job_id);
                        i.putExtra("disp_id",disp_id);
                        i.putExtra("dri_id",dri_id);
                        context.startActivity(i);
                    }else {

                        startJobInterface.startjobs(getAdapterPosition(),data.getData().get(getAdapterPosition()).getJobId());

//                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
//                        // alertDialog.setTitle("NKA SERVICE");
//                        alertDialog.setMessage("Are you sure you want to start this job?");
//                        alertDialog.setIcon(R.mipmap.call_icon);
//                        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                String job_id;
//                                job_id=data.getData().get(getAdapterPosition()).getJobId();
//                                hitStartJobAPI(job_id);
//                            }
//                        });
//                        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });
//                        alertDialog.show();
//
//
                   }


                }
            });
        }
    }


    private void hitStartJobAPI(String job_d) {
        final Dialog dialog = AppUtil.showProgress(context);
        Interface service = CommonUtils.retroInit();
        Call<GenricModel> call = service.start_job(PreferenceHandler.readString(context, PreferenceHandler.USER_ID, ""),
                job_d);
        call.enqueue(new Callback<GenricModel>() {
            @Override
            public void onResponse(Call<GenricModel> call, Response<GenricModel> response) {
                GenricModel data_start = response.body();
                assert data_start != null;
                if (data_start.getCode().equalsIgnoreCase("201")) {
                    dialog.dismiss();
                    Job_id_static=job_id;
                    dis_id_static=disp_id;
                    refreshMyJobs.refresh_jobs();
                 //   CommonUtils.showSmallToast(context,data_start.getMessage());
                } else {
                  //  CommonUtils.showSmallToast(context,data_start.getMessage());
                    dialog.dismiss();
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
