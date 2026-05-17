package com.travelplanner.ui;

import com.travelplanner.decorator.BaseCityActivity;
import com.travelplanner.decorator.CityCenterVisit;
import com.travelplanner.decorator.CityActivity;
import com.travelplanner.decorator.MuseumVisit;
import com.travelplanner.decorator.ParkVisit;
import com.travelplanner.decorator.ShoppingMallVisit;
import com.travelplanner.model.City;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;

public class PlannerPanel extends JPanel {

    private final JLabel    cityLabel    = new JLabel("—");
    private final JCheckBox museumCb     = new JCheckBox("Museum Visit  ($15 / 3 h)");
    private final JCheckBox shoppingCb   = new JCheckBox("Shopping Mall  ($100 / 4 h)");
    private final JCheckBox parkCb       = new JCheckBox("Park Visit  ($5 / 2 h)");
    private final JCheckBox cityCenterCb = new JCheckBox("City Center  ($10 / 2.5 h)");
    private final JLabel    costLabel    = new JLabel("Total Cost:  $0.00");
    private final JLabel    hoursLabel   = new JLabel("Total Hours:  0.0 h");
    private final JTextArea descArea     = new JTextArea(3, 22);

    private City currentCity;

    public PlannerPanel() {
        setLayout(new BorderLayout(5, 6));
        setBorder(BorderFactory.createTitledBorder("Activity Planner"));

        // --- North: selected city ---
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        topRow.add(bold("Selected city:"));
        cityLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        topRow.add(cityLabel);
        add(topRow, BorderLayout.NORTH);

        // --- Center: activity checkboxes ---
        JPanel cbPanel = new JPanel(new GridLayout(4, 1, 2, 2));
        cbPanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        cbPanel.add(museumCb);
        cbPanel.add(shoppingCb);
        cbPanel.add(parkCb);
        cbPanel.add(cityCenterCb);
        add(cbPanel, BorderLayout.CENTER);

        // --- South: totals + description ---
        JPanel bottomPanel = new JPanel(new BorderLayout(4, 4));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        costLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        hoursLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

        JPanel numPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        numPanel.add(costLabel);
        numPanel.add(hoursLabel);
        bottomPanel.add(numPanel, BorderLayout.NORTH);

        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 11));
        descArea.setBackground(new Color(248, 248, 248));
        bottomPanel.add(new JScrollPane(descArea), BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        // Hook all checkboxes to the same recalculate listener.
        ItemListener recalc = e -> recalculate();
        museumCb.addItemListener(recalc);
        shoppingCb.addItemListener(recalc);
        parkCb.addItemListener(recalc);
        cityCenterCb.addItemListener(recalc);
    }

    public void setCity(City city) {
        this.currentCity = city;
        cityLabel.setText(city != null ? city.getName() : "—");
        museumCb.setSelected(false);
        shoppingCb.setSelected(false);
        parkCb.setSelected(false);
        cityCenterCb.setSelected(false);
        recalculate();
    }

    private void recalculate() {
        if (currentCity == null) {
            costLabel.setText("Total Cost:  $0.00");
            hoursLabel.setText("Total Hours:  0.0 h");
            descArea.setText("Select a city to plan activities.");
            return;
        }
        // Build the decorator chain from the checked boxes.
        CityActivity plan = new BaseCityActivity(currentCity);
        if (museumCb.isSelected())     plan = new MuseumVisit(plan);
        if (shoppingCb.isSelected())   plan = new ShoppingMallVisit(plan);
        if (parkCb.isSelected())       plan = new ParkVisit(plan);
        if (cityCenterCb.isSelected()) plan = new CityCenterVisit(plan);

        costLabel.setText(String.format("Total Cost:  $%.2f", plan.getCost()));
        hoursLabel.setText(String.format("Total Hours:  %.1f h", plan.getRequiredHours()));
        descArea.setText(plan.getDescription());
    }

    private static JLabel bold(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        return l;
    }
}
