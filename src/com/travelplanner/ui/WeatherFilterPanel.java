package com.travelplanner.ui;

import com.travelplanner.iterator.CloudyCityIterator;
import com.travelplanner.iterator.RainyCityIterator;
import com.travelplanner.iterator.SnowyCityIterator;
import com.travelplanner.iterator.SunnyCityIterator;
import com.travelplanner.iterator.WeatherCityIterator;
import com.travelplanner.model.City;
import com.travelplanner.model.WeatherState;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WeatherFilterPanel extends JPanel {

    private final DefaultListModel<City> listModel = new DefaultListModel<>();
    private final JList<City>            cityList;
    private final JLabel                 countLabel = new JLabel("0 cities");

    public WeatherFilterPanel() {
        setLayout(new BorderLayout(3, 3));
        setBorder(BorderFactory.createTitledBorder("Weather-Filtered Cities"));

        cityList = new JList<>(listModel);
        cityList.setCellRenderer(new CityListCellRenderer());

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 2));
        south.add(countLabel);

        add(new JScrollPane(cityList), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
    }

    /**
     * Rebuilds the list using the appropriate WeatherCityIterator —
     * no stream filtering or direct list traversal in the GUI layer.
     */
    public void setCitiesWithFilter(List<City> cities, WeatherState filter) {
        listModel.clear();
        WeatherCityIterator it = createIterator(cities, filter);
        while (it.hasNext()) listModel.addElement(it.next());
        countLabel.setText(listModel.size() + " cities");
    }

    private WeatherCityIterator createIterator(List<City> cities, WeatherState filter) {
        switch (filter) {
            case SUNNY:  return new SunnyCityIterator(cities);
            case CLOUDY: return new CloudyCityIterator(cities);
            case RAINY:  return new RainyCityIterator(cities);
            case SNOWY:  return new SnowyCityIterator(cities);
            default:     return new SunnyCityIterator(cities);
        }
    }
}
