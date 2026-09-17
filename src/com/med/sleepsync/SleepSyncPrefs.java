package com.med.sleepsync;

import android.content.Context;
import android.content.SharedPreferences;

final class SleepSyncPrefs {
    private static final String PREFS = "sleep_sync";
    private static final String KEY_ENABLED = "enabled";
    private static final String KEY_LAST_EVENT = "last_event";
    private static final String KEY_LAST_EVENT_TIME = "last_event_time";
    private static final String KEY_SELECTED_PACKAGE = "selected_syncthing_package";

    private SleepSyncPrefs() {}

    static boolean isEnabled(Context context) {
        return prefs(context).getBoolean(KEY_ENABLED, false);
    }

    static void setEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_ENABLED, enabled).apply();
    }

    static String getSelectedPackage(Context context) {
        return prefs(context).getString(KEY_SELECTED_PACKAGE, null);
    }

    static void setSelectedPackage(Context context, String packageName) {
        SharedPreferences.Editor editor = prefs(context).edit();
        if (packageName == null || packageName.isEmpty()) {
            editor.remove(KEY_SELECTED_PACKAGE);
        } else {
            editor.putString(KEY_SELECTED_PACKAGE, packageName);
        }
        editor.apply();
    }

    static void recordEvent(Context context, String event) {
        prefs(context).edit()
                .putString(KEY_LAST_EVENT, event)
                .putLong(KEY_LAST_EVENT_TIME, System.currentTimeMillis())
                .apply();
    }

    static String getLastEvent(Context context) {
        return prefs(context).getString(KEY_LAST_EVENT, "No activity recorded yet");
    }

    static long getLastEventTime(Context context) {
        return prefs(context).getLong(KEY_LAST_EVENT_TIME, 0L);
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}
