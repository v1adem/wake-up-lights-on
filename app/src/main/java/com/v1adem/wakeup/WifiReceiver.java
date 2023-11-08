package com.v1adem.wakeup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class WifiReceiver extends BroadcastReceiver {
    private final WifiStatusListener listener;

    public WifiReceiver(WifiStatusListener listener) {
        this.listener = (WifiStatusListener) listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            listener.onWifiStatusChanged(true);
        }
    }
}
