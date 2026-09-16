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
                Log.i(TAG, "Screen OFF -> STOP");
                handler.removeCallbacks(followRunnable);
                SleepSyncPrefs.recordEvent(SleepSyncService.this, "Screen OFF → Syncthing STOP");
                SyncthingController.sendStop(SleepSyncService.this);
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
            SleepSyncPrefs.recordEvent(this, "Initial state OFF → Syncthing STOP");
            SyncthingController.sendStop(this);
            Log.i(TAG, "Applied initial screen state: OFF");
        }
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
