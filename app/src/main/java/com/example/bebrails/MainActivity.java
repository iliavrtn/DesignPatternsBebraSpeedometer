package com.example.bebrails;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView speedTextView;
    private Button buttonKmh, buttonKnots, buttonMs;
    private Switch themeSwitch;
    private double speed = 0.0;
    private String currentUnit = "m/s";
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        speedTextView = findViewById(R.id.speedTextView);
        buttonKmh = findViewById(R.id.buttonKmh);
        buttonKnots = findViewById(R.id.buttonKnots);
        buttonMs = findViewById(R.id.buttonMs);
        themeSwitch = findViewById(R.id.themeSwitch);

        preferences = getSharedPreferences("app_prefs", MODE_PRIVATE);

        // Restore user preferences
        currentUnit = preferences.getString("unit", "m/s");
        boolean isDarkMode = preferences.getBoolean("dark_mode", false);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        updateSpeedDisplay();

        buttonKmh.setOnClickListener(v -> {
            currentUnit = "km/h";
            savePreferences();
            updateSpeedDisplay();
        });

        buttonKnots.setOnClickListener(v -> {
            currentUnit = "knots";
            savePreferences();
            updateSpeedDisplay();
        });

        buttonMs.setOnClickListener(v -> {
            currentUnit = "m/s";
            savePreferences();
            updateSpeedDisplay();
        });

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            savePreferences();
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            speed = Math.sqrt(x * x + y * y + z * z);
            updateSpeedDisplay();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No action needed
    }

    private void updateSpeedDisplay() {
        double displaySpeed;
        switch (currentUnit) {
            case "km/h":
                displaySpeed = speed * 3.6 - 35.22;
                break;
            case "knots":
                displaySpeed = speed * 1.94384 - 19;
                break;
            default:
                displaySpeed = speed - 9.79;
                break;
        }
        speedTextView.setText(String.format("%.2f %s", displaySpeed, currentUnit));
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("unit", currentUnit);
        editor.putBoolean("dark_mode", themeSwitch.isChecked());
        editor.apply();
    }
}
