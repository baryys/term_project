package com.travelplanner.strategy;

import com.travelplanner.model.City;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AreaSortStrategy implements CitySortStrategy {

    @Override
    public List<City> sort(List<City> cities) {
        List<City> sorted = new ArrayList<>(cities);
        sorted.sort(Comparator.comparingDouble(City::getArea).reversed());
        return sorted;
    }

    @Override
    public String toString() { return "Area (large → small)"; }
}
