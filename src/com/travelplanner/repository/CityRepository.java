package com.travelplanner.repository;

import com.travelplanner.model.City;
import com.travelplanner.util.SimpleCityJsonParser;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public final class CityRepository {

    private static CityRepository instance;

    private final List<City> cities;

    private CityRepository() {
        try {
            cities = Collections.unmodifiableList(
                    SimpleCityJsonParser.parse("cities.json"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load cities.json", e);
        }
    }

    public static CityRepository getInstance() {
        if (instance == null) {
            instance = new CityRepository();
        }
        return instance;
    }

    public List<City> getCities() {
        return cities;
    }
}
