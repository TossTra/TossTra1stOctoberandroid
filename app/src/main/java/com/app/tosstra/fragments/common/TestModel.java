package com.app.tosstra.fragments.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TestModel {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("errors")
    @Expose
    private Errors errors;

    public class Data{
        @SerializedName("otp")
        @Expose
        private Integer otp;

        public Integer getOtp() {
            return otp;
        }

        public void setOtp(Integer otp) {
            this.otp = otp;
        }
    }

    public class Errors {
        @SerializedName("exception")
        @Expose
        private List<String> exception = null;
        @SerializedName("error")
        @Expose
        private List<Object> error = null;

        public List<String> getException() {
            return exception;
        }

        public void setException(List<String> exception) {
            this.exception = exception;
        }

        public List<Object> getError() {
            return error;
        }

        public void setError(List<Object> error) {
            this.error = error;
        }
    }
}
