package com.travelplanner.ui;

import com.travelplanner.model.WeatherState;
import com.travelplanner.strategy.AreaSortStrategy;
import com.travelplanner.strategy.CitySortStrategy;
import com.travelplanner.strategy.NameSortStrategy;
import com.travelplanner.strategy.PopulationSortStrategy;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {

    private final JComboBox<CitySortStrategy> sortCombo;
    private final JComboBox<WeatherState>     weatherCombo;

    public ControlPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 14, 8));
        setBorder(BorderFactory.createEtchedBorder());

        add(new JLabel("Sort by:"));
        sortCombo = new JComboBox<>(new CitySortStrategy[]{
                new NameSortStrategy(),
                new PopulationSortStrategy(),
                new AreaSortStrategy()
        });
        sortCombo.setPreferredSize(new Dimension(180, 26));
        add(sortCombo);

        add(Box.createHorizontalStrut(24));

        add(new JLabel("Weather filter:"));
        weatherCombo = new JComboBox<>(WeatherState.values());
        weatherCombo.setPreferredSize(new Dimension(110, 26));
        add(weatherCombo);
    }

    public JComboBox<CitySortStrategy> getSortCombo()    { return sortCombo; }
    public JComboBox<WeatherState>     getWeatherCombo() { return weatherCombo; }
}
