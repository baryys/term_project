package com.travelplanner.ui;

import com.travelplanner.model.City;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AllCitiesPanel extends JPanel {

    private final DefaultListModel<City> listModel = new DefaultListModel<>();
    private final JList<City>            cityList;

    public AllCitiesPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("All Cities"));

        cityList = new JList<>(listModel);
        cityList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cityList.setCellRenderer(new CityListCellRenderer());

        add(new JScrollPane(cityList), BorderLayout.CENTER);
    }

    /**
     * Replaces the displayed list while preserving the currently selected city
     * by name so that sort and weather updates don't discard the selection.
     */
    public void setCities(List<City> cities) {
        City prevSelected = cityList.getSelectedValue();
        listModel.clear();
        for (City c : cities) listModel.addElement(c);
        if (prevSelected != null) {
            for (int i = 0; i < listModel.size(); i++) {
                if (listModel.get(i).getName().equals(prevSelected.getName())) {
                    cityList.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    public City getSelectedCity()  { return cityList.getSelectedValue(); }
    public JList<City> getList()   { return cityList; }
}
