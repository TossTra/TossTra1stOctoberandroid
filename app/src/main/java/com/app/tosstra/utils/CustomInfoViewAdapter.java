package com.app.tosstra.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.app.tosstra.R;
import com.app.tosstra.models.AllJobsToDriver;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

public class CustomInfoViewAdapter implements GoogleMap.InfoWindowAdapter {
    LayoutInflater mInflater;
    private Context context;
    AllJobsToDriver.Data data;


    public CustomInfoViewAdapter(LayoutInflater mInflater, Context context, AllJobsToDriver.Data data) {
        this.mInflater = mInflater;
        this.context = context;
        this.data = data;

    }

    @Override
    public View getInfoWindow(Marker marker) {
        final View popup = mInflater.inflate(R.layout.info_window_layout, null);
        try {
            TextView tv_company_name = popup.findViewById(R.id.tv_company_name);
            tv_company_name.setText(marker.getTitle());
            ((TextView) popup.findViewById(R.id.title)).setText(marker.getTitle());
            Picasso.with(context)
                    .load("http://a1professionals.net/tosstra/assets/usersImg/" + data.getProfileImg())
                    .resize(150, 150)
                    .error(R.mipmap.logo)
                    .placeholder(R.mipmap.logo)
                    .centerCrop()
                    .priority(Picasso.Priority.HIGH)
                    .into(((ImageView) popup.findViewById(R.id.iv_dis_img)), new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) ((ImageView) popup.findViewById(R.id.image)).getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                            ((ImageView) popup.findViewById(R.id.image)).setImageDrawable(imageDrawable);
                        }

                        @Override
                        public void onError() {

                        }

                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return popup;
    }

    @Override
    public View getInfoContents(Marker marker) {


        return null;
    }
}