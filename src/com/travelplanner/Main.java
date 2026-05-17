package com.travelplanner;

import com.travelplanner.ui.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    static {
        // Disable the XRender Java2D pipeline.
        // On XWayland (Niri, Sway, Hyprland, i3, etc.) XRender paints into a
        // stale buffer that is never composited, producing a blank white window.
        // This must be set before any AWT class is loaded, so a static block
        // is the right place — main() is already too late on some JDK versions.
        System.setProperty("sun.java2d.xrender", "false");
    }

    public static void main(String[] args) {
        // Avoid GTK L&F: it loads native GTK libraries that can further
        // destabilise rendering on Wayland. The cross-platform (Metal) L&F
        // is pure Java and works correctly everywhere.
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(MainFrame::new);
    }
}
