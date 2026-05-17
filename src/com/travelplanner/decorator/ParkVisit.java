package com.travelplanner.decorator;

public class ParkVisit extends CityActivityDecorator {

    private static final String LABEL = "Park Visit";
    private static final double COST  = 5.0;
    private static final double HOURS = 2.0;

    public ParkVisit(CityActivity wrapped) {
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
