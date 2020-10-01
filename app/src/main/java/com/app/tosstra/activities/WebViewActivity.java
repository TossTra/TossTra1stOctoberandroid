package com.app.tosstra.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.tosstra.R;

public class WebViewActivity extends AppCompatActivity {
    String key;
    ImageView iv_back_offer;
    TextView tvTitle;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        key=getIntent().getStringExtra("url");
        title=getIntent().getStringExtra("title");
        WebView mywebview = (WebView) findViewById(R.id.webView);
        tvTitle=findViewById(R.id.tvTitle);
        iv_back_offer=findViewById(R.id.iv_back_offer);
        iv_back_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvTitle.setText(title);
        mywebview.loadUrl(key);
    }
}
