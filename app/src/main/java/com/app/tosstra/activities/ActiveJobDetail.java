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
import com.app.tosstra.models.AllDrivers;
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

public class ActiveJobDetail extends AppCompatActivity implements View.OnClickListener {
    TextView tvCompanyValue, tvPickAddval, tvDropAddval, tvDriverNameVal, tvDriverEmailVal,
            startTimetVal, dateToVal, dateStartVal, tomeToVal, tvLocVal;
    ImageView iv_back;
    private Button btn_end_job, btn_dirction;
    private String cur_lat;
    private String cur_lon;
    private String dr_lat;
    private String dr_lon;
    private String dri_lat;
    private String dri_lon;
    int pos;
    String job_id, dri_id;
    private String dis_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_job_detail);
        initUI();
        // job_detail();
        Getinfluence();
    }

    private void initUI() {
        pos = 0;
        job_id = getIntent().getStringExtra("job_id1");
        dis_id = getIntent().getStringExtra("dis_id1");
        dri_id = getIntent().getStringExtra("driver_id");
        Log.e("ttttt", dri_id);
        tvLocVal = findViewById(R.id.tvLocVal);
        btn_dirction = findViewById(R.id.btn_dirction);
        //allJobsToDriver = (AllJobsToDriver) getIntent().getSerializableExtra("our_job"); //Obtaining data
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
        btn_end_job.setText("End Job");
        btn_end_job.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        btn_dirction.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_end_job:
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActiveJobDetail.this);
                // alertDialog.setTitle("NKA SERVICE");
                alertDialog.setMessage("Are you sure you want to end job?");
                alertDialog.setIcon(R.mipmap.call_icon);
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        hitJobComplete();
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();


                break;
            case R.id.btn_dirction:
                Intent i = new Intent(ActiveJobDetail.this, SeeDriverLocation.class);
                i.putExtra("dis_id", dis_id);
                i.putExtra("job_id", job_id);
                i.putExtra("pup_lat", cur_lat);
                i.putExtra("pup_lon", cur_lon);
                i.putExtra("dr_lat", dr_lat);
                i.putExtra("dr_lon", dr_lon);
                i.putExtra("dri_id", dri_id);

                Log.e("fsdvdsc", dr_lat);
                startActivity(i);
                break;
        }
    }


    private void Getinfluence() {
        final Dialog dialog = AppUtil.showProgress(ActiveJobDetail.this);
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
            Call<AllDriverNew> call = apiListener.single_job_detail1(job_id, dis_id, dri_id);
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
                        tvDriverNameVal.setText(data.getData().get(pos).getDriverfirstName() + " " +
                                data.getData().get(pos).getDriverlastName());
                        tvDriverEmailVal.setText(data.getData().get(pos).getDriveremail());
                        tvLocVal.setText(data.getData().get(pos).getDriverphone());
                        dateStartVal.setText(data.getData().get(pos).getDateFrom());
                        dateToVal.setText(data.getData().get(pos).getDateTo());
                        startTimetVal.setText(data.getData().get(pos).getEndTime());
                        tomeToVal.setText(data.getData().get(pos).getEndTime());
                        cur_lat = data.getData().get(pos).getPuplatitude();
                        cur_lon = data.getData().get(pos).getPuplongitude();
                        dr_lat = data.getData().get(pos).getDrplatitude();
                        dr_lon = data.getData().get(pos).getDrplongitude();
                        dri_lat = data.getData().get(pos).getDriverlatitude();
                        dri_lon = data.getData().get(pos).getDriverlongitude();
                    } else {
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<AllDriverNew> call, Throwable t) {
                    Toast.makeText(ActiveJobDetail.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                    //      progress.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("exception", e.getMessage());
        }
    }

    private void hitJobComplete() {
        final Dialog dialog = AppUtil.showProgress(ActiveJobDetail.this);
        Interface service = CommonUtils.retroInit();
        Call<AllJobsToDriver> call = service.job_complete(dri_id, job_id);
        call.enqueue(new Callback<AllJobsToDriver>() {
            @Override
            public void onResponse(Call<AllJobsToDriver> call, Response<AllJobsToDriver> response) {
                AllJobsToDriver data = response.body();
                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {
                    dialog.dismiss();
                    //   CommonUtils.showSmallToast(ActiveJobDetail.this, data.getMessage());
                    finish();
                } else {
                    dialog.dismiss();
                    //   CommonUtils.showSmallToast(ActiveJobDetail.this, data.getMessage());
                }
            }

            @Override
            public void onFailure(Call<AllJobsToDriver> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(ActiveJobDetail.this, t.getMessage());
            }
        });
    }
}