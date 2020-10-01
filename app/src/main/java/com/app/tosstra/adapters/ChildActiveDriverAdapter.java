package com.app.tosstra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tosstra.R;
import com.app.tosstra.activities.RecoverPasswordActivity;
import com.app.tosstra.models.GenricModel;
import com.app.tosstra.services.Interface;
import com.app.tosstra.utils.CommonUtils;
import com.app.tosstra.utils.PreferenceHandler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChildActiveDriverAdapter extends RecyclerView.Adapter<ChildActiveDriverAdapter.ViewHolder> {

    public ChildActiveDriverAdapter(Context context) {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_truck_item, parent, false);
        return new ChildActiveDriverAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_header1.setText("Blue Transporation");
        holder.tv_header2.setText("Mike");
    }


    @Override
    public int getItemCount() {
        return 10;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tv_header1, tv_header2;
        private ImageView ivFav;

        public ViewHolder(View v) {
            super(v);
            tv_header1 = v.findViewById(R.id.tv_header1);
            tv_header2 = v.findViewById(R.id.tv_header2);
            ivFav=v.findViewById(R.id.iv_fv);
         //   ivFav.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_fv:
                  //  hitFavUnFav();
                    break;
            }
        }
    }


}

