package com.travelplanner.ui;

import com.travelplanner.model.City;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

class CityListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof City) {
            City c = (City) value;
            setText(String.format("%-15s  %6.1f°C  %s",
                    c.getName(), c.getCurrentTemperature(), c.getCurrentWeatherState()));
            setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            setBorder(new EmptyBorder(2, 5, 2, 5));
        }
        return this;
    }
}
