package com.app.tosstra.ViewPagerHome;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.tosstra.R;
import com.app.tosstra.activities.AppUtil;
import com.app.tosstra.activities.JobDetailActivity;
import com.app.tosstra.activities.JobOfferActivity;
import com.app.tosstra.activities.MainActivity;
import com.app.tosstra.fragments.driver.AllJobsFragment;
import com.app.tosstra.fragments.driver.MyJobFragment;
import com.app.tosstra.models.AllJobsToDriver;
import com.app.tosstra.models.GenricModel;
import com.app.tosstra.services.Interface;
import com.app.tosstra.utils.CommonUtils;
import com.app.tosstra.utils.PreferenceHandler;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPagerFragment extends Fragment implements View.OnClickListener {

    private TextView tv_amount, tv_company_name;
    AllJobsToDriver data;
    int position;
    private CircleImageView iv_user;
    private RelativeLayout llTop;
    private Button btn_accept, btn_reject,btn_start;

    public ViewPagerFragment(int position, AllJobsToDriver data) {
        this.data = data;
        this.position = position;
    }


    public static Fragment newInstance(int position, AllJobsToDriver data) {
        ViewPagerFragment viewPagerFragment = new ViewPagerFragment(position, data);
        return viewPagerFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_pager_fragment, container, false);
        initUI(view);
        return view;
    }

    private void initUI(View view) {

        tv_amount = view.findViewById(R.id.tv_amount);
        tv_company_name = view.findViewById(R.id.tv_company_name);
        iv_user = view.findViewById(R.id.iv_user);
        btn_accept=view.findViewById(R.id.btn_accept);
        btn_reject=view.findViewById(R.id.btn_reject);
        btn_start=view.findViewById(R.id.btn_start);

        tv_amount.setText("$" + data.getData().get(position).getRate() + " per Hours");
        tv_company_name.setText("Company Name - " + data.getData().get(position).getCompanyName());
        llTop = view.findViewById(R.id.llTop);
        llTop.setOnClickListener(this);
        btn_reject.setOnClickListener(this);
        btn_accept.setOnClickListener(this);
        btn_start.setOnClickListener(this);

        if(data.getData().get(position).getJobStatus().equalsIgnoreCase("1")){
            btn_accept.setVisibility(View.GONE);
            btn_reject.setVisibility(View.GONE);
            btn_start.setVisibility(View.VISIBLE);
        }

        Glide
                .with(getActivity())
                .load("http://a1professionals.net/tosstra/assets/usersImg/" + data.getData().get(position).getProfileImg())
                .centerCrop()
                .placeholder(R.mipmap.image)
                .into(iv_user);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llTop:
                if(btn_start.getVisibility()==View.VISIBLE){
                    Intent i = new Intent(getContext(), JobOfferActivity.class);
                    i.putExtra("job_id",data.getData().get(position).getJobId());
                    i.putExtra("disp_id",data.getData().get(position).getDispatcherId());
                    i.putExtra("dri_id",data.getData().get(position).getDispatcherId());
                    i.putExtra("status_accept","1");
                    startActivity(i);
                }else {
                    Intent i = new Intent(getContext(), JobOfferActivity.class);
                    i.putExtra("job_id",data.getData().get(position).getJobId());
                    i.putExtra("disp_id",data.getData().get(position).getDispatcherId());
                    i.putExtra("dri_id",data.getData().get(position).getDispatcherId());
                    i.putExtra("status_accept","0");
                    startActivity(i);
                }

                break;
            case R.id.btn_reject:
                btn_reject.setBackground(getActivity().getResources().getDrawable(R.drawable.rounded_blue_solid));
                btn_accept.setBackground(getActivity().getResources().getDrawable(R.drawable.rounded_blue_stroke));
                btn_reject.setTextColor(getResources().getColor(R.color.white));
                btn_accept.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                hitAcceptReject("0");
                break;
            case R.id.btn_accept:
                btn_accept.setBackground(getActivity().getResources().getDrawable(R.drawable.rounded_blue_solid));
                btn_reject.setBackground(getActivity().getResources().getDrawable(R.drawable.rounded_blue_stroke));
                btn_accept.setTextColor(getResources().getColor(R.color.white));
                btn_reject.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
              //  final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                hitAcceptReject("1");
                // alertDialog.setTitle("NKA SERVICE");
                /*alertDialog.setMessage("Are you sure you want to end this job?");
                alertDialog.setIcon(R.mipmap.call_icon);
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        hitAcceptReject("1");
                    }
                });
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();*/

                break;
                case R.id.btn_start:
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setMessage("Are you sure you want to start this job?");
                alertDialog.setIcon(R.mipmap.call_icon);
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        hitStartJobAPI();
                    }
                });
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();



                break;
        }
    }


    private void hitStartJobAPI() {
        final Dialog dialog = AppUtil.showProgress(getActivity());
        Interface service = CommonUtils.retroInit();
        Call<GenricModel> call = service.start_job(PreferenceHandler.readString(getContext(), PreferenceHandler.USER_ID, ""),
                data.getData().get(position).getJobId());
        call.enqueue(new Callback<GenricModel>() {
            @Override
            public void onResponse(Call<GenricModel> call, Response<GenricModel> response) {
                GenricModel data_start = response.body();
                assert data_start != null;
                if (data_start.getCode().equalsIgnoreCase("201")) {
                    dialog.dismiss();
                    Fragment fragment=new AllJobsFragment();
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.add(R.id.mainNavFragment2,fragment,"");
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    CommonUtils.showSmallToast(getActivity(),data_start.getMessage());
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<GenricModel> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(getActivity(), t.getMessage());
            }
        });
    }

    private void hitAcceptReject(final String status) {
        final Dialog dialog = AppUtil.showProgress(getActivity());
        Interface service = CommonUtils.retroInit();
        Call<GenricModel> call = service.acceptReject(PreferenceHandler.readString(getContext(),PreferenceHandler.USER_ID,""), data.getData().get(position).getJobId(),
                status, data.getData().get(position).getDispatcherId());
        call.enqueue(new Callback<GenricModel>() {
            @Override
            public void onResponse(Call<GenricModel> call, Response<GenricModel> response) {
                GenricModel data = response.body();
                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {
                    dialog.dismiss();
                    if(data.getMessage()!=null){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            if (status.equalsIgnoreCase("1")) {
                                btn_accept.setVisibility(View.GONE);
                                btn_reject.setVisibility(View.GONE);
                                btn_start.setVisibility(View.VISIBLE);
                            }else {
                                Intent i = new Intent(getContext(), MainActivity.class);
                                startActivity(i);
                            }
                        }

                        }

                } else {
                    dialog.dismiss();
                    CommonUtils.showLongToast(getActivity(), data.getMessage());
                }
            }

            @Override
            public void onFailure(Call<GenricModel> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(getActivity(), t.getMessage());
            }
        });
    }

    private void job_detail() {
        final Dialog dialog = AppUtil.showProgress(getActivity());
        Interface service = CommonUtils.retroInit();
        Call<AllJobsToDriver> call = service.single_job_detail(data.getData().get(position).getJobId(),
                data.getData().get(position).getDispatcherId(), data.getData().get(position).getDriverId());
        call.enqueue(new Callback<AllJobsToDriver>() {
            @Override
            public void onResponse(Call<AllJobsToDriver> call, Response<AllJobsToDriver> response) {
                AllJobsToDriver data = response.body();
                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {
                    dialog.dismiss();
                    CommonUtils.showLongToast(getActivity(), data.getMessage());
                    }
                else {
                    dialog.dismiss();
                    CommonUtils.showLongToast(getActivity(), data.getMessage());
                }
            }

            @Override
            public void onFailure(Call<AllJobsToDriver> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(getActivity(), t.getMessage());
            }
        });
    }

}



