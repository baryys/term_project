package com.travelplanner.iterator;

import com.travelplanner.model.City;
import com.travelplanner.model.WeatherState;

import java.util.List;

public class CloudyCityIterator extends AbstractWeatherCityIterator {

    public CloudyCityIterator(List<City> cities) {
        super(cities);
    }

    @Override
    protected WeatherState getWeatherState() {
        return WeatherState.CLOUDY;
    }
}
