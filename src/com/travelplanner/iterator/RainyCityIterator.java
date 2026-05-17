package com.travelplanner.iterator;

import com.travelplanner.model.City;
import com.travelplanner.model.WeatherState;

import java.util.List;

public class RainyCityIterator extends AbstractWeatherCityIterator {

    public RainyCityIterator(List<City> cities) {
        super(cities);
    }

    @Override
    protected WeatherState getWeatherState() {
        return WeatherState.RAINY;
    }
}
