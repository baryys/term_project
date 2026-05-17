package com.travelplanner.decorator;

import com.travelplanner.model.City;

public interface CityActivity {
    City   getCity();
    String getDescription();
    double getCost();
    double getRequiredHours();
}
