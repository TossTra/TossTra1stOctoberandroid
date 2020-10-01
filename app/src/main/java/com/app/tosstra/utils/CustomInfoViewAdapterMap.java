package com.app.tosstra.utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.tosstra.R;
import com.app.tosstra.activities.MainActivity;
import com.app.tosstra.models.AllDrivers;
import com.app.tosstra.models.AllJobsToDriver;
import com.app.tosstra.models.MarkerDetails;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomInfoViewAdapterMap implements GoogleMap.InfoWindowAdapter {
    LayoutInflater mInflater;
    private Context context;
    AllDrivers.Data data;



    public CustomInfoViewAdapterMap(LayoutInflater mInflater, Context context, AllDrivers.Data data) {
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
            CircleImageView imageUser=popup.findViewById(R.id.iv_dis_img);
            MarkerDetails markerDetails=(MarkerDetails)marker.getTag();
//            imageUser.setImageResource(markerDetails.getImageView());


            Picasso.with(context).load("http://a1professionals.net/tosstra/assets/usersImg/profileImg36euH.jpg").into(imageUser,new InfoWindowRefresher(marker));


            Log.e("imggg",data.getCompanyName());
           /* Glide
                    .with(context)
                    .load(markerDetails.getImageView())
                    .centerCrop()
                    .placeholder(R.mipmap.image)
                    .into(imageUser);*/

        } catch (Exception e) {
            e.printStackTrace();
        }
        return popup;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    public class InfoWindowRefresher implements Callback {
        private Marker markerToRefresh;

        public InfoWindowRefresher(Marker markerToRefresh) {
            this.markerToRefresh = markerToRefresh;
        }

        @Override
        public void onSuccess() {
            markerToRefresh.showInfoWindow();
        }

        @Override
        public void onError() {}
    }
}