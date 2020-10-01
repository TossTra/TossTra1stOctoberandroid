package com.app.tosstra.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.app.tosstra.R;
import com.app.tosstra.fragments.dispacher.SearchActivity;
import com.app.tosstra.models.GenricModel;
import com.app.tosstra.services.Interface;
import com.app.tosstra.utils.CommonUtils;
import com.app.tosstra.utils.PreferenceHandler;
import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.tosstra.adapters.ChildActieTruckAdapter.interestList;
import static com.app.tosstra.fragments.dispacher.SeniorityTruckFragment.new_interestList_seniority;


public class AddANewJobActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_amount, et_zip,
            et_zipDrop, et_add_info;
    private Button btn_send;
    private ImageView iv_back;
    private TextView tvDateStart, tvDateEnd, tvTimeStart, tv_EndTime, tvPerHours, tvPerLoad;
    private String rate;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    String lst = "";
    String type;
    public static TextView et_street, et_dropAddval;
    public static LatLng current_latlon, drp_latlon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_a_new_job);
        initUI();
        type = getIntent().getStringExtra("f_type");
    }

    private void initUI() {
        tvPerHours = findViewById(R.id.tvPerHours);
        tvPerLoad = findViewById(R.id.tvPerLoad);
        tvDateStart = findViewById(R.id.tvDateStart);
        tvDateEnd = findViewById(R.id.tvDateEnd);
        iv_back = findViewById(R.id.iv_back_addJob);
        tvTimeStart = findViewById(R.id.tvTimeStart);
        tv_EndTime = findViewById(R.id.tv_EndTime);
        et_amount = findViewById(R.id.et_amount);
        et_street = findViewById(R.id.et_street);

        et_zip = findViewById(R.id.et_zip);
        et_dropAddval = findViewById(R.id.et_dropAddval1);

        et_zipDrop = findViewById(R.id.et_zipDrop);
        et_add_info = findViewById(R.id.et_add_info);
        btn_send = findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tvDateStart.setOnClickListener(this);
        tvDateEnd.setOnClickListener(this);
        tvTimeStart.setOnClickListener(this);
        tv_EndTime.setOnClickListener(this);
        tvPerHours.setOnClickListener(this);
        tvPerLoad.setOnClickListener(this);
       /* et_street.setEnabled(false);
        et_stateDrop.setEnabled(false);*/
        et_street.setOnClickListener(this);
        et_dropAddval.setOnClickListener(this);
        tvPerHours.performClick();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                if (validation())
                    hitAddNewAPI();
                break;
            case R.id.iv_back_addJob:
                finish();
                break;
            case R.id.tvDateStart:
                datePicker("S");
                break;
            case R.id.tvDateEnd:
                datePicker("E");
                break;
            case R.id.tvTimeStart:
                timePicker("S");
                break;
            case R.id.tv_EndTime:
                timePicker("E");
                break;

            case R.id.tvPerHours:
                rate = "per Hours";
                tvPerHours.setTextColor(getResources().getColor(R.color.white));
                tvPerLoad.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                tvPerLoad.setBackground(AddANewJobActivity.this.getResources().getDrawable(R.drawable.rounded_right_stroke));
                tvPerHours.setBackground(AddANewJobActivity.this.getResources().getDrawable(R.drawable.rounded_left_solid));
                break;
            case R.id.tvPerLoad:
                rate = "per Load";
                tvPerHours.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                tvPerLoad.setTextColor(getResources().getColor(R.color.white));
                tvPerHours.setBackground(AddANewJobActivity.this.getResources().getDrawable(R.drawable.rounded_left_stroke));
                tvPerLoad.setBackground(AddANewJobActivity.this.getResources().getDrawable(R.drawable.rounded_right_soliid));
                break;
            case R.id.et_street:
                Intent i = new Intent(AddANewJobActivity.this, SearchActivity.class);
                i.putExtra("pickDrop", "pick");
                startActivity(i);
                break;
            case R.id.et_dropAddval1:
                Intent i1 = new Intent(AddANewJobActivity.this, SearchActivity.class);
                i1.putExtra("pickDrop", "drop");
                startActivity(i1);
                break;
        }
    }

    private boolean validation() {
        if (et_amount.getText().toString().isEmpty()) {
            CommonUtils.showSmallToast(AddANewJobActivity.this, "Please select amount");
            return false;
        } else if (et_street.getText().toString().isEmpty()) {
            CommonUtils.showSmallToast(AddANewJobActivity.this, "Please select pickup address");
            return false;
        } else if (et_dropAddval.getText().toString().isEmpty()) {
            CommonUtils.showSmallToast(AddANewJobActivity.this, "Please select Drop off address");
            return false;
        } else if (tvDateStart.getText().toString().equalsIgnoreCase("____________")) {
            CommonUtils.showSmallToast(AddANewJobActivity.this, "Please select start from");
            return false;
        } else if (tvDateEnd.getText().toString().equalsIgnoreCase("____________")) {
            CommonUtils.showSmallToast(AddANewJobActivity.this, "Please select start from to end date");
            return false;
        }else if(tvTimeStart.getText().toString().equalsIgnoreCase("____________")){
            CommonUtils.showSmallToast(AddANewJobActivity.this, "Please select start time");
            return false;
        }else if(tv_EndTime.getText().toString().trim().equalsIgnoreCase("____________")){
            CommonUtils.showSmallToast(AddANewJobActivity.this, "Please select end time");
            return false;
        }
        return true;
    }

    private void timePicker(final String key) {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);


        timePickerDialog = new TimePickerDialog(AddANewJobActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        Date date = null;
                        SimpleDateFormat outputDate = new SimpleDateFormat("HH:mm");
                        try {
                            date = outputDate.parse(hourOfDay + ":" + minute);
                            String selected_time = outputDate.format(date);
                            if (key.equalsIgnoreCase("S")) {
                                tvTimeStart.setTextColor(getResources().getColor(R.color.black));
                                tvTimeStart.setText(selected_time);
                            } else {
                                tv_EndTime.setTextColor(getResources().getColor(R.color.black));
                                tv_EndTime.setText(selected_time);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                    }
                }, mHour, mMinute, true);

        timePickerDialog.show();
    }

    private void datePicker(final String key) {

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(AddANewJobActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        SimpleDateFormat inputDate = new SimpleDateFormat("dd-MM-yyyy");
                        Date date = null;
                        try {
                            date = inputDate.parse(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            assert date != null;
                            String selected_date = inputDate.format(date);
                            if (key.equalsIgnoreCase("S")) {
                                tvDateStart.setTextColor(getResources().getColor(R.color.black));
                                tvDateStart.setText(selected_date);
                            } else {
                                tvDateEnd.setTextColor(getResources().getColor(R.color.black));
                                tvDateEnd.setText(selected_date);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void hitAddNewAPI() {
        String mul_driver_ids;
        if (type.equalsIgnoreCase("act")) {
            lst = interestList.toString();
            String StringAdd = lst.toString();
            int comma = StringAdd.lastIndexOf(',');
            String finalStr = lst.replaceAll("[\\[\\](){}(,)*$]", "");
            mul_driver_ids = finalStr.replace(" ", ",");
        } else {
            lst = new_interestList_seniority.toString();
            String StringAdd = lst.toString();
            int comma = StringAdd.lastIndexOf(',');
            String finalStr = lst.replaceAll("[\\[\\](){}(,)*$]", "");
            mul_driver_ids = finalStr.replace(" ", ",");
        }


        final Dialog dialog = AppUtil.showProgress(AddANewJobActivity.this);
        Interface service = CommonUtils.retroInit();
        Call<GenricModel> call = service.createJpbDisp(PreferenceHandler.readString(
                AddANewJobActivity.this, PreferenceHandler.USER_ID, ""),
                rate, et_amount.getText().toString().trim(),
                et_street.getText().toString().trim(), "", "",
                et_zip.getText().toString().trim(), et_dropAddval.getText().toString().trim(), "",
                "", et_zipDrop.getText().toString().trim(), tvDateStart.getText().toString(), tvDateEnd.getText().toString(),
                tvTimeStart.getText().toString().trim(), tv_EndTime.getText().toString().trim(), et_add_info.getText().toString().trim(), mul_driver_ids,
                String.valueOf(current_latlon.latitude), String.valueOf(current_latlon.longitude), String.valueOf(drp_latlon.latitude), String.valueOf(drp_latlon.longitude));
        call.enqueue(new Callback<GenricModel>() {
            @Override
            public void onResponse(Call<GenricModel> call, Response<GenricModel> response) {
                GenricModel data = response.body();
                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {
                    dialog.dismiss();
                    CommonUtils.showLongToast(AddANewJobActivity.this, data.getMessage());
                    Intent intent=new Intent();
                    intent.putExtra("refresh_allTruck","1");
                    setResult(1,intent);
                    setResult(2,intent);
                    finish();
                } else {
                    dialog.dismiss();
                    CommonUtils.showLongToast(AddANewJobActivity.this, data.getMessage());
                }
            }

            @Override
            public void onFailure(Call<GenricModel> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(AddANewJobActivity.this, t.getMessage());
            }
        });
    }
}