package com.app.tosstra.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.app.tosstra.R;
import com.app.tosstra.models.GenricModel;
import com.app.tosstra.services.Interface;
import com.app.tosstra.utils.CommonUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecoverPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_pass;
    private EditText et_con_pass;
    private String otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);
        initUI();
    }

    private void initUI() {
        otp=getIntent().getStringExtra("OTP");
        EditText et_otp = findViewById(R.id.et_otp);
        et_pass=findViewById(R.id.et_pass);
        ImageView iv_back = findViewById(R.id.iv_back);
        et_con_pass=findViewById(R.id.et_con_pass);
        //et_otp.setSelection(et_otp.getText().length());
        Button bt_submit = findViewById(R.id.bt_submit);
        bt_submit.setOnClickListener(this);
        et_otp.setText(otp);
    }

    private void hitApi() {
        final Dialog dialog = AppUtil.showProgress(RecoverPasswordActivity.this);
        Interface service = CommonUtils.retroInit();
        Call<GenricModel> call = service.recover_password(otp,et_pass.getText().toString().trim(),
                et_con_pass.getText().toString().trim());
        call.enqueue(new Callback<GenricModel>() {
            @Override
            public void onResponse(Call<GenricModel> call, Response<GenricModel> response) {
                GenricModel data = response.body();
                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {
                    dialog.dismiss();
                    CommonUtils.showLongToast(RecoverPasswordActivity.this, data.getMessage());
                    finish();
                } else {
                    dialog.dismiss();
                    CommonUtils.showLongToast(RecoverPasswordActivity.this, data.getMessage());
                }
            }

            @Override
            public void onFailure(Call<GenricModel> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(RecoverPasswordActivity.this, t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_submit:
                hitApi();
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }
}