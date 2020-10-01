package com.app.tosstra.activities;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.app.tosstra.R;
import com.app.tosstra.googleMaps.FetchURL;
import com.app.tosstra.googleMaps.TaskLoadedCallback;
import com.app.tosstra.models.AllJobsToDriver;
import com.app.tosstra.services.Interface;
import com.app.tosstra.utils.CommonUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeeDriverLocation extends AppCompatActivity implements TaskLoadedCallback, View.OnClickListener,
        LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {
    private GoogleMap mMap,mMap1;
    private MarkerOptions place1, place2, place3;
    private Polyline currentPolyline;
    private ImageView iv_back_addJob;
    private String cur_lat;
    private String cur_lon;
    private String dr_lat;
    private String dr_lon;
    private String dri_lat, dri_lon;
    String dis_id, job_id,dri_id;
    private int pos = 0;
    LocationManager locationManager;
    GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_LOCATION = 1;
    private static final String TAG = "MainActivity";
    int count = 0;
    private ImageView refresh_driver;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initUI();
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapNearBy);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap1=googleMap;
                place1 = new MarkerOptions().position(new LatLng(Double.parseDouble(cur_lat),Double.parseDouble(cur_lon)));
                place2 = new MarkerOptions().position(new LatLng(Double.parseDouble(dr_lat), Double.parseDouble(dr_lon)));
                mMap1.addMarker(place1);
                mMap1.addMarker(place2);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new FetchURL(SeeDriverLocation.this).execute(getUrl(place1.getPosition(), place2.getPosition()), "driving");
                    }
                }, 1000);
            }

        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationPermission();
        }

        job_detail();
    }

    private void initUI() {
        refresh_driver=findViewById(R.id.refresh_driver);
        refresh_driver.setOnClickListener(this);
        iv_back_addJob = findViewById(R.id.iv_back_addJob);
        iv_back_addJob.setOnClickListener(this);
        dis_id = getIntent().getStringExtra("dis_id");
        dri_id = getIntent().getStringExtra("dri_id");
        job_id = getIntent().getStringExtra("job_id");
        cur_lat=getIntent().getStringExtra("pup_lat");
        cur_lon=getIntent().getStringExtra("pup_lon");
        dr_lat=getIntent().getStringExtra("dr_lat");
        dr_lon=getIntent().getStringExtra("dr_lon");
        Log.e("pup_lat", String.valueOf(cur_lat));
        Log.e("pup_lon", String.valueOf(cur_lon));
        Log.e("jobid", String.valueOf(job_id));
        Log.e("disid", String.valueOf(dis_id));
    }

    @Override
    public void onResume() {
        super.onResume();
        //Now lets connect to the API
        mGoogleApiClient.connect();
        //  mapFragment.getMapAsync(this);
        //   hitAllJobsToDriverAPI();
    }


    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void locationPermission() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        checkLocation(); //c
        getLocation();
    }

    private void getLocation() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        onLocationChanged(location);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        startLocationUpdates();

        Location mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation == null) {
            startLocationUpdates();
        }
        if (mLocation != null) {

        } else {

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void startLocationUpdates() {
        /* 10 secs */
        long UPDATE_INTERVAL = 2 * 1000;
        long FASTEST_INTERVAL = 2000;
        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            if (mMap != null) {
              //  mMap.clear();
                //job_detail();
            }
        } else {
            checkLocation();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }


    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        checkLocation();
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        });
                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // locationPermission();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private String getUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=" + "driving";
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.refresh_driver:
                if(mMap1!=null)
                    mMap1.clear();
                MapFragment mapFragment = (MapFragment) getFragmentManager()
                        .findFragmentById(R.id.mapNearBy);
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        mMap1=googleMap;
                        place1 = new MarkerOptions().position(new LatLng(Double.parseDouble(cur_lat),Double.parseDouble(cur_lon)));
                        place2 = new MarkerOptions().position(new LatLng(Double.parseDouble(dr_lat), Double.parseDouble(dr_lon)));
                        mMap1.addMarker(place1);
                        mMap1.addMarker(place2);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new FetchURL(SeeDriverLocation.this).execute(getUrl(place1.getPosition(), place2.getPosition()), "driving");
                            }
                        }, 1000);
                    }

                });

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    locationPermission();
                }

                job_detail();

                break;
            case R.id.iv_back_addJob:
                finish();
                break;
        }
    }

    private void job_detail() {
        final Dialog dialog = AppUtil.showProgress(SeeDriverLocation.this);
        Interface service = CommonUtils.retroInit();
        Call<AllJobsToDriver> call = service.single_job_detail(job_id, dis_id, dri_id);
        call.enqueue(new Callback<AllJobsToDriver>() {
            @Override
            public void onResponse(Call<AllJobsToDriver> call, Response<AllJobsToDriver> response) {
                AllJobsToDriver data = response.body();
                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {
                    dialog.dismiss();
                    dri_lat = data.getData().get(pos).getDriverlatitude();
                    dri_lon = data.getData().get(pos).getDriverlongitude();
               //     CommonUtils.showLongToast(SeeDriverLocation.this, data.getStatus());

                    Log.e("dr_lat", String.valueOf(dri_lat));
                    Log.e("dr_lon", String.valueOf(dr_lon));

                    MapFragment mapFragment = (MapFragment) getFragmentManager()
                            .findFragmentById(R.id.mapNearBy);
                    if (mapFragment != null)
                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                mMap = googleMap;
                                boolean success = googleMap.setMapStyle(
                                        MapStyleOptions.loadRawResourceStyle(
                                                SeeDriverLocation.this, R.raw.style_json));
                                if(!dri_lat.isEmpty()){
                                    place3 = new MarkerOptions().position(new LatLng(Double.parseDouble(dri_lat), Double.parseDouble(dri_lon))).title("Driver Current Location");
                                    mMap.addMarker(place3);
                                    Log.e("driiii", String.valueOf(dri_lat));
                                    CameraPosition googlePlex = CameraPosition.builder()
                                            .target(new LatLng(Double.parseDouble(dri_lat), Double.parseDouble(dri_lon)))
                                            .zoom(15)
                                            .tilt(45)
                                            .build();
                                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
                                    googleMap.addMarker(place3);
                                    googleMap.getUiSettings().isMyLocationButtonEnabled();
                                    googleMap.getUiSettings().isZoomControlsEnabled();
                                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                                }



                            }
                        });
                    Log.e("dri_lttt", String.valueOf(dri_lat));



                  /*  new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new FetchURL(SeeDriverLocation.this).execute(getUrl(place1.getPosition(), place2.getPosition()), "driving");
                        }
                    }, 1000);*/
                } else {
                    dialog.dismiss();
                    CommonUtils.showLongToast(SeeDriverLocation.this, data.getStatus());
                }
            }

            @Override
            public void onFailure(Call<AllJobsToDriver> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(SeeDriverLocation.this, t.getMessage());
            }
        });
    }
}