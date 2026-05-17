package com.travelplanner.iterator;

import com.travelplanner.model.City;
import com.travelplanner.model.WeatherState;

import java.util.List;

public class SunnyCityIterator extends AbstractWeatherCityIterator {

    public SunnyCityIterator(List<City> cities) {
        super(cities);
    }

    @Override
    protected WeatherState getWeatherState() {
        return WeatherState.SUNNY;
    }
}
