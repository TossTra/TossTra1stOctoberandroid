package com.app.tosstra.models;


import android.widget.ImageView;

public class MarkerDetails {
    private String job_id;
    private String dis_id;
    private String dri_id;
    private String imageView;
    private String job_start_status;

    public MarkerDetails() {

    }

    public String getJob_start_status() {
        return job_start_status;
    }

    public void setJob_start_status(String job_start_status) {
        this.job_start_status = job_start_status;
    }

    public String getDis_id() {
        return dis_id;
    }

    public void setDis_id(String dis_id) {
        this.dis_id = dis_id;
    }

    public String getDri_id() {
        return dri_id;
    }

    public void setDri_id(String dri_id) {
        this.dri_id = dri_id;
    }

    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }

    public String getImageView() {
        return imageView;
    }

    public void setImageView(String imageView) {
        this.imageView = imageView;
    }
}
