package com.triton.referralcampaign.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.triton.referralcampaign.R;


public class ConnectionDetector {

    private Context mObjContext;
    Dialog dialog;

    public ConnectionDetector(Context pObjContext) {
        this.mObjContext = pObjContext;
    }

    public boolean isAvailable(Context context){
    	ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)  {
                for (int i = 0, len = info.length; i < len; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        MakeToast(context, "No Network Connection");
        return false;
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        } else {
            showInternetAlert(context, "No Network Connection");
            return false;

        }
    }

    private void showInternetAlert(final Context context, String message) {

        try {

            dialog = new Dialog(context);
            dialog.setContentView(R.layout.alert_no_internet_layout);
            Button dialogButton = dialog.findViewById(R.id.btnDialogOk);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();

                }
            });
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }




    }

    private void MakeToast(Context ctx, String msg) {
        int offset = Math.round(30 * ctx.getResources().getDisplayMetrics().density);
        Toast toast = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, Gravity.CENTER_HORIZONTAL, offset);
        toast.show();
    }
}