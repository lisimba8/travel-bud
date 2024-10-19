package com.example.coursework1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class InternetReceiver extends BroadcastReceiver {

    private static final String TAG = "InternetReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        context = context.getApplicationContext();

        //check internet connectivity
        //if not connected, show toast
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            //connected to internet
            if(isOnline(context)) {
                Toast.makeText(context, "Internet Connected. You can access map and trip plans.", Toast.LENGTH_SHORT).show();
            }
        }else{
            //not connected to internet
            Toast.makeText(context, "No connection. Connect to internet to access maps and trip plans.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }
}
