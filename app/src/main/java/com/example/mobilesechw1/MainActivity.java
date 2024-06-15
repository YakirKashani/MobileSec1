package com.example.mobilesechw1;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import Interfaces.BatteryListener;
import Interfaces.BluetoothStateChangeListener;

public class MainActivity extends AppCompatActivity implements SensorEventListener, BluetoothStateChangeListener, BatteryListener {

    /*Views*/
    private MaterialTextView main_LBL_North;
    private MaterialTextView main_LBL_Bluetooth_services;
    private MaterialTextView main_LBL_BatteryStatus;
    private MaterialButton main_BTN_Log_In;
    private TextInputEditText main_TIET_Password;

    /*Magnetic-Field*/
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnometer;
    private float[] gravity;
    private float[] geomagnetic;
    private boolean NorthOk;

    /*Bluetooth*/
    private BluetoothStateReceiver bluetoothStateReceiver;
    private boolean BluetoothOk;

    /*Battery Status*/
    private BatteryReceiver batteryReceiver;
    private int Battery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        /*Magnetic Field*/
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }
        /*Bluetooth*/
        bluetoothStateReceiver = new BluetoothStateReceiver(this);
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothStateReceiver, filter);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)
            onBluetoothStateChange(false);
        else
            onBluetoothStateChange(bluetoothAdapter.isEnabled());
        /*Battery Status*/
        batteryReceiver = new BatteryReceiver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*Magnetic Field*/
        if (accelerometer != null)
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        if (magnometer != null)
            sensorManager.registerListener(this, magnometer, SensorManager.SENSOR_DELAY_UI);
        /*Battery Status*/
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, intentFilter);
        /*Log-In Button*/
        main_BTN_Log_In.setOnClickListener(v -> {
            if (Integer.parseInt(main_TIET_Password.getText().toString()) == Battery && NorthOk && BluetoothOk)
                ChangeActivityToWelcomeActivity();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        unregisterReceiver(batteryReceiver);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            gravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            geomagnetic = event.values;
        if (gravity != null && geomagnetic != null) {
            float[] R = new float[9];
            float[] I = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
            if (success) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);
                float azimuth = (float) Math.toDegrees(orientation[0]);
                if (azimuth < 0)
                    azimuth += 360;
                if (isFacingNorth(azimuth)) {
                    main_LBL_North.setText("North: V");
                    this.NorthOk = true;
                } else {
                    main_LBL_North.setText("North: X");
                    this.NorthOk = false;
                }
            }
        }
    }

    private boolean isFacingNorth(float azimuth) {
        //threshold -> how close to north the azimuth should be
        final float threshold = 10;
        return (azimuth < threshold || azimuth > (360 - threshold));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onBluetoothStateChange(boolean isEnabled) {
        if (isEnabled) {
            main_LBL_Bluetooth_services.setText("Bluetooth: V");
            this.BluetoothOk = true;
        } else {
            main_LBL_Bluetooth_services.setText("Bluetooth: X");
            this.BluetoothOk = false;
        }
    }

    @Override
    public void onBatteryLevelChange(int batteryPercentage) {
        this.Battery = batteryPercentage;
        main_LBL_BatteryStatus.setText("Battery: " + batteryPercentage);
    }

    public void ChangeActivityToWelcomeActivity() {
        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
        startActivity(intent);
    }

    private void findViews() {
        main_LBL_North = findViewById(R.id.main_LBL_North);
        main_LBL_Bluetooth_services = findViewById(R.id.main_LBL_Bluetooth_services);
        main_LBL_BatteryStatus = findViewById(R.id.main_LBL_BatteryStatus);
        main_BTN_Log_In = findViewById(R.id.main_BTN_Log_In);
        main_TIET_Password = findViewById(R.id.main_TIET_Password);
    }
}