package com.med.sleepsync;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends Activity {
    private TextView syncthingStatus;
    private TextView sleepSyncStatus;
    private TextView lastActivityStatus;
    private Button enableButton;
    private Button openSyncthingButton;
    private Button testStopButton;
    private Button testFollowButton;
    private Button finishSetupButton;

    private boolean darkMode;
    private int backgroundColor;
    private int primaryTextColor;
    private int secondaryTextColor;
    private int sectionColor;
    private int successColor;
    private int warningColor;
    private int errorColor;
    private int inactiveColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPalette();
        setTitle("SleepSync");
        setContentView(buildUi());
        refreshUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ensureServiceRunningIfEnabled();
        refreshUi();
    }

    private void initPalette() {
        int nightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        darkMode = nightMode == Configuration.UI_MODE_NIGHT_YES;

        if (darkMode) {
            backgroundColor = Color.rgb(9, 18, 37);
            primaryTextColor = Color.rgb(241, 245, 249);
            secondaryTextColor = Color.rgb(169, 181, 199);
            sectionColor = Color.rgb(92, 179, 255);
            successColor = Color.rgb(89, 217, 135);
            warningColor = Color.rgb(255, 190, 92);
            errorColor = Color.rgb(255, 112, 112);
            inactiveColor = Color.rgb(174, 184, 199);
        } else {
            backgroundColor = Color.rgb(247, 249, 252);
            primaryTextColor = Color.rgb(28, 35, 48);
            secondaryTextColor = Color.rgb(92, 103, 118);
            sectionColor = Color.rgb(31, 111, 235);
            successColor = Color.rgb(35, 145, 75);
            warningColor = Color.rgb(181, 111, 0);
            errorColor = Color.rgb(190, 45, 45);
            inactiveColor = Color.rgb(95, 99, 104);
        }
    }

    private View buildUi() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(backgroundColor);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(28), dp(24), dp(28));
        scroll.addView(root, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        TextView title = text("SleepSync", 30, true);
        root.addView(title);

        TextView version = text("Version 1.0.1", 13, false);
        version.setTextColor(secondaryTextColor);
        version.setPadding(0, dp(2), 0, 0);
        root.addView(version);

        TextView subtitle = text(
                "Pause Syncthing-Fork while the screen is off and automatically resume its normal run conditions when the screen wakes.",
                16,
                false
        );
        subtitle.setTextColor(secondaryTextColor);
        subtitle.setPadding(0, dp(10), 0, dp(24));
        root.addView(subtitle);

        root.addView(sectionTitle("Setup"));
        root.addView(label("1. Syncthing-Fork"));
        syncthingStatus = statusText();
        root.addView(syncthingStatus);

        openSyncthingButton = button("Open Syncthing-Fork");
        openSyncthingButton.setOnClickListener(v -> openSyncthing());
        addWithTopMargin(root, openSyncthingButton, 8);

        TextView broadcastHint = text(
                "2. Enable in Syncthing-Fork:\nSettings → Behaviour → Service Control by Broadcast",
                15,
                false
        );
        broadcastHint.setTextColor(secondaryTextColor);
        broadcastHint.setPadding(0, dp(18), 0, 0);
        root.addView(broadcastHint);

        root.addView(spacer(24));
        root.addView(sectionTitle("SleepSync status"));
        sleepSyncStatus = statusText();
        root.addView(sleepSyncStatus);

        enableButton = button("Enable SleepSync");
        enableButton.setOnClickListener(v -> toggleSleepSync());
        addWithTopMargin(root, enableButton, 10);

        TextView behavior = text(
                "Screen OFF  →  Syncthing STOP\nScreen ON   →  Syncthing FOLLOW after 1 second",
                15,
                false
        );
        behavior.setTextColor(secondaryTextColor);
        behavior.setPadding(0, dp(18), 0, 0);
        root.addView(behavior);

        lastActivityStatus = text("", 13, false);
        lastActivityStatus.setTextColor(secondaryTextColor);
        lastActivityStatus.setPadding(0, dp(18), 0, 0);
        root.addView(lastActivityStatus);

        root.addView(spacer(28));
        root.addView(sectionTitle("Advanced test"));
        TextView testHint = text(
                "These buttons send the same Syncthing-Fork broadcasts used automatically by SleepSync.",
                14,
                false
        );
        testHint.setTextColor(secondaryTextColor);
        root.addView(testHint);

        LinearLayout testRow = new LinearLayout(this);
        testRow.setOrientation(LinearLayout.HORIZONTAL);
        testRow.setGravity(Gravity.CENTER_VERTICAL);
        testRow.setPadding(0, dp(10), 0, 0);

        testStopButton = button("Test STOP");
        testFollowButton = button("Test FOLLOW");

        testStopButton.setOnClickListener(v -> {
            SyncthingController.sendStop(this);
            toast("STOP sent to Syncthing-Fork");
            refreshUi();
        });
        testFollowButton.setOnClickListener(v -> {
            SyncthingController.sendFollow(this);
            toast("FOLLOW sent to Syncthing-Fork");
            refreshUi();
        });

        LinearLayout.LayoutParams half = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        half.setMarginEnd(dp(6));
        testRow.addView(testStopButton, half);
        LinearLayout.LayoutParams half2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        half2.setMarginStart(dp(6));
        testRow.addView(testFollowButton, half2);
        root.addView(testRow);

        TextView footer = text(
                "Once enabled, you can close SleepSync. It continues in the background and starts again automatically after reboot. Don't kill SleepSync from the task switcher.",
                13,
                false
        );
        footer.setTextColor(secondaryTextColor);
        footer.setPadding(0, dp(28), 0, 0);
        root.addView(footer);

        finishSetupButton = button("Finish setup");
        finishSetupButton.setOnClickListener(v -> leaveUiSafely());
        addWithTopMargin(root, finishSetupButton, 12);

        return scroll;
    }

    private void refreshUi() {
        if (syncthingStatus == null) return;

        boolean installed = SyncthingController.isInstalled(this);
        boolean enabled = SleepSyncPrefs.isEnabled(this);
        boolean running = SleepSyncService.isRunning();

        syncthingStatus.setText(installed ? "✓ Installed" : "✕ Not installed");
        syncthingStatus.setTextColor(installed ? successColor : errorColor);

        if (enabled && running) {
            sleepSyncStatus.setText("✓ Active");
            sleepSyncStatus.setTextColor(successColor);
        } else if (enabled) {
            sleepSyncStatus.setText("Enabled • service starting");
            sleepSyncStatus.setTextColor(warningColor);
        } else {
            sleepSyncStatus.setText("Not enabled");
            sleepSyncStatus.setTextColor(inactiveColor);
        }

        enableButton.setText(enabled ? "Disable SleepSync" : "Enable SleepSync");
        openSyncthingButton.setEnabled(installed);
        testStopButton.setEnabled(installed);
        testFollowButton.setEnabled(installed);
        finishSetupButton.setEnabled(enabled && running);

        String lastEvent = SleepSyncPrefs.getLastEvent(this);
        long lastTime = SleepSyncPrefs.getLastEventTime(this);
        if (lastTime > 0L) {
            String formatted = DateFormat.getMediumDateFormat(this).format(new Date(lastTime))
                    + " " + DateFormat.getTimeFormat(this).format(new Date(lastTime));
            lastActivityStatus.setText("Last activity: " + lastEvent + "\n" + formatted);
        } else {
            lastActivityStatus.setText("Last activity: " + lastEvent);
        }
    }

    private void scheduleUiRefresh() {
        if (sleepSyncStatus == null) return;
        sleepSyncStatus.postDelayed(this::refreshUi, 250L);
        sleepSyncStatus.postDelayed(this::refreshUi, 1000L);
    }

    private void ensureServiceRunningIfEnabled() {
        if (!SleepSyncPrefs.isEnabled(this) || SleepSyncService.isRunning()) return;
        if (!SyncthingController.isInstalled(this)) return;

        Intent service = new Intent(this, SleepSyncService.class);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(service);
            } else {
                startService(service);
            }
            SleepSyncPrefs.recordEvent(this, "SleepSync service restarted on app open");
            scheduleUiRefresh();
        } catch (Exception e) {
            SleepSyncPrefs.recordEvent(this, "Unable to restart: " + e.getClass().getSimpleName());
            toast("Unable to restart SleepSync: " + e.getClass().getSimpleName());
        }
    }

    private void leaveUiSafely() {
        if (!moveTaskToBack(true)) {
            finish();
        }
    }

    private void toggleSleepSync() {
        boolean enabled = SleepSyncPrefs.isEnabled(this);
        if (enabled) {
            SleepSyncPrefs.setEnabled(this, false);
            SleepSyncPrefs.recordEvent(this, "SleepSync disabled by user");
            boolean stopped = stopService(new Intent(this, SleepSyncService.class));
            if (!stopped) {
                SyncthingController.sendFollow(this, "FOLLOW(disabled)");
            }
            toast("SleepSync disabled");
            refreshUi();
            return;
        }

        if (!SyncthingController.isInstalled(this)) {
            toast("Install Syncthing-Fork first");
            return;
        }

        SleepSyncPrefs.setEnabled(this, true);
        SleepSyncPrefs.recordEvent(this, "SleepSync enabled by user");
        Intent service = new Intent(this, SleepSyncService.class);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(service);
            } else {
                startService(service);
            }
            toast("SleepSync enabled");
            scheduleUiRefresh();
        } catch (Exception e) {
            SleepSyncPrefs.setEnabled(this, false);
            SleepSyncPrefs.recordEvent(this, "Unable to start: " + e.getClass().getSimpleName());
            toast("Unable to start SleepSync: " + e.getClass().getSimpleName());
        }
        refreshUi();
    }

    private void openSyncthing() {
        if (!SyncthingController.openApp(this)) {
            toast("Unable to open Syncthing-Fork");
        }
    }

    private TextView sectionTitle(String value) {
        TextView view = text(value, 19, true);
        view.setTextColor(sectionColor);
        view.setPadding(0, 0, 0, dp(10));
        return view;
    }

    private TextView label(String value) {
        TextView view = text(value, 15, true);
        view.setPadding(0, 0, 0, dp(4));
        return view;
    }

    private TextView statusText() {
        return text("", 16, true);
    }

    private TextView text(String value, int sp, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(sp);
        view.setTextColor(primaryTextColor);
        if (bold) view.setTypeface(view.getTypeface(), android.graphics.Typeface.BOLD);
        return view;
    }

    private Button button(String value) {
        Button button = new Button(this);
        button.setText(value);
        button.setAllCaps(false);
        button.setMinHeight(dp(48));
        return button;
    }

    private View spacer(int dp) {
        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(1, dp(dp)));
        return spacer;
    }

    private void addWithTopMargin(LinearLayout parent, View child, int marginDp) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.topMargin = dp(marginDp);
        parent.addView(child, lp);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void toast(String value) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
    }
}
