package com.app.tosstra.services;


import com.app.tosstra.fragments.common.TestModel;
import com.app.tosstra.models.AllDriverNew;
import com.app.tosstra.models.AllDrivers;
import com.app.tosstra.models.AllJobsToDriver;
import com.app.tosstra.models.GenricModel;
import com.app.tosstra.models.NotificationModel;
import com.app.tosstra.models.Profile;
import com.app.tosstra.models.SIgnUp;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Interface {

    @FormUrlEncoded
    @POST("driver-Register")
    Call<SIgnUp> signUpDriver(@Field("firstName") String firstName,
                              @Field("lastName") String lastName,
                              @Field("companyName") String companyName,
                              @Field("email") String email,
                              @Field("password") String password,
                              @Field("dotNumber") String dotNumber,
                              @Field("userType") String userType,
                              @Field("phone") String phone,
                              @Field("SubUserType") String SubUserType);

    @FormUrlEncoded
    @POST("dispatcher-Register")
    Call<SIgnUp> signUpDispacher(@Field("firstName") String firstName,
                                 @Field("lastName") String lastName,
                                 @Field("companyName") String companyName,
                                 @Field("email") String email,
                                 @Field("password") String password,
                                 @Field("dotNumber") String dotNumber,
                                 @Field("phone") String phone,
                                 @Field("userType") String userType,
                                 @Field("SubUserType") String SubUserType);

    @FormUrlEncoded
    @POST("user-Login")
    Call<SIgnUp> signIn(@Field("email") String email,
                        @Field("password") String password,
                        @Field("deviceId") String deviceId,
                        @Field("deviceType") String deviceType,
                        @Field("timeZone") String timeZone,
                        @Field("latitude") String latitude,
                        @Field("longitude") String longitude);


    @FormUrlEncoded
    @POST("view-Profile")
    Call<Profile> view_profile(@Field("userId") String userId);

    @Multipart
    @POST("edit-Profile")
    Call<GenricModel> edit_profile(@Part("userId") RequestBody userId,
                                   @Part MultipartBody.Part profileImg,
                                   @Part("firstName") RequestBody firstName,
                                   @Part("lastName") RequestBody lastName,
                                   @Part("companyName") RequestBody companyName,
                                   @Part("dotNumber") RequestBody dotNumber,
                                   @Part("address") RequestBody address,
                                   @Part("phone") RequestBody phone,
                                   @Part("latitude") RequestBody latitude,
                                   @Part("longitude") RequestBody longitude
    );

    @FormUrlEncoded
    @POST("user-logOut")
    Call<GenricModel> logout(@Field("userId") String userId);

    @FormUrlEncoded
    @POST("delete-Account")
    Call<GenricModel> deleteAccount(@Field("userId") String userId);

    @FormUrlEncoded
    @POST("forgot-Password")
    Call<GenricModel> forgot_password(@Field("email") String email);

    @FormUrlEncoded
    @POST("recover-Passwrod")
    Call<GenricModel> recover_password(@Field("otp") String otp,
                                       @Field("password") String password,
                                       @Field("confirm_password") String confirm_password);

    @FormUrlEncoded
    @POST("change-Password")
    Call<GenricModel> change_password(@Field("userId") String userId,
                                      @Field("oldPassword") String oldPassword,
                                      @Field("newPassword") String newPassword,
                                      @Field("confirm_password") String confirm_password);

    @FormUrlEncoded
    @POST("get-All-Drivers")
    Call<AllDrivers> getAllDrivers(@Field("userId") String userId,
                                   @Field("latitude") String latitude,
                                   @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("active-Driver-List")
    Call<AllDrivers> active_drivers(@Field("userId") String userId);

    @FormUrlEncoded
    @POST("create-Job-By-dispatcher")
    Call<GenricModel> createJpbDisp(@Field("dispatcherId") String dispatcherId,
                                    @Field("rateType") String rateType,
                                    @Field("rate") String rate,
                                    @Field("pupStreet") String pupStreet,
                                    @Field("pupCity") String pupCity,
                                    @Field("pupState") String pupState,
                                    @Field("pupZipcode") String pupZipcode,
                                    @Field("drpStreet") String drpStreet,
                                    @Field("drpCity") String drpCity,
                                    @Field("drpState") String drpState,
                                    @Field("drpZipcode") String drpZipcode,
                                    @Field("dateFrom") String dateFrom,
                                    @Field("dateTo") String dateTo,
                                    @Field("startTime") String startTime,
                                    @Field("endTime") String endTime,
                                    @Field("additinal_Instructions") String additinal_Instructions,
                                    @Field("offerForSelectedDrivers") String offerForSelectedDrivers,
                                    @Field("puplatitude") String puplatitude,
                                    @Field("puplongitude") String puplongitude,
                                    @Field("drplatitude") String drplatitude,
                                    @Field("drplongitude") String drplongitude);

    @FormUrlEncoded
    @POST("driver-Favorite-Unfavorite")
    Call<GenricModel> favUnFav(@Field("userId") String userId,
                               @Field("driverId") String driverId);

    @FormUrlEncoded
    @POST("get-Only-FavDrivers")
    Call<AllDrivers> onlyFav(@Field("userId") String userId,
                             @Field("latitude") String latitude,
                             @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("get-All-Jobs-To-drivers")
    Call<AllJobsToDriver> allJobsToDriver(
            @Field("userId") String userId,
            @Field("latitude") double latitude,
            @Field("longitude") double longitude,
            @Field("distance") String distance);

    @FormUrlEncoded
    @POST("get-Our-All-Jobs")
    Call<AllJobsToDriver> ourJobs(
            @Field("userId") String userId);

    @FormUrlEncoded
    @POST("job-Accecpt-Reject")
    Call<GenricModel> acceptReject(@Field("driverId") String driverId,
                                   @Field("jobId") String jobId,
                                   @Field("jobStatus") String jobStatus,
                                   @Field("dispatcherId") String dispatcherId);

    @FormUrlEncoded
    @POST("job-Complete-Status")
    Call<AllJobsToDriver> job_complete(@Field("userId") String userId,
                                       @Field("jobId") String jobId);

    @FormUrlEncoded
    @POST("change-Online-Status")
    Call<GenricModel> onlone_status(@Field("userId") String userId,
                                    @Field("onlineStatus") String onlineStatus,
                                    @Field("latitude") double latitude,
                                    @Field("longitude") double longitude);

    @FormUrlEncoded
    @POST("end-Driver-Job")
    Call<GenricModel> end_driver_job(@Field("dispatcherId") String dispatcherId,
                                     @Field("driverId") String driverId,
                                     @Field("jobId") String jobId);

    @FormUrlEncoded
    @POST("work-Start-Status")
    Call<GenricModel> start_job(@Field("userId") String userId,
                                @Field("jobId") String jobId);

    @FormUrlEncoded
    @POST("get-Single-Job-Data")
    Call<AllDriverNew> single_job_detail1(@Field("jobId") String jobId,
                                          @Field("dispatcherId") String dispatcherId,
                                          @Field("driverId") String driverId);

    @FormUrlEncoded
    @POST("get-Single-Job-Data")
    Call<AllJobsToDriver> single_job_detail(@Field("jobId") String jobId,
                                            @Field("dispatcherId") String dispatcherId,
                                            @Field("driverId") String driverId);


    @FormUrlEncoded
    @POST("get-Driver-Nofitications")
    Call<NotificationModel> noti(@Field("userId") String userId);

    @FormUrlEncoded
    @POST("get-Dispatcher-Nofitications")
    Call<NotificationModel> noti_dis(@Field("userId") String userId);

    @FormUrlEncoded
    @POST("driver-Location-Point")
    Call<AllJobsToDriver> notification_same_loc(@Field("driverId") String driverId);


    @FormUrlEncoded
    @POST("user-Payment")
    Call<GenricModel> user_payment(@Field("user_id") String user_id,
                                   @Field("payment_id") String payment_id,
                                   @Field("pay_success") String pay_success,
                                   @Field("payment_status") String payment_status,
                                   @Field("amount") String amount,
                                   @Field("plan") String plan,
                                   @Field("purchaseDate") String purchaseDate,
                                   @Field("expiryDate") String expiryDate);

    @FormUrlEncoded
    @POST("passenger/mobile")
    Call<TestModel> test(@Field("emergTwo_isdCode") String emergTwo_isdCode,
                         @Field("emergOne_id") String emergOne_id,
                         @Field("emergOne_isdCode") String emergOne_isdCode,
                         @Field("emergTwo_id") String emergTwo_id,
                         @Field("isdCode") String isdCode,
                         @Field("emergOne_contact_no") String emergOne_contact_no,
                         @Field("emergTwo_contact_no") String emergTwo_contact_no,
                         @Field("emergOne_name") String emergOne_name,
                         @Field("emergTwo_name") String emergTwo_name,
                         @Field("mobile_no") String mobile_no);
}
