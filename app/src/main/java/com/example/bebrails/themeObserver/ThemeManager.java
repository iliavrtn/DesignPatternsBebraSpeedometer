package com.example.bebrails.themeObserver;

import java.util.ArrayList;
import java.util.List;

public class ThemeManager {
    private final List<ThemeObserver> observers = new ArrayList<>();
    private static volatile ThemeManager instance;  // Volatile variable to ensure visibility across threads
    private Boolean isDarkMode = null;

    // Private constructor to prevent instantiation
    private ThemeManager() {}

    // Double-checked locking to ensure thread safety
    public static ThemeManager getInstance() {
        if (instance == null) {
            synchronized (ThemeManager.class) {  // Synchronize on the class object
                if (instance == null) {
                    instance = new ThemeManager();
                }
            }
        }
        return instance;
    }

    public void addObserver(ThemeObserver observer) {
        synchronized (observers) {  // Synchronize access to the mutable list
            if (!observers.contains(observer)) {
                observers.add(observer);
            }
        }
    }

    public void removeObserver(ThemeObserver observer) {
        synchronized (observers) {  // Synchronize access to the mutable list
            observers.remove(observer);
        }
    }

    public void notifyThemeChanged(boolean isDarkMode) {
        synchronized (this) {  // Synchronize access to the isDarkMode variable
            if (this.isDarkMode != null && this.isDarkMode.equals(isDarkMode)) {
                return;  // Return if the theme hasn't changed
            }
            this.isDarkMode = isDarkMode;
        }
        for (ThemeObserver observer : observers) {
            observer.onThemeChanged(isDarkMode);  // Notify observers outside of the synchronized block
        }
    }
}
