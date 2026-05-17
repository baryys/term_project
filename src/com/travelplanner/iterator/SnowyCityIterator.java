package com.travelplanner.iterator;

import com.travelplanner.model.City;
import com.travelplanner.model.WeatherState;

import java.util.List;

public class SnowyCityIterator extends AbstractWeatherCityIterator {

    public SnowyCityIterator(List<City> cities) {
        super(cities);
    }

    @Override
    protected WeatherState getWeatherState() {
        return WeatherState.SNOWY;
    }
}
