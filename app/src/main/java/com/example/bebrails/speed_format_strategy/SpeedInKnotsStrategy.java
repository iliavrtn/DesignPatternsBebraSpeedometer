package com.example.bebrails.speed_format_strategy;

import java.util.Locale;

public class SpeedInKnotsStrategy implements SpeedFormatStrategy {

    public static final double KNOTS_IN_METER_PER_SECOND = 1.94384;

    @Override
    public String formatSpeed(float speed) {
        return String.format(Locale.ROOT, "Speed: %.2f knots", speed * KNOTS_IN_METER_PER_SECOND);
    }
}