package com.travelplanner.observer;

import com.travelplanner.model.City;
import com.travelplanner.model.WeatherState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class WeatherReportProvider implements WeatherSubject {

    private static final int UPDATE_INTERVAL_MS = 3000;
    private static final double MAX_TEMP_DELTA   = 2.0;
    private static final double MIN_TEMP         = -30.0;
    private static final double MAX_TEMP         = 50.0;
    /** Probability (1-in-N) that a city's WeatherState changes each tick. */
    private static final int    WEATHER_CHANGE_ODDS = 5;

    private final List<WeatherObserver> observers = new CopyOnWriteArrayList<>();
    private final List<City>            cities;
    private final Random                random    = new Random();

    private volatile boolean running;
    private Thread           workerThread;

    public WeatherReportProvider(List<City> cities) {
        // Own mutable copy so updates don't race with the repository's list.
        this.cities = new ArrayList<>(cities);
    }

    // -------------------------------------------------------------------------
    // WeatherSubject
    // -------------------------------------------------------------------------

    @Override
    public void addObserver(WeatherObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(WeatherObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        // Pass an unmodifiable snapshot so observers cannot mutate the list.
        List<City> snapshot = Collections.unmodifiableList(new ArrayList<>(cities));
        for (WeatherObserver observer : observers) {
            observer.onWeatherUpdated(snapshot);
        }
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    public void start() {
        if (running) return;
        running = true;
        workerThread = new Thread(this::updateLoop, "WeatherReportProvider");
        workerThread.setDaemon(true); // does not block JVM shutdown
        workerThread.start();
    }

    public void stop() {
        running = false;
        if (workerThread != null) {
            workerThread.interrupt();
        }
    }

    // -------------------------------------------------------------------------
    // Background loop
    // -------------------------------------------------------------------------

    private void updateLoop() {
        while (running) {
            try {
                Thread.sleep(UPDATE_INTERVAL_MS);
                updateCities();
                notifyObservers();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void updateCities() {
        WeatherState[] states = WeatherState.values();
        for (City city : cities) {
            double delta = (random.nextDouble() * 2 - 1) * MAX_TEMP_DELTA;
            double newTemp = city.getCurrentTemperature() + delta;
            city.setCurrentTemperature(
                    Math.max(MIN_TEMP, Math.min(MAX_TEMP, newTemp)));

            if (random.nextInt(WEATHER_CHANGE_ODDS) == 0) {
                city.setCurrentWeatherState(states[random.nextInt(states.length)]);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /** Returns an unmodifiable snapshot of the current city list. */
    public List<City> getCities() {
        return Collections.unmodifiableList(new ArrayList<>(cities));
    }
}
