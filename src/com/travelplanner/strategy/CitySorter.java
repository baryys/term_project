package com.travelplanner.strategy;

import com.travelplanner.model.City;

import java.util.List;

public class CitySorter {

    private CitySortStrategy strategy;

    public CitySorter(CitySortStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(CitySortStrategy strategy) {
        this.strategy = strategy;
    }

    public CitySortStrategy getStrategy() {
        return strategy;
    }

    public List<City> sort(List<City> cities) {
        return strategy.sort(cities);
    }
}
