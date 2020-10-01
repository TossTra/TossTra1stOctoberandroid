package com.app.tosstra.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.app.tosstra.R;
import com.app.tosstra.models.GenricModel;
import com.app.tosstra.services.Interface;
import com.app.tosstra.utils.CommonUtils;
import com.app.tosstra.utils.PreferenceHandler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_old_pass, et_pass, et_con_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_activitt);
        initUI();
    }

    private void initUI() {
        et_old_pass=findViewById(R.id.et_old_pass);
        et_pass=findViewById(R.id.et_pass);
        et_con_pass=findViewById(R.id.et_con_pass);
        Button bt_submit = findViewById(R.id.bt_submit);
        ImageView iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        bt_submit.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_submit:
                hitChangePassword();
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void hitChangePassword() {
        final Dialog dialog = AppUtil.showProgress(ChangePasswordActivity.this);
        Interface service = CommonUtils.retroInit();
        Call<GenricModel> call = service.change_password(PreferenceHandler.readString(
                ChangePasswordActivity.this,PreferenceHandler.USER_ID,""),et_old_pass.getText().toString().trim(),
                et_pass.getText().toString().trim(),et_con_pass.getText().toString().trim());
        call.enqueue(new Callback<GenricModel>() {
            @Override
            public void onResponse(Call<GenricModel> call, Response<GenricModel> response) {
                GenricModel data = response.body();
                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {
                    dialog.dismiss();
                    CommonUtils.showLongToast(ChangePasswordActivity.this, data.getMessage());
                    //finish();
                } else {
                    dialog.dismiss();
                    CommonUtils.showLongToast(ChangePasswordActivity.this, data.getMessage());
                }
            }

            @Override
            public void onFailure(Call<GenricModel> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(ChangePasswordActivity.this, t.getMessage());
            }
        });
    }
}