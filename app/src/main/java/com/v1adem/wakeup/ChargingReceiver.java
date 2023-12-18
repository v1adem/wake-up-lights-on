package com.v1adem.wakeup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

public class ChargingReceiver extends BroadcastReceiver {
    private final ChargingStatusListener listener;

    public ChargingReceiver(ChargingStatusListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, @NonNull Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
                // Charging started
                listener.onChargingStatusChanged(true);
            }
        }
    }
}
