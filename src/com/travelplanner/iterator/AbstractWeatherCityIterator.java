package com.travelplanner.iterator;

import com.travelplanner.model.City;
import com.travelplanner.model.WeatherState;

import java.util.List;
import java.util.NoSuchElementException;

abstract class AbstractWeatherCityIterator implements WeatherCityIterator {

    private final List<City> cities;
    private int cursor;

    protected AbstractWeatherCityIterator(List<City> cities) {
        this.cities = cities;
        this.cursor = 0;
        advance();
    }

    protected abstract WeatherState getWeatherState();

    @Override
    public boolean hasNext() {
        return cursor < cities.size();
    }

    @Override
    public City next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more cities with weather: " + getWeatherState());
        }
        City city = cities.get(cursor);
        cursor++;
        advance();
        return city;
    }

    // Move cursor forward until it points at a matching city (or past the end).
    private void advance() {
        while (cursor < cities.size()
                && cities.get(cursor).getCurrentWeatherState() != getWeatherState()) {
            cursor++;
        }
    }
}
