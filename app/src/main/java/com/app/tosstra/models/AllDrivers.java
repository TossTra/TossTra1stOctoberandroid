package com.app.tosstra.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AllDrivers implements Serializable {
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<Data> data = null;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public class Data implements Serializable {

        @SerializedName("driverId")
        @Expose
        private String driverId;

        @SerializedName("dispatcherId")
        @Expose
        private String dispatcherId;

        @SerializedName("jobId")
        @Expose
        private String jobId;

        @SerializedName("puplatitude")
        @Expose
        private double puplatitude;

        @SerializedName("puplongitude")
        @Expose
        private double puplongitude;

        @SerializedName("drplatitude")
        @Expose
        private double drplatitude;

        @SerializedName("drplongitude")
        @Expose
        private double drplongitude;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("firstName")
        @Expose
        private String firstName;
        @SerializedName("lastName")
        @Expose
        private String lastName;
        @SerializedName("address")
        @Expose
        private Object address;
        @SerializedName("profileImg")
        @Expose
        private String profileImg;
        @SerializedName("companyName")
        @Expose
        private String companyName;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("password")
        @Expose
        private String password;
        @SerializedName("dotNumber")
        @Expose
        private String dotNumber;
        @SerializedName("deviceId")
        @Expose
        private Object deviceId;
        @SerializedName("deviceType")
        @Expose
        private Object deviceType;
        @SerializedName("latitude")
        @Expose
        private String latitude;
        @SerializedName("longitude")
        @Expose
        private String longitude;
        @SerializedName("otp")
        @Expose
        private String otp;
        @SerializedName("verifiedStatus")
        @Expose
        private String verifiedStatus;
        @SerializedName("userType")
        @Expose
        private String userType;
        @SerializedName("SubUserType")
        @Expose
        private String subUserType;
        @SerializedName("phone")
        @Expose
        private String phone;
        @SerializedName("onlineStatus")
        @Expose
        private String onlineStatus;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;

        public String getDriverId() {
            return driverId;
        }

        public void setDriverId(String driverId) {
            this.driverId = driverId;
        }

        public String getDispatcherId() {
            return dispatcherId;
        }

        public void setDispatcherId(String dispatcherId) {
            this.dispatcherId = dispatcherId;
        }

        public String getJobId() {
            return jobId;
        }

        public void setJobId(String jobId) {
            this.jobId = jobId;
        }

        public double getPuplatitude() {
            return puplatitude;
        }

        public void setPuplatitude(double puplatitude) {
            this.puplatitude = puplatitude;
        }

        public double getPuplongitude() {
            return puplongitude;
        }

        public void setPuplongitude(double puplongitude) {
            this.puplongitude = puplongitude;
        }

        public double getDrplatitude() {
            return drplatitude;
        }

        public void setDrplatitude(double drplatitude) {
            this.drplatitude = drplatitude;
        }

        public double getDrplongitude() {
            return drplongitude;
        }

        public void setDrplongitude(double drplongitude) {
            this.drplongitude = drplongitude;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public Object getAddress() {
            return address;
        }

        public void setAddress(Object address) {
            this.address = address;
        }

        public String getProfileImg() {
            return profileImg;
        }

        public void setProfileImg(String profileImg) {
            this.profileImg = profileImg;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDotNumber() {
            return dotNumber;
        }

        public void setDotNumber(String dotNumber) {
            this.dotNumber = dotNumber;
        }

        public Object getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(Object deviceId) {
            this.deviceId = deviceId;
        }

        public Object getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(Object deviceType) {
            this.deviceType = deviceType;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }

        public String getVerifiedStatus() {
            return verifiedStatus;
        }

        public void setVerifiedStatus(String verifiedStatus) {
            this.verifiedStatus = verifiedStatus;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public String getSubUserType() {
            return subUserType;
        }

        public void setSubUserType(String subUserType) {
            this.subUserType = subUserType;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getOnlineStatus() {
            return onlineStatus;
        }

        public void setOnlineStatus(String onlineStatus) {
            this.onlineStatus = onlineStatus;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}
