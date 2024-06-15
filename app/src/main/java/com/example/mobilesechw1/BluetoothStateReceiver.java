package com.example.mobilesechw1;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import Interfaces.BluetoothStateChangeListener;

public class BluetoothStateReceiver extends BroadcastReceiver {
    private BluetoothStateChangeListener bluetoothStateChangeListener;

    public BluetoothStateReceiver(BluetoothStateChangeListener bluetoothStateChangeListener) {
        this.bluetoothStateChangeListener = bluetoothStateChangeListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    bluetoothStateChangeListener.onBluetoothStateChange(false);
                    break;
                case BluetoothAdapter.STATE_ON:
                    bluetoothStateChangeListener.onBluetoothStateChange(true);
                    break;
            }
        }
    }
}
