package com.med.sleepsync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "SleepSync";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!SleepSyncPrefs.isEnabled(context)) {
            SleepSyncPrefs.recordEvent(context, "Boot event ignored: SleepSync disabled");
            Log.i(TAG, "Boot event ignored because SleepSync is disabled: " + action);
            return;
        }

        Intent service = new Intent(context, SleepSyncService.class);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(service);
            } else {
                context.startService(service);
            }
            SleepSyncPrefs.recordEvent(context, "Started after " + action);
            Log.i(TAG, "Started after " + action);
        } catch (Exception e) {
            SleepSyncPrefs.recordEvent(context, "Boot start failed: " + e.getClass().getSimpleName());
            Log.e(TAG, "Unable to start SleepSync service after " + action, e);
        }
    }
}
