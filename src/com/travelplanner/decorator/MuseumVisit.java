package com.travelplanner.decorator;

public class MuseumVisit extends CityActivityDecorator {

    private static final String LABEL = "Museum Visit";
    private static final double COST  = 15.0;
    private static final double HOURS = 3.0;

    public MuseumVisit(CityActivity wrapped) {
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
