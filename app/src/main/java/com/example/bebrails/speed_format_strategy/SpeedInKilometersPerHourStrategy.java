package com.example.bebrails.speed_format_strategy;

public class SpeedInKilometersPerHourStrategy implements SpeedFormatStrategy {
    @Override
    public String formatSpeed(float speed) {
        return String.format("Speed: %.2f km/h", speed * 3.6);
    }
}
