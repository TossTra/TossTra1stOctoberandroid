package com.app.tosstra.fragments.common;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.app.tosstra.R;
import com.app.tosstra.activities.AppUtil;
import com.app.tosstra.activities.MainActivity;
import com.app.tosstra.activities.SigninActivity;
import com.app.tosstra.models.SIgnUp;
import com.app.tosstra.services.Interface;
import com.app.tosstra.utils.CommonUtils;
import com.app.tosstra.utils.PreferenceHandler;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        hitSignInAPI();

    }

    private void hitSignInAPI() {
        try {
            //final Dialog dialog = AppUtil.showProgress(TestActivity.this);
            Retrofit service = retroInitHeader();
            Interface interface41 = service.create(Interface.class);
            Call<TestModel> call = interface41.test(" +91", "0", " +91",
                    "0", " +91", "7018441734", "7018441734", " dev",
                    "funny", "7018441734");
            call.enqueue(new Callback<TestModel>() {
                @Override
                public void onResponse(Call<TestModel> call, Response<TestModel> response) {
                    TestModel data = response.body();
                    if (data != null) {
                        if (response.isSuccessful()) {
                          //  dialog.dismiss();
                            CommonUtils.showSmallToast(TestActivity.this, "success");
                        } else {
                         //   dialog.dismiss();
                            CommonUtils.showSmallToast(TestActivity.this, "success but else");
                        }
                    }
                }

                @Override
                public void onFailure(Call<TestModel> call, Throwable t) {
                   // dialog.dismiss();
                    CommonUtils.showSmallToast(TestActivity.this, t.getMessage());
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "edwg"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static Retrofit retroInitHeader() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @NotNull
            @Override
            public okhttp3.Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder();
                requestBuilder.header("Version","1.0");
                requestBuilder.header("timeZone","IST");
                requestBuilder.header("Authorization", "Bearer " + "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiI1ZWU5YWI4ZTU4MGQxZDRlNDY2NDRhZjIiLCJqdGkiOiI2ZjA4N2RmNmM0NmZlNDAyMGVmMTBjOWY1MzdjOGU4OTIwYWUzMTQzNmM2NmVjZjg5NTc5OWE1NGQ4MTNjMTg3MzgxMjM1MGQzYjRmMTE3YyIsImlhdCI6MTYwMDk0NTY1MiwibmJmIjoxNjAwOTQ1NjUyLCJleHAiOjE2MTY1ODQwNTIsInN1YiI6IjVmNmM3ZGY0MDdkMDg5MTU1NDAwNjU2MiIsInNjb3BlcyI6WyJ0ZW1wb3JhcnktY3VzdG9tZXItc2VydmljZSJdfQ.N_6Jjo5TBHV_Y99Zm1ZgRSkjAvvX3LCYZfeayV6QSayt2O-lsjFQC9IB7iK6yXXtpY7n1aaD2yLwPYv4TlAFxfkxJ7pSxpmoi-sRxvV53Kp5_hK_Wje9LvBpS0xQIzavh8T7GQfyMVLSDmlYLGhIOydl0Fr_5dzUDVfEQdhIAYqSp7VFlVITV4BQ3t4MrKxySfmPeB-fBqfnu_ZgTgnYdMJoQ-ZSljGarXfOImneqhBSNfo1PBQLn9ChPYQd5NMgUQFYApLY1wx7AOVds5x2tkFJPOYnPKoPy-ATTFmX2K9ts_4Bw7HL_rWy2js-hWLnSkkFWSoYeq720o35we88hYEfcKS0o-dOPUKSUSsDZb3mBMfYx-b6LolV_Z5PPZNgFjqpEtQVgoXI4Yn3aO_ZD3Nys3JF81HwaKTVyqlq7y0UqXdTmTYc0BJ9LXYfy9Q9oA61Si2sONF3ATV4nL1JlUiswb64xjlToufjIKdWXQXxWddjhzNt4kM4-Xxovm0GveBYxUCgbw0PNgQm28fabjF9sJ1rDlr4A-wch_qY1r9DYXiuuu1Bc6qk9jmGM7Tc4v6Le0HlfDRl755HUmULnpwZltTa-zlHP-JUKDFAorCAUzjb9P5NnKAlI09SSsw41ff5YQZbXRH3KgxqHZShA5-m9bJ9Qgh0hK8S07itwXI"); // <-- this is the important line
                requestBuilder.header("Accept", "application/json"); // <-- this is the important line
                requestBuilder.header("Content-Type", "application/x-www-form-urlencoded"); // <-- this is the important line
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        httpClient.connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .build();
        OkHttpClient client = httpClient.build();
        return new Retrofit.Builder()
                .baseUrl("http://112.196.9.212:8233/ridyrAPI/public/api/").client(client)
                .addConverterFactory(GsonConverterFactory.create()).client(client)
                .build();
    }
}