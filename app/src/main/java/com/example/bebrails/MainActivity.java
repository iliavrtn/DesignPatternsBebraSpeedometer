package com.example.bebrails;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.example.bebrails.speed_format_strategy.SpeedFormatStrategy;
import com.example.bebrails.speed_format_strategy.SpeedInKilometersPerHourStrategy;
import com.example.bebrails.speed_format_strategy.SpeedInKnotsStrategy;
import com.example.bebrails.speed_format_strategy.SpeedInMetersPerSecondStrategy;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public static final int DISPLAY_SPEED_REFRESH_INTERVAL_IN_MILLISECONDS = 100;
    private Sensor accelerometer;
    private Sensor gravitySensor;
    private TextView speedTextView;
    private SharedPreferences preferences;
    private SpeedFormatStrategy speedFormatStrategy = new SpeedInMetersPerSecondStrategy();
    private final Speedometer speedometer = new Speedometer();



    private Timer timer;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences("appPreferences", MODE_PRIVATE);
        boolean isDarkMode = preferences.getBoolean("darkMode", false);
        setTheme(isDarkMode ? R.style.Theme_Bebrails_Dark : R.style.Theme_Bebrails_Light);
        setContentView(R.layout.activity_main);

        speedTextView = findViewById(R.id.speedTextView);
        SwitchCompat themeSwitch = findViewById(R.id.themeSwitch);
        themeSwitch.setChecked(isDarkMode);
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> toggleTheme(isChecked));

        Button buttonKmh = findViewById(R.id.buttonKmh);
        Button buttonKnots = findViewById(R.id.buttonKnots);
        Button buttonMs = findViewById(R.id.buttonMs);

        buttonKmh.setOnClickListener(v -> speedFormatStrategy = new SpeedInKilometersPerHourStrategy());
        buttonKnots.setOnClickListener(v -> speedFormatStrategy = new SpeedInKnotsStrategy());
        buttonMs.setOnClickListener(v -> speedFormatStrategy = new SpeedInMetersPerSecondStrategy());


        handler = new Handler(Looper.getMainLooper());
        timer = new Timer();
    }

    private Vector3D getVectorFromEvent(SensorEvent event){
        return new Vector3D(event.values[0], event.values[1], event.values[2]);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_GRAVITY:
                speedometer.updateGravity(getVectorFromEvent(event));
                break;
            case Sensor.TYPE_ACCELEROMETER:
                speedometer.updateAcceleration(getVectorFromEvent(event));
                break;
            default:
                break;
        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this example
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerSensorListeners();

        // Schedule the timer to update the speed display every second


        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    updateSpeedDisplay();
                });
            }
        }, 0, DISPLAY_SPEED_REFRESH_INTERVAL_IN_MILLISECONDS);
    }

    private void registerSensorListeners() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void updateSpeedDisplay() {
        float currentSpeed = speedometer.getSpeed();
        String formattedSpeed = speedFormatStrategy.formatSpeed(currentSpeed);
        speedTextView.setText(formattedSpeed);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(this);

        // Cancel the timer when the activity is paused
        timer.cancel();
    }


    private void toggleTheme(boolean isDarkMode) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("darkMode", isDarkMode);
        editor.apply();
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        recreate();
    }
}