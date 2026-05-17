package com.travelplanner.ui;

import com.travelplanner.model.City;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TemperatureBarChartPanel extends JPanel {

    // Fixed margins: left needs room for y-axis labels; bottom for rotated city names
    private static final int ML = 52;   // left margin
    private static final int MR = 14;   // right margin
    private static final int MT = 38;   // top margin  (title lives here)
    private static final int MB = 74;   // bottom margin (rotated labels)

    private static final int Y_TICKS = 6;

    private List<City> cities = new ArrayList<>();

    public TemperatureBarChartPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(430, 270));
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
        String title = "City Temperatures (°C)";
        FontMetrics fm = g2.getFontMetrics();
        int x = ML + (getWidth() - ML - MR - fm.stringWidth(title)) / 2;
        g2.drawString(title, Math.max(ML, x), MT - 12);
    }

    private void drawPlaceholder(Graphics2D g2) {
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.setColor(Color.GRAY);
        g2.drawString("No city data", getWidth() / 2 - 30, getHeight() / 2);
    }

    private void drawChart(Graphics2D g2) {
        int w      = getWidth();
        int h      = getHeight();
        int chartW = w - ML - MR;
        int chartH = h - MT - MB;
        if (chartW < 40 || chartH < 40) return;

        // ---- Temperature range ------------------------------------------------
        double lo = cities.stream().mapToDouble(City::getCurrentTemperature).min().orElse(0);
        double hi = cities.stream().mapToDouble(City::getCurrentTemperature).max().orElse(1);
        if (hi - lo < 1) { hi += 0.5; lo -= 0.5; }
        double rawRange = hi - lo;
        lo -= rawRange * 0.08;   // small gap below the lowest bar
        hi += rawRange * 0.14;   // head-room above the tallest bar
        double range = hi - lo;

        // ---- Bar geometry -----------------------------------------------------
        int n    = cities.size();
        int barW = Math.max(4, (chartW - (n + 1) * 3) / n);
        int gap  = Math.max(2, (chartW - n * barW) / (n + 1));

        // ---- Y-axis gridlines and tick labels ---------------------------------
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        for (int t = 0; t <= Y_TICKS; t++) {
            double tick = lo + range * t / Y_TICKS;
            int    ty   = yPixel(tick, lo, range, chartH);

            // Gridline
            g2.setColor(new Color(225, 225, 225));
            g2.drawLine(ML, ty, ML + chartW, ty);

            // Tick mark on y-axis
            g2.setColor(new Color(100, 100, 100));
            g2.drawLine(ML - 4, ty, ML, ty);

            // Label
            String lbl = String.format("%.0f°", tick);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(lbl, ML - fm.stringWidth(lbl) - 6, ty + fm.getAscent() / 2 - 1);
        }

        // ---- Dashed zero line (only when range straddles 0) ------------------
        if (lo < 0 && hi > 0) {
            int zy = yPixel(0, lo, range, chartH);
            Stroke saved = g2.getStroke();
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    10f, new float[]{6f, 4f}, 0f));
            g2.setColor(new Color(80, 80, 80));
            g2.drawLine(ML + 1, zy, ML + chartW, zy);
            g2.setStroke(saved);
        }

        // ---- Bars -------------------------------------------------------------
        for (int i = 0; i < n; i++) {
            City   city = cities.get(i);
            double temp = city.getCurrentTemperature();

            int bx    = ML + gap + i * (barW + gap);
            int topY  = yPixel(temp, lo, range, chartH);   // top of bar
            int baseY = yPixel(Math.max(lo, 0), lo, range, chartH); // rests on 0-line or bottom
            int barH  = Math.max(1, baseY - topY);

            // Fill
            Color fill = tempToColor(temp);
            g2.setColor(fill);
            g2.fillRect(bx, topY, barW, barH);

            // Outline
            g2.setColor(fill.darker());
            g2.setStroke(new BasicStroke(1f));
            g2.drawRect(bx, topY, barW, barH);

            // Temperature label above (or below for negative bars)
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            g2.setColor(new Color(50, 50, 50));
            String ts  = String.format("%.1f°", temp);
            FontMetrics fm9 = g2.getFontMetrics();
            int labelX = bx + (barW - fm9.stringWidth(ts)) / 2;
            int labelY = (temp >= 0) ? topY - 3 : baseY + fm9.getAscent() + 2;
            g2.drawString(ts, labelX, labelY);

            // City name — rotated −45° below x-axis
            Graphics2D gl = (Graphics2D) g2.create();
            gl.setFont(new Font("SansSerif", Font.PLAIN, 9));
            gl.setColor(new Color(60, 60, 60));
            gl.translate(bx + barW / 2, MT + chartH + 7);
            gl.rotate(-Math.PI / 4);
            gl.drawString(city.getName(), 0, 0);
            gl.dispose();
        }

        // ---- Axes (drawn last so they sit on top of gridlines) ---------------
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2f));
        // Y-axis
        g2.drawLine(ML, MT, ML, MT + chartH);
        // X-axis
        g2.drawLine(ML, MT + chartH, ML + chartW, MT + chartH);

        // Y-axis unit label (rotated)
        g2.setFont(new Font("SansSerif", Font.BOLD, 10));
        g2.setColor(new Color(60, 60, 60));
        Graphics2D gy = (Graphics2D) g2.create();
        gy.translate(12, MT + chartH / 2);
        gy.rotate(-Math.PI / 2);
        gy.drawString("Temperature (°C)", -40, 0);
        gy.dispose();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Converts a temperature value to a pixel y-coordinate inside the chart. */
    private int yPixel(double temp, double lo, double range, int chartH) {
        return MT + chartH - (int) ((temp - lo) / range * chartH);
    }

    /** Maps temperature to a bar colour. */
    private static Color tempToColor(double t) {
        if (t <  0)  return new Color(100, 149, 237);   // cold  — cornflower blue
        if (t < 15)  return new Color( 64, 186, 213);   // cool  — cyan
        if (t < 25)  return new Color(255, 165,   0);   // warm  — orange
        return            new Color(220,  60,  60);     // hot   — red
    }
}
