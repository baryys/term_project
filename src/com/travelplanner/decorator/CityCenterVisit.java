package com.travelplanner.decorator;

public class CityCenterVisit extends CityActivityDecorator {

    private static final String LABEL = "City Center Visit";
    private static final double COST  = 10.0;
    private static final double HOURS = 2.5;

    public CityCenterVisit(CityActivity wrapped) {
        super(wrapped);
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + " + " + LABEL;
    }

    @Override
    public double getCost() {
        return wrapped.getCost() + COST;
    }

    @Override
    public double getRequiredHours() {
        return wrapped.getRequiredHours() + HOURS;
    }
}
