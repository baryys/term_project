package com.travelplanner.decorator;

public class ShoppingMallVisit extends CityActivityDecorator {

    private static final String LABEL = "Shopping Mall Visit";
    private static final double COST  = 100.0;
    private static final double HOURS = 4.0;

    public ShoppingMallVisit(CityActivity wrapped) {
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
