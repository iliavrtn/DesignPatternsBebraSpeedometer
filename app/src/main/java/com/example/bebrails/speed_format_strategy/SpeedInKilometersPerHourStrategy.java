package com.example.bebrails.speed_format_strategy;

import java.util.Locale;

public class SpeedInKilometersPerHourStrategy implements SpeedFormatStrategy {

    public static final double KILOMETERS_PER_HOUR_IN_METER_PER_SECOND = 3.6;

    @Override
    public String formatSpeed(float speed) {
        return String.format(Locale.ROOT, "Speed: %.2f km/h", speed * KILOMETERS_PER_HOUR_IN_METER_PER_SECOND);
    }
}
