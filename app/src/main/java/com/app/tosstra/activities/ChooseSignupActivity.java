package com.app.tosstra.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.app.tosstra.R;

public class ChooseSignupActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_dispacher, btn_driver;
    private ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_signup);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }
        initUI();
        setOnClickListnor();
    }

    private void setOnClickListnor() {
        btn_driver.setOnClickListener(this);
        btn_dispacher.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }

    private void initUI() {
        btn_dispacher = findViewById(R.id.btn_dispacher);
        btn_driver = findViewById(R.id.btn_driver);
        iv_back = findViewById(R.id.iv_back);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_driver:
                Intent i = new Intent(ChooseSignupActivity.this, SignupActivity.class);
                i.putExtra("user_type", "driver");
                startActivity(i);
                break;
            case R.id.btn_dispacher:
                i = new Intent(ChooseSignupActivity.this, SignupActivity.class);
                // user_trpe="dispacher";
                i.putExtra("user_type", "dispatcher");
                startActivity(i);
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }
}