package com.app.tosstra.fragments.driver;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.tosstra.R;
import com.app.tosstra.models.AllJobsToDriver;
import com.app.tosstra.models.Person;
import com.app.tosstra.utils.CustomInfoViewAdapter;
import com.app.tosstra.utils.MyGps;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;


public class AllJobFragmentNew extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener {

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView recyclerView;
    private ImageView fab;
    private ImageView back_click;
    private GoogleMap mMap;
    private int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private double longitude = 0;
    private double latitude = 0;
    private LatLng latLng;
    private NavController navController;
    private GoogleApiClient googleApiClient;
    private AllJobsToDriver data;
    private List<Person> personList = new ArrayList<>();
    private AlertDialog pd;
    private ClusterManager<Person> mClusterManager;
    private List<Person> mClusterManagerTemp = new ArrayList<>();
    private List<Person> mClusterManagerTemp2 = new ArrayList<>();
    //private ClusterAdapter clusterAdapter;
    // private ClusterAdapterRetailer clusterAdapterRetailer;
    View locationButton;
    //private ResponseData responseData;
    //private UserLogin userLoginData;
    private boolean valueMove = false;
    Location startPoint = new Location("locationA");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.all_jobs_fragment, container, false);
        locationButton = ((View) view.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        LatLng latLng = new LatLng(30.567, 76.9657);
        mClusterManager.addItem(new Person(latLng));

        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 30, 30, 0);
        if (mClusterManager == null) {
            mClusterManager = new ClusterManager<>(getContext(), googleMap);
        }


        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.cluster();


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
//                recyclerView.setVisibility(View.GONE);
            }
        });


        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                valueMove = true;

            }
        });


        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    try {
                        latLng = mMap.getCameraPosition().target;
                        Location endPoint = new Location("locationA");
                        endPoint.setLatitude(30.7191);
                        endPoint.setLongitude(76.7487);
                        double distance = startPoint.distanceTo(endPoint);
                        if (distance != 0.0) {
                            if (distance > 5200) {
                                valueMove = false;
                            } else {
                                valueMove = true;
                            }
                        } else {
                            valueMove = false;
                        }
                        if (latLng != null && distance != 0.0) {

                            Log.e("distance", "==" + distance);
                            //   getGroupList(latLng);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.e("move", "The user gestured on the map.");
                } else if (reason == GoogleMap.OnCameraMoveStartedListener
                        .REASON_API_ANIMATION) {
                    Log.e("move", "The user tapped something on the map.");
                } else if (reason == GoogleMap.OnCameraMoveStartedListener
                        .REASON_DEVELOPER_ANIMATION) {
                    Log.e("move", "The app moved the camera.");
                }
            }
        });


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }


}
