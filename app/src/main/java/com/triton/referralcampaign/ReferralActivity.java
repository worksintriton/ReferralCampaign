package com.triton.referralcampaign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import static com.android.installreferrer.api.InstallReferrerClient.InstallReferrerResponse;

import android.content.Intent;

import android.os.Handler;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.google.android.gms.analytics.CampaignTrackingReceiver;
import com.google.android.gms.tagmanager.InstallReferrerReceiver;
import com.google.gson.Gson;
import com.triton.referralcampaign.api.APIClient;
import com.triton.referralcampaign.api.RestApiInterface;
import com.triton.referralcampaign.requestpojo.SignupRefRequest;
import com.triton.referralcampaign.responsepojo.SuccessResponse;
import com.triton.referralcampaign.utils.RestUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReferralActivity extends AppCompatActivity {

    private static final String TAG = "ReferralActivity" ;
    private final Executor backgroundExecutor = Executors.newSingleThreadExecutor();
    TextView txt_referral, txt_edit, txt_source, txt_medium;

    private String referredBy;
    private String mobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refferal);
        txt_referral = (TextView) findViewById(R.id.zText);
        txt_edit = (TextView) findViewById(R.id.edit);
        txt_medium = (TextView) findViewById(R.id.mediaun);
        txt_source = (TextView) findViewById(R.id.source);
        checkInstallReferrer();
    }

    // TODO: Change this to use whatever preferences are appropriate. The install referrer should
    // only be sent to the receiver once.
    private final String prefKey = "checkedInstallReferrer";

    void checkInstallReferrer() {
        /*if (getPreferences(MODE_PRIVATE).getBoolean(prefKey, false)) {
            return;
        }*/


        InstallReferrerClient referrerClient = InstallReferrerClient.newBuilder(this).build();
        backgroundExecutor.execute(() -> getInstallReferrerFromClient(referrerClient));
    }

    void getInstallReferrerFromClient(InstallReferrerClient referrerClient) {

        referrerClient.startConnection(new InstallReferrerStateListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                Log.w(TAG,"responseCode : "+responseCode);

                switch (responseCode) {
                    case InstallReferrerResponse.OK:
                        ReferrerDetails response = null;
                        try {
                            response = referrerClient.getInstallReferrer();
                            Log.w(TAG,"response : "+response);

                        } catch (RemoteException e) {
                            e.printStackTrace();
                            return;
                        }

                        String referrerUrl1 = response.getInstallReferrer();
                        String referrerUrl = "https://play.google.com/store/apps/details?id=com.sirpi.dvaracattlehealth&utm_source=S2CBCuJ73&utm_medium=9751832181";
                        long referrerClickTime = response.getReferrerClickTimestampSeconds();
                        long appInstallTime = response.getInstallBeginTimestampSeconds();
                        boolean instantExperienceLaunched = response.getGooglePlayInstantParam();

                        if (!TextUtils.isEmpty(referrerUrl)) {
                            String[] utms = referrerUrl.split("&");
                            String source = "";
                            String medium = "";
                            String campaign = "";
                            String content = "";

                            for (String utm : utms) {
                                if (utm.contains("utm_source"))
                                    source = utm.substring(utm.indexOf("=") + 1);
                                if (utm.contains("utm_medium"))
                                    medium = utm.substring(utm.indexOf("=") + 1);
                                if (utm.contains("utm_campaign"))
                                    campaign = utm.substring(utm.indexOf("=") + 1);
                                if (utm.contains("utm_content"))
                                    content = utm.substring(utm.indexOf("=") + 1);
                            }
                            referredBy = source;
                            mobileNumber = medium;
                            txt_referral.setText(referrerUrl);
                            txt_edit.setText("campaign : "+ campaign);
                            txt_source.setText("source : "+source);
                            txt_medium.setText("medium : "+medium);

                            if(!mobileNumber.isEmpty() && !referredBy.isEmpty()) {
                                signupRefResponseCall();
                            }

                            Log.w(TAG," source : "+source+" medium : "+medium+" campaign : "+campaign+" content : "+content);
                        }


                        // TODO: If you're using GTM, call trackInstallReferrerforGTM instead.
                        trackInstallReferrer(referrerUrl);




                        // Only check this once.
                        getPreferences(MODE_PRIVATE).edit().putBoolean(prefKey, true).apply();

                        // End the connection
                        referrerClient.endConnection();

                        break;
                    case InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        // API not available on the current Play Store app.
                        break;
                    case InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        // Connection couldn't be established.
                        break;
                }
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {

            }
        });
    }

    // Tracker for Classic GA (call this if you are using Classic GA only)
    private void trackInstallReferrer(final String referrerUrl) {
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                CampaignTrackingReceiver receiver = new CampaignTrackingReceiver();
                Intent intent = new Intent("com.android.vending.INSTALL_REFERRER");
                intent.putExtra("referrer", referrerUrl);
                receiver.onReceive(getApplicationContext(), intent);
            }
        });
    }

    // Tracker for GTM + Classic GA (call this if you are using GTM + Classic GA only)
    private void trackInstallReferrerforGTM(final String referrerUrl) {
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                InstallReferrerReceiver receiver = new InstallReferrerReceiver();
                Intent intent = new Intent("com.android.vending.INSTALL_REFERRER");
                intent.putExtra("referrer", referrerUrl);
                receiver.onReceive(getApplicationContext(), intent);
            }
        });
    }



    @SuppressLint("LogNotTimber")
    private void signupRefResponseCall() {
        RestApiInterface apiInterface = APIClient.getClient().create(RestApiInterface.class);
        Call<SuccessResponse> call = apiInterface.signupRefResponseCall(RestUtils.getContentType(), signupRefRequest());
        Log.w(TAG,"SignupResponse url  :%s"+" "+ call.request().url().toString());

        call.enqueue(new Callback<SuccessResponse>() {
            @SuppressLint("LogNotTimber")
            @Override
            public void onResponse(@NonNull Call<SuccessResponse> call, @NonNull Response<SuccessResponse> response) {
                Log.w(TAG,"SignupResponse" + new Gson().toJson(response.body()));
                if (response.body() != null) {

                    if (200 == response.body().getCode()) {

                        Toast.makeText(getApplicationContext(), "Successfully saved....", Toast.LENGTH_SHORT).show();




                    } else {

                    }
                }


            }

            @Override
            public void onFailure(@NonNull Call<SuccessResponse> call,@NonNull Throwable t) {

                Log.e("OTP", "--->" + t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private SignupRefRequest signupRefRequest() {

        /*
         * referredBy : 9751832183
         * mobileNumber : 9751832184
         */

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());


        SignupRefRequest signupRefRequest = new SignupRefRequest();
        signupRefRequest.setMobileNumber("9898989898");
        signupRefRequest.setReferredBy(mobileNumber);
        Log.w(TAG,"signupRefRequest "+ new Gson().toJson(signupRefRequest));
        return signupRefRequest;
    }

}