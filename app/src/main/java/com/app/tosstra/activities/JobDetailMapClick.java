package com.app.tosstra.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.tosstra.R;
import com.app.tosstra.models.AllDriverNew;
import com.app.tosstra.models.AllJobsToDriver;
import com.app.tosstra.services.Interface;
import com.app.tosstra.utils.CommonUtils;
import com.app.tosstra.utils.PreferenceHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JobDetailMapClick extends AppCompatActivity implements View.OnClickListener {
    TextView tvCompanyValue, tvPickAddval, tvDropAddval, tvDriverNameVal, tvDriverEmailVal,
            startTimetVal, dateToVal, dateStartVal, tomeToVal, tvLocVal,tvDriverInfo;
    ImageView iv_back;
    private String cur_lat, cur_lon, dr_lat, dr_lon;
    int pos = 0;
    String job_id, disp_id, dri_id, start_status, end_job;
    Button btn_end_job;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail_map_click);



        job_id = getIntent().getStringExtra("job_id");
        disp_id = getIntent().getStringExtra("disp_id");
        dri_id = getIntent().getStringExtra("dri_id");
        start_status = getIntent().getStringExtra("start_status");
        end_job = getIntent().getStringExtra("end_job");
        initUI();
        Getinfluence();
    }
    private void initUI() {
        tvLocVal = findViewById(R.id.tvLocVal);
        tvDriverInfo=findViewById(R.id.tvDriverInfo);
        Button btn_dirction = findViewById(R.id.btn_dirction);
        tvCompanyValue = findViewById(R.id.tvCompanyValue);
        iv_back = findViewById(R.id.iv_back);
        tvPickAddval = findViewById(R.id.tvPickAddval);
        tvDropAddval = findViewById(R.id.tvDropAddval);
        tvDriverNameVal = findViewById(R.id.tvDriverNameVal);
        tvDriverEmailVal = findViewById(R.id.tvDriverEmailVal);
        tomeToVal = findViewById(R.id.tomeToVal);
        dateStartVal = findViewById(R.id.dateStartVal);
        dateToVal = findViewById(R.id.dateToVal);
        startTimetVal = findViewById(R.id.startTimetVal);
        btn_end_job = findViewById(R.id.btn_end_job);

        if (end_job != null && end_job.equalsIgnoreCase("1")) {
            btn_end_job.setText("End Job");
        }


        btn_end_job.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        btn_dirction.setOnClickListener(this);

        if (start_status != null)
            if (start_status.equalsIgnoreCase("0")) {
                btn_end_job.setVisibility(View.GONE);
                btn_dirction.setVisibility(View.GONE);
            }
    }


    @Override

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_end_job:
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(JobDetailMapClick.this);
                // alertDialog.setTitle("NKA SERVICE");
                alertDialog.setMessage("Are you sure you want to mark complete for this job?");
                alertDialog.setIcon(R.mipmap.call_icon);
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        hitJobComplete();
                    }
                });
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();

                break;
            case R.id.btn_dirction:
                Intent i = new Intent(JobDetailMapClick.this, SeeDirectionActivity.class);
                i.putExtra("cur_lat", cur_lat);
                i.putExtra("cur_lon", cur_lon);
                i.putExtra("dr_lat", dr_lat);
                i.putExtra("dr_lon", dr_lon);

                Log.e("curr", cur_lat + "  " + dr_lat);
                startActivity(i);
                break;
        }
    }


    private void Getinfluence() {
        final Dialog dialog = AppUtil.showProgress(JobDetailMapClick.this);
        try {
            //  progress.setVisibility(View.VISIBLE);
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            Retrofit retrofit2 = new Retrofit.Builder()
                    .baseUrl("http://a1professionals.net/tosstra/api/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            final Interface apiListener = retrofit2.create(Interface.class);
            Call<AllDriverNew> call = apiListener.single_job_detail1(job_id, disp_id, dri_id);
            call.enqueue(new Callback<AllDriverNew>() {
                @Override
                public void onResponse(Call<AllDriverNew> call, Response<AllDriverNew> response) {
                    AllDriverNew data = response.body();
                    assert data != null;
                    if (data.getCode().equalsIgnoreCase("201")) {
                        dialog.dismiss();
                        tvCompanyValue.setText(data.getData().get(pos).getCompanyName());
                        tvPickAddval.setText(data.getData().get(pos).getPupStreet());
                        tvDropAddval.setText(data.getData().get(pos).getDrpStreet());
                        if(end_job!=null && end_job.equalsIgnoreCase("1")){
                            tvDriverInfo.setText("Driver Information");
                            tvDriverNameVal.setText(data.getData().get(pos).getDriverfirstName() + " " + data.getData().get(pos).getDriverlastName());
                            tvDriverEmailVal.setText(data.getData().get(pos).getDriveremail());
                            tvLocVal.setText(data.getData().get(pos).getDriverphone());
                        }else {
                            tvDriverInfo.setText("Dispatcher Information");
                            tvDriverNameVal.setText(data.getData().get(pos).getDriverfirstName() + " " + data.getData().get(pos).getDriverlastName());
                            tvDriverEmailVal.setText(data.getData().get(pos).getDriveremail());
                            tvLocVal.setText(data.getData().get(pos).getDriverphone());
                        }
                        tvDriverNameVal.setText(data.getData().get(pos).getFirstName() + " " + data.getData().get(pos).getLastName());
                        tvDriverEmailVal.setText(data.getData().get(pos).getEmail());
                        dateStartVal.setText(data.getData().get(pos).getDateFrom());
                        dateToVal.setText(data.getData().get(pos).getDateTo());
                        startTimetVal.setText(data.getData().get(pos).getEndTime());
                        tomeToVal.setText(data.getData().get(pos).getEndTime());
                        cur_lat = data.getData().get(pos).getPuplatitude();
                        cur_lon = data.getData().get(pos).getPuplongitude();
                        dr_lat = data.getData().get(pos).getDrplatitude();
                        dr_lon = data.getData().get(pos).getDrplongitude();
                        tvLocVal.setText(data.getData().get(pos).getPhone());
                        Log.e("posss", cur_lat + "," + cur_lon);


                    } else {
                        dialog.dismiss();
                        //CommonUtils.showLongToast(ActiveJobDetail.this, data.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<AllDriverNew> call, Throwable t) {
                    Toast.makeText(JobDetailMapClick.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                    //      progress.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("exception", e.getMessage());
        }
    }


    private void hitJobComplete() {
        final Dialog dialog = AppUtil.showProgress(JobDetailMapClick.this);
        Interface service = CommonUtils.retroInit();
        Call<AllJobsToDriver> call = service.job_complete(PreferenceHandler.readString(JobDetailMapClick.this, PreferenceHandler.USER_ID, ""),
                job_id);
        call.enqueue(new Callback<AllJobsToDriver>() {
            @Override
            public void onResponse(Call<AllJobsToDriver> call, Response<AllJobsToDriver> response) {
                AllJobsToDriver data = response.body();
                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {
                    dialog.dismiss();
                    CommonUtils.showSmallToast(JobDetailMapClick.this, data.getMessage());
                    finish();

                } else {
                    dialog.dismiss();
                    CommonUtils.showSmallToast(JobDetailMapClick.this, data.getMessage());
                }
            }

            @Override
            public void onFailure(Call<AllJobsToDriver> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(JobDetailMapClick.this, t.getMessage());
            }
        });
    }
}
