package com.triton.referralcampaign.api;


import com.triton.referralcampaign.requestpojo.SignupRefRequest;
import com.triton.referralcampaign.responsepojo.SuccessResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;


public interface RestApiInterface {


    /*referral signup create*/
    @POST("findReferralCode")
    Call<SuccessResponse> signupRefResponseCall(@Header("Content-Type") String type, @Body SignupRefRequest signupRequest);




}
