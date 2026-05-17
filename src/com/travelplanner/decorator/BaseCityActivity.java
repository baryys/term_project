package com.travelplanner.decorator;

import com.travelplanner.model.City;

public class BaseCityActivity implements CityActivity {

    private final City city;

    public BaseCityActivity(City city) {
        this.city = city;
    }

    @Override
    public City getCity() {
        return city;
    }

    @Override
    public String getDescription() {
        return "Trip to " + city.getName();
    }

    @Override
    public double getCost() {
        return 0.0;
    }

    @Override
    public double getRequiredHours() {
        return 0.0;
    }
}
