package com.travelplanner.observer;

import com.travelplanner.model.City;

import java.util.List;

public interface WeatherObserver {
    /**
     * Called on the background weather thread.
     * Implementations that touch Swing components must dispatch via
     * SwingUtilities.invokeLater(...).
     */
    void onWeatherUpdated(List<City> cities);
}
