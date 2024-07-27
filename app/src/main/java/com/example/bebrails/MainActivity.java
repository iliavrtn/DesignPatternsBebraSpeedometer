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
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gravitySensor;
    private TextView speedTextView;
    private SwitchCompat themeSwitch;
    private SharedPreferences preferences;
    private boolean isDarkMode;
    private boolean firstRun=true;
    private String currentUnit = "m/s";  // Default unit

    Vector3D gravity;
    private boolean gravityIsSet=false;
    Vector3D linearAcceleration;
    private float speed = 0.0f;
    Vector3D velocity = new Vector3D(0.0f,0.0f,0.0f);
    private long lastUpdateTime = 0;

    private static final float SPEED_THRESHOLD = 0.1f;
    private static final float NOISE_THRESHOLD = 0.1f;

    private Timer timer;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences("appPreferences", MODE_PRIVATE);
        isDarkMode = preferences.getBoolean("darkMode", false);
        setTheme(isDarkMode ? R.style.Theme_Bebrails_Dark : R.style.Theme_Bebrails_Light);
        setContentView(R.layout.activity_main);

        speedTextView = findViewById(R.id.speedTextView);
        themeSwitch = findViewById(R.id.themeSwitch);
        themeSwitch.setChecked(isDarkMode);
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> toggleTheme(isChecked));

        Button buttonKmh = findViewById(R.id.buttonKmh);
        Button buttonKnots = findViewById(R.id.buttonKnots);
        Button buttonMs = findViewById(R.id.buttonMs);

        buttonKmh.setOnClickListener(v -> currentUnit = "km/h");
        buttonKnots.setOnClickListener(v -> currentUnit = "knots");
        buttonMs.setOnClickListener(v -> currentUnit = "m/s");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        handler = new Handler(Looper.getMainLooper());
        timer = new Timer();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            gravity = new Vector3D(event.values[0], event.values[1], event.values[2]);
            gravityIsSet = true;
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && gravityIsSet) {
            linearAcceleration = new Vector3D(event.values[0], event.values[1], event.values[2]);
            linearAcceleration = linearAcceleration.subtract(gravity);

            long currentTime = System.nanoTime();
            if (lastUpdateTime != 0) {
                float deltaTime = (currentTime - lastUpdateTime) / 1000000000.0f;
                if (linearAcceleration.magnitude()>NOISE_THRESHOLD) {
                    velocity = velocity.add(linearAcceleration.scale(deltaTime));
                    speed = velocity.magnitude();
                }
            }
            lastUpdateTime = currentTime;

            // Apply speed threshold
            if (speed < SPEED_THRESHOLD) {
                speed = 0;
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this example
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_FASTEST);

        // Schedule the timer to update the speed display every second
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> updateSpeedDisplay(speed));
            }
        }, 0, 1000); // Delay of 0 ms, interval of 1000 ms (1 second)
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);

        // Cancel the timer when the activity is paused
        timer.cancel();
    }

    private void updateSpeedDisplay(float speed) {
        String speedText;
        switch (currentUnit) {
            case "km/h":
                speedText = String.format("Speed: %.2f km/h", speed * 3.6);
                break;
            case "knots":
                speedText = String.format("Speed: %.2f knots", speed * 1.94384);
                break;
            default:
                speedText = String.format("Speed: %.2f m/s", speed);
                break;
        }
        speedTextView.setText(speedText);
    }

    private void toggleTheme(boolean isDarkMode) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("darkMode", isDarkMode);
        editor.apply();
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        recreate();
    }
}