package com.med.sleepsync;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class SyncthingController {
    static final String PACKAGE_CURRENT = "com.github.catfriend1.syncthingfork";
    static final String PACKAGE_CURRENT_DEBUG = "com.github.catfriend1.syncthingfork.debug";
    static final String PACKAGE_LEGACY = "com.github.catfriend1.syncthingandroid";
    static final String PACKAGE_LEGACY_DEBUG = "com.github.catfriend1.syncthingandroid.debug";

    private static final String[] SUPPORTED_PACKAGES = new String[]{
            PACKAGE_CURRENT,
            PACKAGE_CURRENT_DEBUG,
            PACKAGE_LEGACY,
            PACKAGE_LEGACY_DEBUG
    };

    private static final String TAG = "SleepSync";

    static final class Target {
        final String packageName;
        final String displayName;

        Target(String packageName, String displayName) {
            this.packageName = packageName;
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private SyncthingController() {}

    static boolean isInstalled(Context context) {
        return getTargetPackage(context) != null;
    }

    static List<Target> getInstalledTargets(Context context) {
        if (context == null) return Collections.emptyList();

        List<Target> targets = new ArrayList<>();
        for (String packageName : SUPPORTED_PACKAGES) {
            if (isPackageInstalled(context, packageName)) {
                targets.add(new Target(packageName, buildDisplayName(context, packageName)));
            }
        }
        return targets;
    }

    static String getTargetPackage(Context context) {
        String selected = SleepSyncPrefs.getSelectedPackage(context);
        if (selected != null && isSupportedPackage(selected) && isPackageInstalled(context, selected)) {
            return selected;
        }

        List<Target> installed = getInstalledTargets(context);
        if (installed.isEmpty()) {
            return null;
        }

        String fallback = installed.get(0).packageName;
        SleepSyncPrefs.setSelectedPackage(context, fallback);
        return fallback;
    }

    static Target getTarget(Context context) {
        String packageName = getTargetPackage(context);
        if (packageName == null) return null;
        return new Target(packageName, buildDisplayName(context, packageName));
    }

    static void selectTarget(Context context, String packageName) {
        if (isSupportedPackage(packageName) && isPackageInstalled(context, packageName)) {
            SleepSyncPrefs.setSelectedPackage(context, packageName);
        }
    }

    static boolean openApp(Context context) {
        String packageName = getTargetPackage(context);
        if (packageName == null) return false;

        Intent launch = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launch == null) return false;
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launch);
        return true;
    }

    static boolean sendStop(Context context) {
        return sendToResolvedTarget(context, ".action.STOP", "STOP");
    }

    static boolean sendFollow(Context context) {
        return sendToResolvedTarget(context, ".action.FOLLOW", "FOLLOW");
    }

    static boolean sendFollow(Context context, String label) {
        return sendToResolvedTarget(context, ".action.FOLLOW", label);
    }

    static boolean sendStopToPackage(Context context, String packageName, String label) {
        return send(context, packageName, ".action.STOP", label);
    }

    static boolean sendFollowToPackage(Context context, String packageName, String label) {
        return send(context, packageName, ".action.FOLLOW", label);
    }

    private static boolean sendToResolvedTarget(Context context, String suffix, String label) {
        String packageName = getTargetPackage(context);
        return send(context, packageName, suffix, label);
    }

    private static boolean send(Context context, String packageName, String suffix, String label) {
        if (packageName == null || !isPackageInstalled(context, packageName)) {
            SleepSyncPrefs.recordEvent(context, "No supported Syncthing-Fork target found");
            Log.w(TAG, "Unable to send " + label + ": no supported Syncthing-Fork package installed");
            return false;
        }

        Intent intent = new Intent(packageName + suffix);
        intent.setPackage(packageName);
        context.sendBroadcast(intent);
        SleepSyncPrefs.recordEvent(context, "Syncthing " + label + " → " + shortName(packageName));
        Log.i(TAG, "Sent Syncthing " + label + " to " + packageName);
        return true;
    }

    private static boolean isPackageInstalled(Context context, String packageName) {
        if (context == null || packageName == null) return false;
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private static boolean isSupportedPackage(String packageName) {
        if (packageName == null) return false;
        for (String supported : SUPPORTED_PACKAGES) {
            if (supported.equals(packageName)) return true;
        }
        return false;
    }

    private static String buildDisplayName(Context context, String packageName) {
        String base = friendlyName(packageName);
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            String versionName = info.versionName;
            if (versionName != null && !versionName.trim().isEmpty()) {
                return base + " • " + versionName;
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return base;
    }

    private static String friendlyName(String packageName) {
        if (PACKAGE_CURRENT.equals(packageName)) return "Syncthing-Fork";
        if (PACKAGE_CURRENT_DEBUG.equals(packageName)) return "Syncthing-Fork Debug / Root build";
        if (PACKAGE_LEGACY.equals(packageName)) return "Syncthing-Fork Legacy";
        if (PACKAGE_LEGACY_DEBUG.equals(packageName)) return "Syncthing-Fork Legacy Debug";
        return "Syncthing-Fork";
    }

    private static String shortName(String packageName) {
        if (PACKAGE_CURRENT_DEBUG.equals(packageName)) return "Debug/Root";
        if (PACKAGE_LEGACY.equals(packageName)) return "Legacy";
        if (PACKAGE_LEGACY_DEBUG.equals(packageName)) return "Legacy Debug";
        return "Standard";
    }
}
