package com.triton.referralcampaign;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class InstallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String rawReferrer = intent.getStringExtra("referrer");
        if (rawReferrer != null) {
            Intent in = new Intent("referral");
            in.putExtra("referral", rawReferrer);
            context.sendBroadcast(in);
            Log.e("refer code", "refer code    " + rawReferrer);
        }
    }
}