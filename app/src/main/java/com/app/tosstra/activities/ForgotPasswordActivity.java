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
import com.app.tosstra.models.SIgnUp;
import com.app.tosstra.services.Interface;
import com.app.tosstra.utils.CommonUtils;
import com.app.tosstra.utils.PreferenceHandler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_email;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initUI();
    }

    private void initUI() {
        Button bt_submit = findViewById(R.id.bt_submit);
        et_email = findViewById(R.id.et_email);
        ImageView iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        bt_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_submit:
                hitApi();
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void hitApi() {
        final Dialog dialog = AppUtil.showProgress(ForgotPasswordActivity.this);
        Interface service = CommonUtils.retroInit();
        Call<GenricModel> call = service.forgot_password(et_email.getText().toString().trim());
        call.enqueue(new Callback<GenricModel>() {
            @Override
            public void onResponse(Call<GenricModel> call, Response<GenricModel> response) {
                GenricModel data = response.body();
                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {
                    dialog.dismiss();
                    CommonUtils.showLongToast(ForgotPasswordActivity.this, data.getMessage());
                    intent = new Intent(ForgotPasswordActivity.this, RecoverPasswordActivity.class);
                    intent.putExtra("OTP", data.getOtp());
                    //PreferenceHandler.writeString(ForgotPasswordActivity.this, PreferenceHandler.USER_ID, data.getData().get(0).getId());
                    startActivity(intent);
                    finish();
                } else {
                    dialog.dismiss();
                    CommonUtils.showLongToast(ForgotPasswordActivity.this, data.getMessage());
                }
            }

            @Override
            public void onFailure(Call<GenricModel> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(ForgotPasswordActivity.this, t.getMessage());
            }
        });
    }
}