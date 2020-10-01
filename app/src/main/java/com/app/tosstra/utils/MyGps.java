package com.app.tosstra.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import com.app.tosstra.activities.AppUtil;
import com.app.tosstra.models.AllJobsToDriver;
import com.app.tosstra.services.Interface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.tosstra.services.TosstraAppInstance.isCounterRunning;

public class MyGps extends Service implements LocationListener {
    private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;
    double latitude_current;
    // flag for network status
    boolean isNetworkEnabled = false;
    double longitude_current;
    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double last_known_latitude; // latitude
    double last_known_longitude; // longitude
    public static CountDownTimer cTimer = null;
    double dist;
    int count = 0;


    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 15 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 5 * 1000; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public MyGps(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    //check the network permission
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) mContext, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            last_known_latitude = location.getLatitude();
                            last_known_longitude = location.getLongitude();
                            if(!isCounterRunning){
                                startTimer();
                            }
                        }
                    }
                }
                if (isGPSEnabled) {
                    if (location == null) {
                        //check the network permission
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) mContext, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                        }
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);
                        Toast.makeText(mContext, "" + "gps_enable", Toast.LENGTH_SHORT).show();
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                last_known_latitude = location.getLatitude();
                                last_known_longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public void startTimer() {
        isCounterRunning=true;
        cTimer = new CountDownTimer(900000, 1000) {
            public void onTick(long millisUntilFinished) {
               // Toast.makeText(mContext, ""+millisUntilFinished, Toast.LENGTH_SHORT).show();
            }
            public void onFinish() {
               // cancel();
                cancelTimer();
                last_known_latitude = latitude_current;
                last_known_longitude = longitude_current;
                if (dist < 1) {
                    try {
                        noti();
                        start();
                    } catch (Exception e) {

                    }

                }
            }
        };
        cTimer.start();
    }

    private void yourOperation() {
        cTimer.start();
    }

    public static void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */

    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates((LocationListener) this);
        }
    }


    /**
     * Function to get latitude
     */

    public double getLatitude() {
        if (location != null) {
            last_known_latitude = location.getLatitude();
        }

        // return latitude
        return last_known_latitude;
    }

    /**
     * Function to get longitude
     */

    public double getLongitude() {
        if (location != null) {
            last_known_longitude = location.getLongitude();
        }

        // return longitude
        return last_known_longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude_current = location.getLatitude();
        longitude_current = location.getLongitude();
        //  Toast.makeText(mContext, "Update location", Toast.LENGTH_SHORT).show();
        //  Log.e("laadd",String.valueOf(last_known_latitude));
        distance(last_known_latitude, last_known_longitude, latitude_current, longitude_current);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private double distance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371; // in miles, change to 6371 for kilometer output
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        dist = earthRadius * c;
        // Toast.makeText(mContext, "sohan" + dist, Toast.LENGTH_SHORT).show();
        return dist;
    }

    private void noti() {
        final Dialog dialog = AppUtil.showProgress((Activity) mContext);
        Interface service = CommonUtils.retroInit();
        Call<AllJobsToDriver> call = service.notification_same_loc(PreferenceHandler.readString(mContext, PreferenceHandler.USER_ID, ""));
        call.enqueue(new Callback<AllJobsToDriver>() {
            @Override
            public void onResponse(Call<AllJobsToDriver> call, Response<AllJobsToDriver> response) {
                AllJobsToDriver data = response.body();
                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {

                    Toast.makeText(mContext, data.getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(mContext, data.getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<AllJobsToDriver> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(MyGps.this, t.getMessage());
            }
        });
    }




    /*private void saveLat(double latitude) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("latitude", String.valueOf(latitude));
        editor.apply();

    }

    private void saveLon(double longitude) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("longitude", String.valueOf(longitude));
        editor.apply();

    }

    private String getLat() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyGps.this);
        return preferences.getString("latitude", null);
    }
    private String getLon() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyGps.this);
        return preferences.getString("longitude", null);
    }
*/
   /* private void hitNoti() {
        final Dialog dialog = AppUtil.showProgress(this);
        Interface service = CommonUtils.retroInit();
        Call<GenricModel> call = service.noti_update(PreferenceHandler.readString(getActivity(), PreferenceHandler.USER_ID, ""));
        call.enqueue(new Callback<GenricModel>() {
            @Override
            public void onResponse(Call<GenricModel> call, Response<GenricModel> response) {
                GenricModel data = response.body();
                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {
                    dialog.dismiss();
                    CommonUtils.showSmallToast(getActivity(), data.getMessage());
                } else {
                    dialog.dismiss();
                    CommonUtils.showSmallToast(getContext(), data.getMessage());
                }
            }
            @Override
            public void onFailure(Call<GenricModel> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(getContext(), t.getMessage());
            }
        });
    }
*/

}