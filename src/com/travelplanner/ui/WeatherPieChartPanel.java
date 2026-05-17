package com.travelplanner.ui;

import com.travelplanner.model.City;
import com.travelplanner.model.WeatherState;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WeatherPieChartPanel extends JPanel {

    private static final WeatherState[] STATES = WeatherState.values();

    // One colour per WeatherState (same index order as the enum)
    private static final Color[] COLORS = {
            new Color(255, 210,  40),   // SUNNY  — golden yellow
            new Color(145, 150, 172),   // CLOUDY — slate gray
            new Color( 65, 125, 195),   // RAINY  — steel blue
            new Color(195, 225, 255),   // SNOWY  — ice blue
    };

    private List<City> cities = new ArrayList<>();

    public WeatherPieChartPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(290, 270));
    }

    /** Called by MainFrame on the EDT after every weather update. */
    public void setCities(List<City> cities) {
        this.cities = new ArrayList<>(cities);
        repaint();
    }

    // -------------------------------------------------------------------------
    // Painting
    // -------------------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            drawTitle(g2);
            if (cities.isEmpty()) {
                drawPlaceholder(g2);
            } else {
                drawChart(g2);
            }
        } finally {
            g2.dispose();
        }
    }

    private void drawTitle(Graphics2D g2) {
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        g2.setColor(new Color(40, 40, 40));
        String title = "Weather Distribution";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, (getWidth() - fm.stringWidth(title)) / 2, 20);
    }

    private void drawPlaceholder(Graphics2D g2) {
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.setColor(Color.GRAY);
        g2.drawString("No city data", getWidth() / 2 - 30, getHeight() / 2);
    }

    private void drawChart(Graphics2D g2) {
        // ---- Count cities per weather state ----------------------------------
        int[] counts = new int[STATES.length];
        for (City c : cities) {
            for (int i = 0; i < STATES.length; i++) {
                if (c.getCurrentWeatherState() == STATES[i]) {
                    counts[i]++;
                    break;
                }
            }
        }
        int total = cities.size();

        // Find the ACTUAL last non-zero state index for rounding correction.
        // Checking only STATES.length - 1 is wrong when that state has 0 cities.
        int lastNonZero = -1;
        for (int i = STATES.length - 1; i >= 0; i--) {
            if (counts[i] > 0) { lastNonZero = i; break; }
        }
        if (lastNonZero == -1) return; // all cities have no weather state (shouldn't happen)

        // ---- Geometry --------------------------------------------------------
        final int PAD      = 12;
        final int LEGEND_W = 125;
        final int TOP_OFF  = 26;  // leave room for the title
        int w = getWidth();
        int h = getHeight();
        int availW   = w - LEGEND_W - PAD * 3;
        int availH   = h - TOP_OFF - PAD * 2;
        int pieSize  = Math.min(availW, availH);
        if (pieSize < 30) return;

        int pieX = PAD;
        int pieY = TOP_OFF + (availH - pieSize) / 2;
        int cx   = pieX + pieSize / 2;
        int cy   = pieY + pieSize / 2;
        int r    = pieSize / 2;

        // ---- Draw slices (start from 12 o'clock = 90°) -----------------------
        int startAngle = 90;   // Java's fillArc: 0 = 3 o'clock, positive = counter-clockwise
        for (int i = 0; i < STATES.length; i++) {
            if (counts[i] == 0) continue;

            // The last non-zero slice absorbs the rounding error so the pie
            // always closes exactly (no 1–2° gap from accumulated rounding).
            int arc;
            if (i == lastNonZero) {
                arc = (90 + 360) - startAngle;   // fill whatever remains
            } else {
                arc = (int) Math.round(360.0 * counts[i] / total);
            }

            // Filled slice
            g2.setColor(COLORS[i]);
            g2.fillArc(pieX, pieY, pieSize, pieSize, startAngle, arc);

            // White separator line (drawn as a thin arc outline on each slice)
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2f));
            g2.drawArc(pieX, pieY, pieSize, pieSize, startAngle, arc);

            // Percentage label inside the slice (skip if the slice is too small)
            if (arc > 15) {
                double midRad = Math.toRadians(startAngle + arc / 2.0);
                // Note: fillArc uses screen y-direction, so sin is negated
                int lx = cx + (int) (r * 0.60 * Math.cos(-midRad));
                int ly = cy + (int) (r * 0.60 * Math.sin(-midRad));

                String pct = String.format("%.0f%%", 100.0 * counts[i] / total);
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                FontMetrics fm = g2.getFontMetrics();
                g2.setColor(sliceLabelColor(COLORS[i]));
                g2.drawString(pct, lx - fm.stringWidth(pct) / 2, ly + fm.getAscent() / 2);
            }

            startAngle += arc;
        }

        // Outer circle border
        g2.setColor(new Color(90, 90, 90));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(pieX, pieY, pieSize, pieSize);

        // ---- Legend ----------------------------------------------------------
        int lx = pieX + pieSize + PAD * 2;
        int ly = cy - (STATES.length * 24) / 2;

        for (int i = 0; i < STATES.length; i++) {
            // Colour swatch
            g2.setColor(COLORS[i]);
            g2.fillRoundRect(lx, ly, 14, 14, 4, 4);
            g2.setColor(new Color(90, 90, 90));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(lx, ly, 14, 14, 4, 4);

            // Label: "Sunny: 8 (53%)"
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.setColor(new Color(40, 40, 40));
            String pct   = (total > 0) ? String.format(" %.0f%%", 100.0 * counts[i] / total) : "";
            String label = capitalize(STATES[i].name()) + ": " + counts[i] + pct;
            g2.drawString(label, lx + 20, ly + 11);
            ly += 24;
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Dark label on light slices, light label on dark slices. */
    private static Color sliceLabelColor(Color bg) {
        double luminance = 0.299 * bg.getRed() + 0.587 * bg.getGreen() + 0.114 * bg.getBlue();
        return (luminance > 160) ? new Color(30, 30, 30) : Color.WHITE;
    }

    private static String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }
}
