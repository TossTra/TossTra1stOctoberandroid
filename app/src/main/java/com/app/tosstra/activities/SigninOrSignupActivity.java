package com.app.tosstra.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.app.tosstra.R;

public class SigninOrSignupActivity extends AppCompatActivity implements View.OnClickListener {
private Button bt_signup,bt_signin;
 String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_or_signup);
       /* getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.white));
        }
        init();
    }

    private void init() {
        bt_signin=findViewById(R.id.bt_signin);
        bt_signup=findViewById(R.id.bt_signup);
        bt_signup.setOnClickListener(this);
        bt_signin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_signup:
                Intent intent=new Intent(SigninOrSignupActivity.this,ChooseSignupActivity.class);
                startActivity(intent);
                break;

            case R.id.bt_signin:
                Intent intent1=new Intent(SigninOrSignupActivity.this,SigninActivity.class);
                startActivity(intent1);
                break;
        }
    }
}