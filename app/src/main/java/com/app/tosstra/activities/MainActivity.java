package com.app.tosstra.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.app.tosstra.R;
import com.app.tosstra.models.Profile;
import com.app.tosstra.services.Interface;
import com.app.tosstra.utils.CommonUtils;
import com.app.tosstra.utils.MyGps;
import com.app.tosstra.utils.PreferenceHandler;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import java.io.Serializable;
import java.util.prefs.PreferenceChangeEvent;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.tosstra.utils.MyGps.cTimer;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private NavController navController;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ProgressBar progressBar1;
    private ImageView imageOpen, userPic, userPicBack;
    private TextView title_tv, nameUser;
    private TextView company_name;
    //public static String userType;
    private MenuItem activeTruckOrAllJobs, myJobOrAddNewJob, activeDriverDis;
    private String image_url;
    private String company;
    BroadcastReceiver receiver;
    private ImageView refresh, refreshhome;
    private CircleImageView imageUser;
    public static ImageView refresh_active;
    private String userT;
    public static String online_stauis;
    // private MyGps gpsTracker;
    private String nameofuser;
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //gpsTracker = new MyGps(MainActivity.this);
        String userType = PreferenceHandler.readString(this, "user_typp", "");
        PreferenceHandler.writeString(MainActivity.this, "userType", userType);
        userT = PreferenceHandler.readString(MainActivity.this, "userType", "");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }
        initUI();
        setDrawer();
        Menu menu = navigationView.getMenu();
        activeTruckOrAllJobs = menu.findItem(R.id.activetrucks);
        myJobOrAddNewJob = menu.findItem(R.id.addjob);
        activeDriverDis = menu.findItem(R.id.activedrivers);
        View headerView = navigationView.getHeaderView(0);
        company_name = headerView.findViewById(R.id.company_name);
        nameUser = (TextView) headerView.findViewById(R.id.nameUser);
        imageUser = headerView.findViewById(R.id.imageUser);
        hitProfileViewAPI();

        if (nameofuser != null) {

        }


        if (userT.equalsIgnoreCase("Dispatcher")) {
            title_tv.setText("All available trucks");
        }
        Log.e("user_T", userT);
        if (userT.equalsIgnoreCase("Driver")) {
            activeTruckOrAllJobs.setTitle("All Jobs");
            myJobOrAddNewJob.setTitle("My Jobs");
            activeDriverDis.setVisible(false);
            title_tv.setText("All Jobs");
            // nameUser.setText("Driver");
            Log.e("User_id", PreferenceHandler.readString(MainActivity.this, PreferenceHandler.USER_ID, ""));
            navController.navigate(R.id.allJobsFragments);
        } else if (userT.equalsIgnoreCase("Dispatcher")) {
            title_tv.setText("All available trucks");
            activeTruckOrAllJobs.setTitle("Available Trucks");
            myJobOrAddNewJob.setVisible(false);
            activeDriverDis.setTitle("Active Driver");
            //  nameUser.setText("Dispatcher");
            userPic.setVisibility(View.GONE);
            navController.navigate(R.id.activeTrucksFragments);
        }

        image_url = PreferenceHandler.readString(this, "profile_url", "");
        company = PreferenceHandler.readString(this, "company", "");

        Log.e("img", String.valueOf(image_url));


        imageOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

    }


    NavController.OnDestinationChangedListener onDestinationChangedListener = new NavController.OnDestinationChangedListener() {
        @Override
        public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
            if (destination.getId() == R.id.notificationFragment || destination.getId() == R.id.myJobFragments ||
                    destination.getId() == R.id.profileFragment || destination.getId() == R.id.settingsFragment
                    || destination.getId() == R.id.activerDriverFragments || destination.getId() == R.id.allJobsFragments
                    || destination.getId() == R.id.activeTrucksFragments) {
                userPic.setVisibility(View.GONE);
                imageOpen.setVisibility(View.GONE);
                refresh.setVisibility(View.GONE);
                userPicBack.setVisibility(View.VISIBLE);
                refresh_active.setVisibility(View.GONE);

            } else {
                userPic.setVisibility(View.VISIBLE);
                imageOpen.setVisibility(View.VISIBLE);
                userPicBack.setVisibility(View.GONE);
                refresh_active.setVisibility(View.GONE);
            }

            if (destination.getId() == R.id.activeTrucksFragments || destination.getId() == R.id.allJobsFragments) {
                imageOpen.setVisibility(View.VISIBLE);
                userPicBack.setVisibility(View.GONE);
                userPic.setVisibility(View.VISIBLE);
                if (userT.equalsIgnoreCase("Driver")) {
                    refreshhome.setVisibility(View.VISIBLE);
                }
            }
            if (destination.getId() == R.id.activeTrucksFragments) {
                imageOpen.setVisibility(View.VISIBLE);
                userPicBack.setVisibility(View.GONE);
                userPic.setVisibility(View.GONE);
            }

        }
    };

    private void hitProfileViewAPI() {
        final Dialog dialog = AppUtil.showProgress(MainActivity.this);
        Interface service = CommonUtils.retroInit();
        Call<Profile> call = service.view_profile(PreferenceHandler.readString(MainActivity.this, PreferenceHandler.USER_ID, ""));
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                Profile data = response.body();
                if (data != null) {
                    if (data.getCode().equalsIgnoreCase("201")) {
                        dialog.dismiss();
                        online_stauis = data.getData().get(0).getOnlineStatus();
                        nameofuser = data.getData().get(0).getFirstName() + " " + data.getData().get(0).getLastName();
                        company_name.setText(data.getData().get(0).getCompanyName());
                        nameUser.setText(data.getData().get(0).getFirstName() + " " + data.getData().get(0).getLastName());

                        Glide.with(MainActivity.this).load("http://a1professionals.net/tosstra/assets/usersImg/" + data.getData().get(0).getProfileImg())
                                .centerCrop()
                                .placeholder(R.drawable.profile_image_placeholder)
                                .into(imageUser);


                    } else {
                        dialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                dialog.dismiss();
                // CommonUtils.showSmallToast(getContext(), t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            //finish();
        }
    }


    private void initUI() {
        navController = Navigation.findNavController(MainActivity.this, R.id.mainNavFragment2);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
//        progressBar1 = findViewById(R.id.pro1);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title_tv = findViewById(R.id.title_tv);
        imageOpen = findViewById(R.id.imageOpen);
        userPic = findViewById(R.id.userPic);
        userPicBack = findViewById(R.id.userPicBack);
        userPic.setOnClickListener(this);
        userPicBack.setOnClickListener(this);
        refresh = findViewById(R.id.refresh1);
        refresh_active = findViewById(R.id.refresh_active);
        refreshhome = findViewById(R.id.refreshhome);
        refreshhome.setOnClickListener(this);
        refresh_active.setOnClickListener(this);
        refresh.setOnClickListener(this);
    }


    private void setDrawer() {
        navigationView.setItemIconTintList(null);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
        navController.getCurrentDestination().getId();
        navController.addOnDestinationChangedListener(onDestinationChangedListener);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);
        drawerLayout.closeDrawers();
        int id = menuItem.getItemId();
        refresh_active.setVisibility(View.GONE);
        switch (id) {

            case R.id.activetrucks:
                if (userT.equalsIgnoreCase("driver")) {
                    title_tv.setText("All Jobs");
                    navController.navigate(R.id.allJobsFragments);

                } else {
                    navController.navigate(R.id.activeTrucksFragments);
                    activeTruckOrAllJobs.setTitle("Available Trucks");
                    title_tv.setText("All available trucks");
                }
                break;
            case R.id.addjob:
                if (userT.equalsIgnoreCase("driver")) {
                    navController.navigate(R.id.myJobFragments);
                    title_tv.setText("My Jobs");
                    refreshhome.setVisibility(View.GONE);
                } else {
                    navController.navigate(R.id.addAnewJobFragments);
                    title_tv.setText("Add a new Job");
                }
                break;

            case R.id.activedrivers:
                title_tv.setText("Active Drivers on job");
                navController.navigate(R.id.activerDriverFragments);
                refresh_active.setVisibility(View.VISIBLE);
                refreshhome.setVisibility(View.GONE);
                break;

            case R.id.profile:
                title_tv.setText("Profile");
                navController.navigate(R.id.profileFragment);
                refreshhome.setVisibility(View.GONE);
                break;

            case R.id.notification:
                title_tv.setText("Notifications");
                navController.navigate(R.id.notificationFragment);
                refreshhome.setVisibility(View.GONE);
                break;


            case R.id.settings:
                title_tv.setText("Settings");
                navController.navigate(R.id.settingsFragment);
                refreshhome.setVisibility(View.GONE);
                break;


        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        PreferenceHandler.writeString(MainActivity.this, "map", "false");
        // gpsTracker.stopUsingGPS();
    }


    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(MainActivity.this, R.id.mainNavFragment2), drawerLayout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userPic:
                Intent i = new Intent(this, ProfileActivity.class);
                startActivity(i);
                break;
            case R.id.userPicBack:
                image_url = PreferenceHandler.readString(this, "profile_url", "");
                company = PreferenceHandler.readString(this, "company", "");
                // company_name.setText(company);
                Log.e("img", String.valueOf(image_url));

                Glide.with(this)
                        .load("http://a1professionals.net/tosstra/assets/usersImg/" + image_url)
                        .centerCrop()
                        .placeholder(R.drawable.profile_image_placeholder)
                        .into(imageUser);

                drawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.refresh1:
                if (PreferenceHandler.readString(MainActivity.this, "map", "").equalsIgnoreCase("true")) {
                    title_tv.setText("All Jobs");
                    navController.navigate(R.id.allJobsFragments);
                    //CommonUtils.showSmallToast(MainActivity.this, "REfresh");
                    break;
                }
            case R.id.refresh_active:
                title_tv.setText("Active Drivers on job");
                Bundle bundle = new Bundle();
                bundle.putString("map", "1");
                navController.navigate(R.id.activerDriverFragments, bundle);
                //  CommonUtils.showSmallToast(MainActivity.this, "REfresh");
                //   refresh_active.setVisibility(View.VISIBLE);
                break;
            case R.id.refreshhome:
                // CommonUtils.showSmallToast(MainActivity.this, "REfresh");
                title_tv.setText("All Jobs");
                navController.navigate(R.id.allJobsFragments);
                break;

        }
    }

}