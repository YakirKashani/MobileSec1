package com.example.mobilesechw1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import Interfaces.BatteryListener;

public class BatteryReceiver extends BroadcastReceiver {
    private BatteryListener batteryListener;

    public BatteryReceiver(BatteryListener batteryListener) {
        this.batteryListener = batteryListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        if (level != -1 && scale != -1) {
            int batteryPct = (int) ((level / (float) scale) * 100);
            batteryListener.onBatteryLevelChange(batteryPct);
        }
    }
}
