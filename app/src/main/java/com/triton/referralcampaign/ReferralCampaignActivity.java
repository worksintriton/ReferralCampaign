package com.triton.referralcampaign;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import java.util.Arrays;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReferralCampaignActivity extends Activity {

    private final String TAG = "MainActivity";
    TextView txt_referral, txt_edit, txt_source, txt_medium;
    private String referredBy;
    private String mobileNumber;
    private String referrerUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_campaign);
        txt_referral = findViewById(R.id.zText);
        txt_edit = findViewById(R.id.edit);
        txt_medium = findViewById(R.id.mediaun);
        txt_source = findViewById(R.id.source);

        checkInstallReferrer();

    }
    // only be sent to the receiver once.
    private final String prefKey = "checkedInstallReferrer";
    void checkInstallReferrer() {
        if (getPreferences(MODE_PRIVATE).getBoolean(prefKey, false)) {
            return;
        }
        callPlayReferrerAPI();
    }

    private void callPlayReferrerAPI() {
        final InstallReferrerClient referrerClient;
        referrerClient = InstallReferrerClient.newBuilder(this).build();
        referrerClient.startConnection(new InstallReferrerStateListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                Log.w(TAG," responseCode : "+responseCode);
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        // Connection established.
                        try {
                            ReferrerDetails response = referrerClient.getInstallReferrer();
                           // String referrerUrl = response.getInstallReferrer();
                             referrerUrl = "https://play.google.com/store/apps/details?id=com.sirpi.dvaracattlehealth&utm_source=S2CBCuJ73&utm_medium=9751832183";
                            long referrerClickTime = response.getReferrerClickTimestampSeconds();
                            long appInstallTime = response.getInstallBeginTimestampSeconds();
                            boolean instantExperienceLaunched = response.getGooglePlayInstantParam();


                            //Process Referrer URL
                            //For Example
                            if (!TextUtils.isEmpty(referrerUrl)) {
                                String[] utms = referrerUrl.split("&");
                                String source = "";
                                String medium = "";
                                String campaign = "";
                                String content = "";

                                Log.w(TAG,"utms : "+ Arrays.toString(utms));

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

                               if(mobileNumber != null && !mobileNumber.isEmpty() && referredBy != null && !referredBy.isEmpty()) {
                                   signupRefResponseCall();
                               }
                                txt_referral.setText(referrerUrl);
                                txt_edit.setText("campaign : "+ campaign);
                                txt_source.setText("source : "+source);
                                txt_medium.setText("medium : "+medium);
                                Log.w(TAG," source : "+source+" medium : "+medium+" campaign : "+campaign+" content : "+content);

                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        // Only check this once.
                        getPreferences(MODE_PRIVATE).edit().putBoolean(prefKey, true).apply();

                        //Closing service connection
                        referrerClient.endConnection();
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        // API not available on the current Play Store app.
                        referrerClient.endConnection();
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        // Connection couldn't be established.
                        referrerClient.endConnection();
                        break;
                }
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                referrerClient.endConnection();
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
        SignupRefRequest signupRefRequest = new SignupRefRequest();
        signupRefRequest.setMobileNumber("9898989898");
        signupRefRequest.setReferredBy(mobileNumber);
        Log.w(TAG,"signupRefRequest "+ new Gson().toJson(signupRefRequest));
        return signupRefRequest;
    }


}