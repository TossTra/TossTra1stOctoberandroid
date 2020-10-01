package com.app.tosstra.fragments.dispacher;

import android.app.Dialog;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.tosstra.R;
import com.app.tosstra.ViewPagerMap.VPMapAdapter;
import com.app.tosstra.activities.AppUtil;
import com.app.tosstra.activities.JobDetailActivity;
import com.app.tosstra.activities.JobDetailForDriver;
import com.app.tosstra.activities.JobDetailMapClick;
import com.app.tosstra.activities.JobOfferForMyJob;
import com.app.tosstra.activities.PagerContainer;
import com.app.tosstra.models.AllDrivers;
import com.app.tosstra.models.MarkerDetails;
import com.app.tosstra.services.Interface;
import com.app.tosstra.utils.CommonUtils;
import com.app.tosstra.utils.CustomInfoViewAdapter;
import com.app.tosstra.utils.CustomInfoViewAdapterMap;
import com.app.tosstra.utils.PreferenceHandler;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapViewFragment extends Fragment implements View.OnClickListener {
    private GoogleMap mMap;

    ArrayList<String> lat = new ArrayList<String>();
    ArrayList<String> lon = new ArrayList<String>();
    ArrayList<String> address = new ArrayList<String>();
    private MarkerOptions place1, place2;
    LocationManager locationManager;
    GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_LOCATION = 1;
    private static final String TAG = "MainActivity";
    VPMapAdapter vpAdapter;
    ViewPager vp_home;
    PagerContainer mContainer;
    AllDrivers data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
       initUI(view);
        hitActiveDriverAPI();
        return view;
    }

    private void initUI(View view) {
        mContainer = view.findViewById(R.id.pager_container4);
        vp_home = view.findViewById(R.id.pager4);
    }

    @Override
    public void onResume() {
        super.onResume();
        hitActiveDriverAPI();
    }

    /*@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            hitActiveDriverAPI();
        }
    }*/





    private void hitActiveDriverAPI() {
        final Dialog dialog = AppUtil.showProgress(getActivity());
        Interface service = CommonUtils.retroInit();
        Call<AllDrivers> call = service.active_drivers(PreferenceHandler.readString(getContext(), PreferenceHandler.USER_ID, ""));
        call.enqueue(new Callback<AllDrivers>() {
            @Override
            public void onResponse(Call<AllDrivers> call, Response<AllDrivers> response) {
                data = response.body();
                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {
                    for (int i = 0; i < data.getData().size(); i++) {
                        lat.addAll(Collections.singleton(data.getData().get(i).getLatitude()));
                        lon.addAll(Collections.singleton(data.getData().get(i).getLongitude()));
                        address.addAll(Collections.singleton(data.getData().get(i).getCompanyName()));
                    }
                    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            if(data!=null)
                            setViewPager(data);
                            int i;
                            for (i = 0; i < data.getData().size(); i++) {
                                if (!lat.get(i).isEmpty() && !lon.get(i).isEmpty()) {
                                    LatLng cur = new LatLng(Double.parseDouble(lat.get(i)), Double.parseDouble(lon.get(i)));
                                    place1 = new MarkerOptions().position(cur).
                                            title(address.get(i)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker)).snippet(String.valueOf(i));
                                    final MarkerDetails markerDetails = new MarkerDetails();
                                    markerDetails.setJob_id(data.getData().get(i).getJobId());
                                    markerDetails.setDis_id(data.getData().get(i).getDispatcherId());
                                    markerDetails.setDri_id(data.getData().get(i).getDriverId());
                                    markerDetails.setImageView("http://a1professionals.net/tosstra/assets/usersImg/"+data.getData().get(i).getProfileImg());

                                    CustomInfoViewAdapterMap customInfoWindow = new CustomInfoViewAdapterMap(LayoutInflater.from(getContext()), getContext(), data.getData().get(i));
                                    googleMap.setInfoWindowAdapter(customInfoWindow);
                                    Marker m = googleMap.addMarker(place1);
                                    m.setTag(markerDetails);
                                    m.showInfoWindow();
                                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                        @Override
                                        public void onInfoWindowClick(Marker marker) {
                                            int pos= Integer.parseInt(marker.getSnippet());
                                            // CommonUtils.showSmallToast(getActivity(),marker.getSnippet());

                                            Intent i = new Intent(getContext(), JobDetailForDriver.class);
                                            i.putExtra("job_id",data.getData().get(pos).getJobId());
                                            i.putExtra("disp_id",data.getData().get(pos).getDispatcherId());
                                            i.putExtra("dri_id",data.getData().get(pos).getDriverId());
                                            i.putExtra("job_offer", data);
                                            i.putExtra("job_offer_pos", pos);
                                            i.putExtra("map_vp_detail","2");
                                            startActivity(i);

                                          /*  intent.putExtra("job_id", data.getData().get(pos).getJobId());
                                            intent.putExtra("disp_id", data.getData().get(pos).getDispatcherId());
                                            intent.putExtra("dri_id", data.getData().get(pos).getDriverId());
                                            intent.putExtra("marker", "1");
                                            intent.putExtra("end_job","1");*/

                                        }
                                    });


                                    CameraPosition googlePlex = CameraPosition.builder()
                                            .target(cur)
                                            .zoom(12)
                                            .bearing(0)
                                            .tilt(45)
                                            .build();
                                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
                                    googleMap.getUiSettings().isMyLocationButtonEnabled();
                                    googleMap.getUiSettings().isZoomControlsEnabled();
                                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                                    // setViewPager(data);
                                }


                                //  googleMap.addMarker(place1);
                            }


                        }
                    });
                    dialog.dismiss();
                 //   CommonUtils.showLongToast(getContext(), data.getMessage());

                } else {
                    dialog.dismiss();
                  //  CommonUtils.showLongToast(getContext(), data.getMessage());
                }
            }

            @Override
            public void onFailure(Call<AllDrivers> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(getContext(), t.getMessage());
            }
        });
    }

    private void setViewPager(AllDrivers data) {
        vp_home = mContainer.getViewPager();
        vpAdapter = new VPMapAdapter(getFragmentManager(), data);
        vp_home.setAdapter(vpAdapter);
        //   vp_store.setCurrentItem(selectedValue);
        vp_home.setOffscreenPageLimit(5);
        vp_home.setPageMargin(0);
        vp_home.setClipChildren(false);
        vpAdapter.notifyDataSetChanged();

        vp_home.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
       // hitActiveDriverAPI();
    }
}