package com.travelplanner.ui;

import com.travelplanner.model.City;
import com.travelplanner.model.WeatherState;
import com.travelplanner.observer.WeatherObserver;
import com.travelplanner.observer.WeatherReportProvider;
import com.travelplanner.repository.CityRepository;
import com.travelplanner.strategy.CitySortStrategy;
import com.travelplanner.strategy.CitySorter;
import com.travelplanner.strategy.NameSortStrategy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class MainFrame extends JFrame implements WeatherObserver {

    private final WeatherReportProvider    provider;
    private final CitySorter               sorter        = new CitySorter(new NameSortStrategy());
    private       WeatherState             currentFilter = WeatherState.SUNNY;
    private       List<City>               latestCities;

    private final ControlPanel             controlPanel;
    private final AllCitiesPanel           allCitiesPanel;
    private final WeatherFilterPanel       weatherFilterPanel;
    private final PlannerPanel             plannerPanel;
    private final TemperatureBarChartPanel barChartPanel;
    private final WeatherPieChartPanel     pieChartPanel;

    public MainFrame() {
        super("Travel Planner System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        latestCities = CityRepository.getInstance().getCities();
        provider     = new WeatherReportProvider(latestCities);
        provider.addObserver(this);

        controlPanel       = new ControlPanel();
        allCitiesPanel     = new AllCitiesPanel();
        weatherFilterPanel = new WeatherFilterPanel();
        plannerPanel       = new PlannerPanel();
        barChartPanel      = new TemperatureBarChartPanel();
        pieChartPanel      = new WeatherPieChartPanel();

        buildLayout();
        wireEvents();
        refreshAll(provider.getCities());

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                provider.stop();
                dispose();
                System.exit(0);
            }
        });

        setVisible(true);
        provider.start();
    }

    // -------------------------------------------------------------------------
    // Layout
    //
    //  ┌─ controlPanel (NORTH) ──────────────────────────────────────────────┐
    //  ├─ leftPanel (WEST, 260px) ──┬─ rightPanel (CENTER) ─────────────────┤
    //  │  allCitiesPanel            │  chartsPanel (CENTER, GridLayout 1×2)  │
    //  │  ─────────────────         │    barChartPanel │ pieChartPanel        │
    //  │  weatherFilterPanel        ├────────────────────────────────────────┤
    //  │                            │  plannerPanel (SOUTH, 230px tall)      │
    //  └────────────────────────────┴────────────────────────────────────────┘
    //
    // No JSplitPane: plain BorderLayout + GridLayout panels are sized by the
    // layout manager immediately at validate() time, with no post-show calls.
    // This works reliably on X11, XWayland, and tiling Wayland compositors.
    // -------------------------------------------------------------------------
    private void buildLayout() {
        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);

        // ---- Left column: city lists stacked in equal halves ----------------
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        leftPanel.setPreferredSize(new Dimension(260, 0));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 2));
        leftPanel.add(allCitiesPanel);
        leftPanel.add(weatherFilterPanel);

        // ---- Charts: bar chart and pie chart side by side -------------------
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 4, 0));
        chartsPanel.add(barChartPanel);
        chartsPanel.add(pieChartPanel);

        // ---- Right column: charts fill center, planner anchored to bottom --
        plannerPanel.setPreferredSize(new Dimension(0, 230));

        JPanel rightPanel = new JPanel(new BorderLayout(0, 4));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 4));
        rightPanel.add(chartsPanel,   BorderLayout.CENTER);
        rightPanel.add(plannerPanel,  BorderLayout.SOUTH);

        // ---- Combine --------------------------------------------------------
        add(leftPanel,  BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    // -------------------------------------------------------------------------
    // Events
    // -------------------------------------------------------------------------
    private void wireEvents() {
        controlPanel.getSortCombo().addActionListener(e -> {
            CitySortStrategy strategy =
                    (CitySortStrategy) controlPanel.getSortCombo().getSelectedItem();
            sorter.setStrategy(strategy);
            allCitiesPanel.setCities(sorter.sort(latestCities));
        });

        controlPanel.getWeatherCombo().addActionListener(e -> {
            currentFilter = (WeatherState) controlPanel.getWeatherCombo().getSelectedItem();
            weatherFilterPanel.setCitiesWithFilter(latestCities, currentFilter);
        });

        allCitiesPanel.getList().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                plannerPanel.setCity(allCitiesPanel.getSelectedCity());
            }
        });
    }

    // -------------------------------------------------------------------------
    // WeatherObserver — background thread calls this; must not touch Swing here
    // -------------------------------------------------------------------------
    @Override
    public void onWeatherUpdated(List<City> cities) {
        SwingUtilities.invokeLater(() -> refreshAll(cities));
    }

    private void refreshAll(List<City> cities) {
        latestCities = cities;
        allCitiesPanel.setCities(sorter.sort(cities));
        weatherFilterPanel.setCitiesWithFilter(cities, currentFilter);
        barChartPanel.setCities(cities);
        pieChartPanel.setCities(cities);
    }
}
