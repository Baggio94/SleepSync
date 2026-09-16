package com.med.sleepsync;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

final class SyncthingController {
    static final String PACKAGE_NAME = "com.github.catfriend1.syncthingfork";
    private static final String ACTION_STOP = PACKAGE_NAME + ".action.STOP";
    private static final String ACTION_FOLLOW = PACKAGE_NAME + ".action.FOLLOW";
    private static final String TAG = "SleepSync";

    private SyncthingController() {}

    static boolean isInstalled(Context context) {
        try {
            context.getPackageManager().getPackageInfo(PACKAGE_NAME, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    static boolean openApp(Context context) {
        Intent launch = context.getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);
        if (launch == null) return false;
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launch);
        return true;
    }

    static void sendStop(Context context) {
        send(context, ACTION_STOP, "STOP");
    }

    static void sendFollow(Context context) {
        send(context, ACTION_FOLLOW, "FOLLOW");
    }

    static void sendFollow(Context context, String label) {
        send(context, ACTION_FOLLOW, label);
    }

    private static void send(Context context, String action, String label) {
        Intent intent = new Intent(action);
        intent.setPackage(PACKAGE_NAME);
        context.sendBroadcast(intent);
        SleepSyncPrefs.recordEvent(context, "Syncthing " + label);
        Log.i(TAG, "Sent Syncthing " + label);
    }
}
