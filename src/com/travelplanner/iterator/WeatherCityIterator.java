package com.travelplanner.iterator;

import com.travelplanner.model.City;

public interface WeatherCityIterator {
    boolean hasNext();
    City next();
}
