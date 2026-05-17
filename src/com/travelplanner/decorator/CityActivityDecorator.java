package com.travelplanner.decorator;

import com.travelplanner.model.City;

public abstract class CityActivityDecorator implements CityActivity {

    protected final CityActivity wrapped;

    protected CityActivityDecorator(CityActivity wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public City getCity() {
        return wrapped.getCity();
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription();
    }

    @Override
    public double getCost() {
        return wrapped.getCost();
    }

    @Override
    public double getRequiredHours() {
        return wrapped.getRequiredHours();
    }
}
