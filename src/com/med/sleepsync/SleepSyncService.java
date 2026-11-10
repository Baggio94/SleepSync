package com.med.sleepsync;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ServiceInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;

public class SleepSyncService extends Service {
    private static final String TAG = "SleepSync";
    private static final String CHANNEL_ID = "sleep_sync";
    private static final int NOTIFICATION_ID = 4217;

    private static volatile boolean running = false;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean receiverRegistered = false;

    static boolean isRunning() {
        return running;
    }

    private final Runnable followRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "Screen ON -> FOLLOW");
            SyncthingController.sendFollow(SleepSyncService.this);
        }
    };

    private final BroadcastReceiver screenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                applyScreenOffState("Screen OFF");
            } else if (Intent.ACTION_POWER_CONNECTED.equals(action)
                    || Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                if (pm != null && !pm.isInteractive()
                        && SleepSyncPrefs.isFollowWhileCharging(SleepSyncService.this)) {
                    applyScreenOffState(Intent.ACTION_POWER_CONNECTED.equals(action)
                            ? "Charger connected" : "Charger disconnected");
                }
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                Log.i(TAG, "Screen ON -> FOLLOW scheduled");
                handler.removeCallbacks(followRunnable);
                SleepSyncPrefs.recordEvent(SleepSyncService.this, "Screen ON → FOLLOW scheduled");
                handler.postDelayed(followRunnable, 1000L);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        running = true;
        createNotificationChannel();
        startAsForeground();
        registerScreenReceiver();
        SleepSyncPrefs.recordEvent(this, "SleepSync service started");
        Log.i(TAG, "Service started");
        applyCurrentScreenState();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!SleepSyncPrefs.isEnabled(this)) {
            SleepSyncPrefs.recordEvent(this, "Start ignored: SleepSync disabled");
            Log.i(TAG, "Service start ignored because SleepSync is disabled");
            stopSelf();
            return START_NOT_STICKY;
        }
        if (!receiverRegistered) {
            registerScreenReceiver();
        }
        return START_STICKY;
    }

    private void startAsForeground() {
        Notification notification = buildNotification();
        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST
            );
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
    }

    private void registerScreenReceiver() {
        if (receiverRegistered) return;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);

        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(screenReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(screenReceiver, filter);
        }
        receiverRegistered = true;
    }

    private void applyCurrentScreenState() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean interactive = pm != null && pm.isInteractive();

        handler.removeCallbacks(followRunnable);
        if (interactive) {
            SleepSyncPrefs.recordEvent(this, "Initial state ON → FOLLOW scheduled");
            handler.postDelayed(followRunnable, 1000L);
        } else {
            applyScreenOffState("Initial state OFF");
        }
    }

    // Screen off: keep syncing while plugged in (Doze does not apply then), stop on battery.
    private void applyScreenOffState(String reason) {
        handler.removeCallbacks(followRunnable);
        if (SleepSyncPrefs.isFollowWhileCharging(this) && isPluggedIn()) {
            Log.i(TAG, reason + " while charging -> FOLLOW");
            SleepSyncPrefs.recordEvent(this, reason + ", charging → Syncthing FOLLOW");
            SyncthingController.sendFollow(this);
        } else {
            Log.i(TAG, reason + " on battery -> STOP");
            SleepSyncPrefs.recordEvent(this, reason + ", on battery → Syncthing STOP");
            SyncthingController.sendStop(this);
        }
    }

    private boolean isPluggedIn() {
        Intent battery = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return battery != null && battery.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) != 0;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "SleepSync background service",
                    NotificationManager.IMPORTANCE_MIN
            );
            channel.setDescription("Keeps SleepSync active for screen off/on monitoring");
            channel.setShowBadge(false);
            channel.enableVibration(false);
            channel.setSound(null, null);
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification() {
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
            builder.setPriority(Notification.PRIORITY_MIN);
        }

        Intent openApp = new Intent(this, MainActivity.class);
        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(
                this,
                0,
                openApp,
                Build.VERSION.SDK_INT >= 23
                        ? android.app.PendingIntent.FLAG_IMMUTABLE | android.app.PendingIntent.FLAG_UPDATE_CURRENT
                        : android.app.PendingIntent.FLAG_UPDATE_CURRENT
        );

        return builder
                .setContentTitle("SleepSync")
                .setContentText("Screen monitoring is active")
                .setSmallIcon(android.R.drawable.stat_notify_sync_noanim)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setShowWhen(false)
                .build();
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(followRunnable);
        if (receiverRegistered) {
            try {
                unregisterReceiver(screenReceiver);
            } catch (IllegalArgumentException ignored) {
            }
            receiverRegistered = false;
        }

        running = false;
        if (SleepSyncPrefs.isEnabled(this)) {
            SleepSyncPrefs.recordEvent(this, "Service stopped unexpectedly → FOLLOW sent");
            SyncthingController.sendFollow(this, "FOLLOW(onDestroy)");
        } else {
            SleepSyncPrefs.recordEvent(this, "SleepSync disabled");
            SyncthingController.sendFollow(this, "FOLLOW(disabled)");
        }
        Log.i(TAG, "Service stopped");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
