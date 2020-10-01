package com.app.tosstra.fragments.driver;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.app.tosstra.PaymentSecurity.Security;
import com.app.tosstra.activities.AppUtil;
import com.app.tosstra.adapters.MyviewPagerPayment;
import com.app.tosstra.adapters.RVMyJobAdapter;
import com.app.tosstra.R;
import com.app.tosstra.interfaces.RefreshMyJobs;
import com.app.tosstra.interfaces.StartJobInterface;
import com.app.tosstra.models.AllJobsToDriver;
import com.app.tosstra.models.GenricModel;
import com.app.tosstra.models.Profile;
import com.app.tosstra.services.Interface;
import com.app.tosstra.utils.AlarmReceiver;
import com.app.tosstra.utils.CommonUtils;
import com.app.tosstra.utils.MyService;
import com.app.tosstra.utils.PreferenceHandler;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyJobFragment extends Fragment implements PurchasesUpdatedListener {
    RecyclerView rvMyJob;
    RVMyJobAdapter rvMyJobAdapter;
    TextView tvEmptyView;
    private TextView[] dots;
    private LinearLayout dotsLayout;
    int[] layouts = new int[]{
            R.layout.slider_one,
            R.layout.slider_two,
            R.layout.slider_three,
            R.layout.slider_four, R.layout.slider_five};
    private MyviewPagerPayment myviewPagerPayment;
    private BillingClient billingClient;
    protected String base64Key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAno6a8a4/Hdek0ZnmrVuCLGRxItimNolcJYF5MBMg7t0w6BdpkvCLrGQHDxdFvKiXSkMkxgC1hehOQB1WB8jSa9yf2cALpGkEe1+6NjUg9Tf18Bw7jPQ8uFaRJQKpRVuJd4U8xS2Q78adf6zkocCXoAOOBcnxQ7f7R9RkEBH/A27tmPx2Ztija7UAHO1jH8nORqPmhal7FMjZiNOXYmMEv2VsyfydwXUaVG2Y14sdDGrFCL96JcFJBfMGUOweuhatbJRllQ2fkb3+hLv4IaqPp0PFjQ9z1isu9/cmkp5wgazCx8Ri/rveMfi19n6Ck5AoiPGlCAEFVBFtPmvlWA+nswIDAQAB";
    private List<SkuDetails> skuDetailsListTemp = new ArrayList<>();
    private List<String> skuList = new ArrayList<>();
    private String currentDateandTime;
    private String ExpiryDate;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_job, container, false);
        initUI(view);
        setupBillingAccount();
        hitProfileViewAPI();
        hitAllJobsToDriverAPI();


        return view;
    }




    private void initUI(View view) {
        rvMyJob = view.findViewById(R.id.rvMyJob);
        tvEmptyView = view.findViewById(R.id.empty_view);
    }

    private void hitProfileViewAPI() {
        final Dialog dialog = AppUtil.showProgress(getActivity());
        Interface service = CommonUtils.retroInit();
        Call<Profile> call = service.view_profile(PreferenceHandler.readString(getActivity(),
                PreferenceHandler.USER_ID, ""));
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                Profile data = response.body();
                if (data != null) {
                    if (data.getCode().equalsIgnoreCase("201")) {
//                        CommonUtils.showSmallToast(getActivity(), data.getMessage());
                        if (data.getPayment().size()>0){
                            ExpiryDate = data.getPayment().get(0).getExpiryDate();

                        }
                        dialog.dismiss();

                    } else {
                        dialog.dismiss();
                         CommonUtils.showSmallToast(getActivity(), data.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(getActivity(), t.getMessage());
            }
        });
    }

    private void setupBillingAccount() {

        billingClient = BillingClient.newBuilder(getContext()).enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    //      Toast.makeText(getContext(), "Billing Connect", Toast.LENGTH_SHORT).show();
                    planSelectOneMonth("month_to_month_subscription");

                } else {
                    Toast.makeText(getContext(), "Billing not Connect", Toast.LENGTH_SHORT).show();

                }
            }


            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(getContext(), "Billing Disconnected", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        hitAllJobsToDriverAPI();
    }

    private void hitAllJobsToDriverAPI() {
        final Dialog dialog = AppUtil.showProgress(getActivity());
        Interface service = CommonUtils.retroInit();
        Call<AllJobsToDriver> call = service.ourJobs(PreferenceHandler.readString(getContext(), PreferenceHandler.USER_ID, ""));
        call.enqueue(new Callback<AllJobsToDriver>() {
            @Override
            public void onResponse(Call<AllJobsToDriver> call, Response<AllJobsToDriver> response) {
                AllJobsToDriver data = response.body();
                assert data != null;
                if (data.getCode().equalsIgnoreCase("201")) {
                    dialog.dismiss();
                    Collections.reverse(data.getData());
                    rvMyJobAdapter = new RVMyJobAdapter(getActivity(), data, refreshMyJobs,startJobInterface);
                    rvMyJob.setLayoutManager(new LinearLayoutManager(getContext()));
                    rvMyJob.setAdapter(rvMyJobAdapter);
                } else {
                    dialog.dismiss();
                    tvEmptyView.setVisibility(View.VISIBLE);
                    rvMyJob.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<AllJobsToDriver> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(getContext(), t.getMessage());
            }
        });
    }

    RefreshMyJobs refreshMyJobs = new RefreshMyJobs() {
        @Override
        public void refresh_jobs() {
            hitAllJobsToDriverAPI();
        }
    };


    StartJobInterface startJobInterface=new StartJobInterface() {
        @Override
        public void startjobs(int pos, String jobid) {
            if (jobid!=null) {
                if (ExpiryDate != null) {
                    if (currentDateandTime.compareTo(ExpiryDate) < 0) {
                        ShowAlertJob(jobid);
                    } else if (currentDateandTime.compareTo(ExpiryDate) > 0) {
                        PaymentDialog();
                    } else {
                        PaymentDialog();
                    }
                } else {
                    PaymentDialog();
                }

            }
        }
    };

    private void ShowAlertJob(final String jobsid){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        // alertDialog.setTitle("NKA SERVICE");
        alertDialog.setMessage("Are you sure you want to start this job?");
        alertDialog.setIcon(R.mipmap.call_icon);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String job_id;
//                job_id=data.getData().get(getAdapterPosition()).getJobId();

                hitStartJobAPI(jobsid);
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void hitStartJobAPI(String job_d) {
        final Dialog dialog = AppUtil.showProgress(getActivity());
        Interface service = CommonUtils.retroInit();
        Call<GenricModel> call = service.start_job(PreferenceHandler.readString(getContext(), PreferenceHandler.USER_ID, ""),
                job_d);
        call.enqueue(new Callback<GenricModel>() {
            @Override
            public void onResponse(Call<GenricModel> call, Response<GenricModel> response) {
                GenricModel data_start = response.body();
                assert data_start != null;
                if (data_start.getCode().equalsIgnoreCase("201")) {
                    dialog.dismiss();
                    hitAllJobsToDriverAPI();
                 //   refreshMyJobs.refresh_jobs();
                       CommonUtils.showSmallToast(getContext(),data_start.getMessage());
                } else {
                      CommonUtils.showSmallToast(getContext(),data_start.getMessage());
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<GenricModel> call, Throwable t) {
                dialog.dismiss();
                CommonUtils.showSmallToast(getContext(), t.getMessage());
            }
        });
    }


    private void PaymentDialog() {
        try {
            PreferenceHandler.writeString(getContext(), "status_onlie", "0");
            final Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.payment_items);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            final RelativeLayout mainview = (RelativeLayout) dialog.findViewById(R.id.mainview);
            mainview.getLayoutParams().width = getHeightWidth("width", getContext()) - 20;
            TextView yes = dialog.findViewById(R.id.yes);
            TextView cancel = dialog.findViewById(R.id.cancel);
            dotsLayout = dialog.findViewById(R.id.layoutDots);
            ViewPager viewpager = dialog.findViewById(R.id.viewpager);
            myviewPagerPayment = new MyviewPagerPayment(getContext());
            viewpager.setAdapter(myviewPagerPayment);
            viewpager.addOnPageChangeListener(viewPagerPageChangeListener);
            addBottomDots(0);
            dialog.show();
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPurchaseOneMonth();
                    dialog.dismiss();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getHeightWidth(String mode, Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        int lenthval = 0;
        if (mode.equalsIgnoreCase("height")) {
            lenthval = height;
        } else if (mode.equalsIgnoreCase("width")) {
            lenthval = width;
        }
        return lenthval;
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {

            } else {

            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];
        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive2);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(getContext());
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(30);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }



    private void startPurchaseOneMonth() {
        if (skuDetailsListTemp.size() > 0) {
            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetailsListTemp.get(0))
                    .build();
            billingClient.launchBillingFlow(getActivity(), billingFlowParams);
            Log.e("sku_onemonth", "" + skuDetailsListTemp);
        }

    }

    private boolean verifyValidSignature(String signedData, String signature) {
        try {
            return Security.verifyPurchase(base64Key, signedData, signature);
        } catch (IOException e) {
            Log.e("tag", "Got an exception trying to validate a purchase: " + e);
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("billingClient", "Destroying the manager.");

        if (billingClient != null && billingClient.isReady()) {
            billingClient.endConnection();
            billingClient = null;
        }
    }





    private void planSelectOneMonth(final String plan) {
        if (plan != null) {
            if (billingClient.isReady()) {
                skuList.clear();
                skuList.add(plan);
                SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);

                billingClient.querySkuDetailsAsync(params.build(),
                        new SkuDetailsResponseListener() {
                            @Override
                            public void onSkuDetailsResponse(BillingResult responseCode, List<SkuDetails> skuDetailsList) {
                                if (responseCode.getResponseCode() == BillingClient.BillingResponseCode.OK
                                        && skuDetailsList != null) {
                                    if (skuDetailsList.size() > 0) {
                                        skuDetailsListTemp.clear();
                                        skuDetailsListTemp = skuDetailsList;
                                        for (SkuDetails skuDetails : skuDetailsList) {
                                            String sku = skuDetails.getSku();
                                            String price1 = skuDetails.getPrice();
                                            String plainType1 = skuDetails.getType();
                                            String plainDescription = skuDetails.getDescription();
//                                            Priceshow=skuDetails.getPrice();
//                                            plandesc=skuDetails.getDescription();
//                                            Planperiod=skuDetails.getSubscriptionPeriod();
                                            if (plan.equals(sku)) {
//                                                priceS2.setText(price1);
//                                                planTypeS2.setText(plainType1);
//                                                planDescriptionS2.setText(plainDescription);

                                            }
                                        }
                                    }
                                }
                            }
                        });
            }
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
        if (list != null) {
            for (Purchase purchase : list) {
                handlePurchase(purchase);
                if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                    // Invalid purchase
                    // show error to user
                    Log.e("tag", "Got a purchase: " + purchase + "; but signature is bad. Skipping...");
                    return;
                } else {

                    Toast.makeText(getContext(), "Payment Done", Toast.LENGTH_SHORT).show();
                }

            }

            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                Log.e("responsecode", "" + billingResult.getResponseCode());
//            Intent intent = new Intent(getContext(), HomeActivity.class);
//            startActivity(intent);
                Toast.makeText(getContext(), "Payment Cancel by user", Toast.LENGTH_SHORT).show();
            }


        }
    }


    private void handlePurchase(Purchase purchase) {
        try {
            if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
//                SaveStatus("0");
                Log.e("tag", "Got a purchase: " + purchase + "; but signature is bad. Skipping...");

                return;
            } else {
                JSONObject object = new JSONObject(purchase.getOriginalJson());
                String purchase_timestamp = object.getString("purchaseTime");
                String purchase_status = object.getString("purchaseState");
                String paymentid = object.getString("orderId");
                String productid = object.getString("productId");
                Log.e("response", purchase.getOriginalJson());

                Log.e("purchasetime", purchase_timestamp);
                Log.e("purchase status", purchase_status);
                Log.e("paymentid", paymentid);
                Log.e("producttid", productid);

                //  Log.e("time_status",""+purchase_status+" "+purchase_timestamp);
                // convert timestamp to date

                SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date result = new Date(Long.parseLong(purchase_timestamp));
                Log.e("PURTIME", "" + simple.format(result));
                String Purchasedatesimple = simple.format(result);
                String purchasedateoriginal = Purchasedatesimple + " Etc/GMT";


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar c = Calendar.getInstance();
                c.setTime(sdf.parse(simple.format(result)));
                c.add(Calendar.MONTH, 1);
                String expirydatesimple = sdf.format(c.getTime());
                String expiryoriginal = expirydatesimple + " Etc/GMT";


                hitPaymentAPI(PreferenceHandler.readString(
                        getContext(), PreferenceHandler.USER_ID, ""), paymentid, "1", "1", "29.99 USD", "Monthly", purchasedateoriginal, expiryoriginal);
                Toast.makeText(getContext(), "Payment Done", Toast.LENGTH_SHORT).show();
//                Savepaymentstatus(paymentid,Planprice,productid,expiredate,Purchasedate);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hitPaymentAPI(String userid, String paymentid, String paysucess, String Paymentstatus, String amount, String plan, String purchasedate, String expirydate) {
        Log.e("data", userid + "=" + paymentid + "=" + purchasedate + "=" + expirydate);
        final Dialog dialog = AppUtil.showProgress(getActivity());
        Interface service = CommonUtils.retroInit();
        Call<GenricModel> call = service.user_payment(userid, paymentid, paysucess, Paymentstatus, amount, plan, purchasedate, expirydate);
        call.enqueue(new Callback<GenricModel>() {
            @Override
            public void onResponse(Call<GenricModel> call, Response<GenricModel> response) {
                GenricModel data = response.body();
                if (data != null) {
                    if (data.getCode().equalsIgnoreCase("201")) {
                        dialog.dismiss();
                        hitProfileViewAPI();

                        Toast.makeText(getContext(), "" + data.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "" + data.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<GenricModel> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(getContext(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        currentDateandTime = sdf.format(new Date());
        Log.e("currentDateandTime", currentDateandTime);
    }
}