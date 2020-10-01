package com.app.tosstra.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.tosstra.R;
import com.app.tosstra.models.SIgnUp;
import com.app.tosstra.services.Interface;
import com.app.tosstra.utils.CommonUtils;
import com.app.tosstra.utils.MyGps;
import com.app.tosstra.utils.PreferenceHandler;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SigninActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_signup, tvForgotPass;
    private Button bt_sigin;
    private ImageView iv_back;
    private Intent intent;
    private EditText et_email, et_pass;
    private String email, password;
    String type;
    SIgnUp data;
    private String firebase_token="";
    String lat,lon;
    private MyGps gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        getLocation();
        gpsTracker = new MyGps(SigninActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }
        init();
    }

    private void init() {
        et_email = findViewById(R.id.et_email);
        et_pass = findViewById(R.id.et_pass);
        tv_signup = findViewById(R.id.tv_signup);
        bt_sigin = findViewById(R.id.bt_sigin);
        iv_back = findViewById(R.id.iv_back);
        tvForgotPass = findViewById(R.id.tvForgotPass);
        tv_signup.setOnClickListener(this);
        bt_sigin.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tvForgotPass.setOnClickListener(this);
    }

    public void getLocation(){
        gpsTracker = new MyGps(SigninActivity.this);
        if(gpsTracker.canGetLocation()){
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            lat=(String.valueOf(latitude));
            lon=(String.valueOf(longitude));
            Log.e("latitttt",lat);
        }else{
            gpsTracker.showSettingsAlert();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_signup:
                intent = new Intent(SigninActivity.this, SignupActivity.class);
                intent.putExtra("user_type", "dispatcher");
                startActivity(intent);
                finish();
                break;
            case R.id.iv_back:
                finish();
                break;

            case R.id.bt_sigin:
                if (validation()) {
                    hitSignInAPI();
                }
                break;
            case R.id.tvForgotPass:
                intent = new Intent(SigninActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                break;

        }
    }

    private void hitSignInAPI() {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        firebase_token= getTokenFromPrefs();
        final Dialog dialog = AppUtil.showProgress(SigninActivity.this);
        Interface service = CommonUtils.retroInit();
        Call<SIgnUp> call = service.signIn(email, password, firebase_token, "1",tz.getID(),lat,lon);
        Log.e("dfsvfsd",tz.getID());
        call.enqueue(new Callback<SIgnUp>() {
            @Override
            public void onResponse(Call<SIgnUp> call, Response<SIgnUp> response) {
                 data = response.body();
                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {
                    dialog.dismiss();
                    Dexter.withActivity(SigninActivity.this)
                            .withPermissions(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.CALL_PHONE)
                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {
                                    // check if all permissions are granted
                                    if (report.areAllPermissionsGranted()) {
                                        intent = new Intent(SigninActivity.this, MainActivity.class);
                                        type=data.getData().get(0).getUserType();
                                        intent.putExtra("user_type", type);

                                 //       CommonUtils.showLongToast(SigninActivity.this, data.getData().get(0).getId());
                                        PreferenceHandler.writeString(SigninActivity.this, PreferenceHandler.USER_ID, data.getData().get(0).getId());
                                        PreferenceHandler.writeString(SigninActivity.this,"profile_url", String.valueOf(data.getData().get(0).getProfileImg()));
                                        PreferenceHandler.writeString(SigninActivity.this,"company", data.getData().get(0).getCompanyName());
                                        PreferenceHandler.writeString(SigninActivity.this,"user_typp", data.getData().get(0).getUserType());

                                        startActivity(intent);
                                        finish();
                                    }
                                    // check for permanent denial of any permission
                                    if (report.isAnyPermissionPermanentlyDenied()) {
                                        showSettingsDialog();
                                    }
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }
                            })
                            .onSameThread()
                            .check();



                } else {
                    dialog.dismiss();
                    CommonUtils.showLongToast(SigninActivity.this, data.getMessage());
                }
            }

            @Override
            public void onFailure(Call<SIgnUp> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(SigninActivity.this, t.getMessage());
            }
        });
    }

    private String getTokenFromPrefs() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SigninActivity.this);
        return preferences.getString("firebase_token", null);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SigninActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }



    private boolean validation() {
        email = et_email.getText().toString().trim();
        password = et_pass.getText().toString().trim();
        return true;
    }
}