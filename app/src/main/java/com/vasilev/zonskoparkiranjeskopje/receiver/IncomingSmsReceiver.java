package com.vasilev.zonskoparkiranjeskopje.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

public class IncomingSmsReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = IncomingSmsReceiver.class.getSimpleName();

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
        if (intent.getAction().equals(ACTION)) {
            Log.d(LOG_TAG, "Sms received");
        }
    }
}
