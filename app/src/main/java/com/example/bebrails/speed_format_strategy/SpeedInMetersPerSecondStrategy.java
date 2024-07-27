package com.example.bebrails.speed_format_strategy;

import java.util.Locale;

public class SpeedInMetersPerSecondStrategy implements SpeedFormatStrategy {
    @Override
    public String formatSpeed(float speed) {
        return String.format(Locale.ROOT, "Speed: %.2f m/s", speed);
    }
}
