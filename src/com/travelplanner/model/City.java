package com.travelplanner.model;

public class City {

    private String name;
    private int population;
    private double area;
    private double currentTemperature;
    private WeatherState currentWeatherState;

    public City(String name, int population, double area,
                double currentTemperature, WeatherState currentWeatherState) {
        this.name = name;
        this.population = population;
        this.area = area;
        this.currentTemperature = currentTemperature;
        this.currentWeatherState = currentWeatherState;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPopulation() { return population; }
    public void setPopulation(int population) { this.population = population; }

    public double getArea() { return area; }
    public void setArea(double area) { this.area = area; }

    public double getCurrentTemperature() { return currentTemperature; }
    public void setCurrentTemperature(double currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public WeatherState getCurrentWeatherState() { return currentWeatherState; }
    public void setCurrentWeatherState(WeatherState currentWeatherState) {
        this.currentWeatherState = currentWeatherState;
    }

    @Override
    public String toString() {
        return "City{name='" + name + "', population=" + population +
               ", area=" + area + ", currentTemperature=" + currentTemperature +
               ", currentWeatherState=" + currentWeatherState + "}";
    }
}
