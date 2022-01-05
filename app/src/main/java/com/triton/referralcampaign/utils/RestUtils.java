package com.triton.referralcampaign.utils;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;

public class RestUtils {
    private static final String TAG = "RestUtils";
    private static RestUtils instance;

    private RestUtils() {

    }

    public static RestUtils getInstance() {

        if (instance == null)
            instance = new RestUtils();

        return instance;
    }

    public static String Access_Token = "";

    public static String getAuthorizationHeader(String Accesstoken) {
        Log.d(TAG, "getAuthorizationHeader: Base64 " + Access_Token);

        byte[] data = new byte[0];
        try {
            data = Accesstoken.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String encodedToken = Base64.encodeToString(data, Base64.DEFAULT);
        Log.d(TAG, "getAuthorizationHeader: encoded Basic " + encodedToken);
        return "Basic " + encodedToken.trim();


    }

    public static String getContentType() {
        return "application/json";
    }

    public static String getDecodedCode(String qrcode) {

        String text = "";
        byte[] data = Base64.decode(qrcode, Base64.DEFAULT);
        try {
            text = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (!text.isEmpty()) {
            String namepass[] = text.split(":");
            if (namepass.length > 1) {
                text = namepass[1];
            } else {
                text = "";
            }


        }

        return text;

    }


}
