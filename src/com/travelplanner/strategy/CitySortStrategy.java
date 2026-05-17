package com.travelplanner.strategy;

import com.travelplanner.model.City;

import java.util.List;

public interface CitySortStrategy {
    List<City> sort(List<City> cities);
}
