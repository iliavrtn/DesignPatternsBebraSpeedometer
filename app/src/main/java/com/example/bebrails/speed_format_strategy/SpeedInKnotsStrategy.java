package com.example.bebrails.speed_format_strategy;

public class SpeedInKnotsStrategy implements SpeedFormatStrategy {
    @Override
    public String formatSpeed(float speed) {
        return String.format("Speed: %.2f knots", speed * 1.94384);
    }
}