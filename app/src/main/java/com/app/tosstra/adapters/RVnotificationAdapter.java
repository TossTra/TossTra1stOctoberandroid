package com.app.tosstra.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tosstra.R;
import com.app.tosstra.models.NotificationModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class RVnotificationAdapter extends RecyclerView.Adapter<RVnotificationAdapter.notificationAdapterHolder> {
    Context context;
    NotificationModel data;
    public RVnotificationAdapter(Context context, NotificationModel data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public notificationAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new notificationAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull notificationAdapterHolder holder, int position) {
        holder.tvName.setText(data.getData().get(position).getMessage());
       // holder.tvDate.setText(data.getData().get(position).getNotificationDate());
        holder.tvTime.setText(changeTimeUnixToGMT(data.getData().get(position).getCreatedAt()));

    }

    @Override
    public int getItemCount() {
        return data.getData().size();
    }

    public class notificationAdapterHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTime;
        public notificationAdapterHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);

        }
    }

    private String changeTimeUnixToGMT(String ourDate) {
        {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd H:mm:s", Locale.getDefault());
                formatter.setTimeZone(TimeZone.getDefault());
                Date value = formatter.parse(ourDate);
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy, hh:mm a",Locale.getDefault()); //this format changeable
                dateFormatter.setTimeZone(TimeZone.getDefault());
                ourDate = dateFormatter.format(value);
                Log.d("ourDate", ourDate);
            } catch (Exception e) {
                ourDate = "00-00-0000 00:00";
            }
            return ourDate;
        }
    }
}
