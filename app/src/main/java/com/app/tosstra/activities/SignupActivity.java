package com.app.tosstra.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.tosstra.R;
import com.app.tosstra.models.SIgnUp;
import com.app.tosstra.services.Interface;
import com.app.tosstra.utils.CommonUtils;
import com.app.tosstra.utils.PreferenceHandler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_email, et_password, et_confirm_pass, et_first_name, et_last_name, et_company_name,
            et_dot_number, et_phone;
    private String email, password, con_pass, first_name, last_name, company_name, dot_name, phone_number;
    private ImageView iv_back;
    private LinearLayout ll_signin;
    private String userType;
    private Button btn_signup;
    private Intent i;
    private CheckBox chk_owner, chk_disp;
    private TextView tv_term, tv_privacy_policy;
    View vl_ph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }
        userType = getIntent().getStringExtra("user_type");
        initUI();
    }

    private void initUI() {
        vl_ph=findViewById(R.id.vl_ph);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_confirm_pass = findViewById(R.id.et_confirm_pass);
        et_first_name = findViewById(R.id.et_first_name);
        et_last_name = findViewById(R.id.et_last_name);
        et_company_name = findViewById(R.id.et_company_name);
        chk_disp = findViewById(R.id.chk_disp);
        chk_owner = findViewById(R.id.chk_owner);
        et_dot_number = findViewById(R.id.et_dot_number);
        et_phone = findViewById(R.id.et_phone);
        iv_back = findViewById(R.id.iv_back);
        ll_signin = findViewById(R.id.ll_signin);
        btn_signup = findViewById(R.id.btn_signup);
        tv_privacy_policy = findViewById(R.id.tv_privacy_policy);
        tv_term = findViewById(R.id.tv_term);
        tv_term.setOnClickListener(this);
        tv_privacy_policy.setOnClickListener(this);

        iv_back.setOnClickListener(this);
        ll_signin.setOnClickListener(this);
        chk_owner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    chk_disp.setChecked(false);
            }
        });

        chk_disp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    chk_owner.setChecked(false);
            }
        });

        if (userType.equalsIgnoreCase("driver")) {
            chk_owner.setText("Driver");
            chk_disp.setText("Manager");
        } else {
            chk_disp.setText("Dispatcher");
            chk_owner.setText("Owner");
            vl_ph.setVisibility(View.GONE);
            et_phone.setVisibility(View.GONE);
        }
        btn_signup.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_signin:
                i = new Intent(SignupActivity.this, SigninActivity.class);
                i.putExtra("user_type", userType);
                startActivity(i);
                finish();
                break;
            case R.id.btn_signup:
                if (isValidation()) {
                    if (userType.equalsIgnoreCase("driver")) {
                        hitSignUpDriverAPI();
                    } else {
                        hitSignUpDispacherAPI();
                    }
                }
                break;
            case R.id.tv_term:
                i=new Intent(SignupActivity.this, WebViewActivity.class);
                i.putExtra("url","https://tosstra.com/terms-and-conditions");
                i.putExtra("title","Term & Conditions");
                startActivity(i);
                break;
            case R.id.tv_privacy_policy:
                i = new Intent(SignupActivity.this, WebViewActivity.class);
                i.putExtra("url","https://tosstra.com/privacy-policy");
                i.putExtra("title","Privacy Policy");
                startActivity(i);
                break;
        }
    }

    private void hitSignUpDriverAPI() {
        final Dialog dialog = AppUtil.showProgress(SignupActivity.this);
        Interface service = CommonUtils.retroInit();
        Call<SIgnUp> call = service.signUpDriver(first_name, last_name, company_name, email, password,
                dot_name, "driver", phone_number, "SubUser");
        call.enqueue(new Callback<SIgnUp>() {
            @Override
            public void onResponse(Call<SIgnUp> call, Response<SIgnUp> response) {
                SIgnUp data = response.body();
                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {
                    dialog.dismiss();
                    CommonUtils.showLongToast(SignupActivity.this, "Driver " + data.getMessage());
                    PreferenceHandler.writeString(SignupActivity.this, PreferenceHandler.USER_ID, data.getData().get(0).getId());
                    i = new Intent(SignupActivity.this, SigninActivity.class);
                    i.putExtra("user_type", userType);
                    startActivity(i);
                } else if (data.getCode().equalsIgnoreCase("200")) {
                    dialog.dismiss();
                    CommonUtils.showSmallToast(SignupActivity.this, data.getMessage());
                }
            }

            @Override
            public void onFailure(Call<SIgnUp> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(SignupActivity.this, t.getMessage());
            }
        });

    }

    private void hitSignUpDispacherAPI() {
        final Dialog dialog = AppUtil.showProgress(SignupActivity.this);
        Interface service = CommonUtils.retroInit();
        Call<SIgnUp> call = service.signUpDispacher(first_name, last_name, company_name, email, password,
                dot_name, phone_number, "dispatcher", "SubUser");
        call.enqueue(new Callback<SIgnUp>() {
            @Override
            public void onResponse(Call<SIgnUp> call, Response<SIgnUp> response) {
                SIgnUp data = response.body();
                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {
                    dialog.dismiss();
                    CommonUtils.showSmallToast(SignupActivity.this, data.getMessage());
                    PreferenceHandler.writeString(SignupActivity.this, PreferenceHandler.USER_ID, data.getData().get(0).getId());
                    i = new Intent(SignupActivity.this, SigninActivity.class);
                    i.putExtra("user_type", userType);
                    startActivity(i);
                } else if (data.getCode().equalsIgnoreCase("200")) {
                    dialog.dismiss();
                    CommonUtils.showSmallToast(SignupActivity.this, data.getMessage());
                }
            }

            @Override
            public void onFailure(Call<SIgnUp> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(SignupActivity.this, t.getMessage());
            }
        });

    }

    private boolean isValidation() {
        first_name = et_first_name.getText().toString().trim();
        last_name = et_last_name.getText().toString().trim();
        company_name = et_company_name.getText().toString().trim();
        email = et_email.getText().toString().trim();
        password = et_password.getText().toString().trim();
        con_pass = et_confirm_pass.getText().toString().trim();
        last_name = et_last_name.getText().toString().trim();
        dot_name = et_dot_number.getText().toString().trim();
        phone_number = et_phone.getText().toString().trim();

        return true;
    }
}