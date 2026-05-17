#!/usr/bin/env bash
# Launch TravelPlanner on any Linux desktop, including Wayland tiling compositors
# (Niri, Sway, Hyprland, i3, …).
#
# _JAVA_AWT_WM_NONREPARENTING=1
#   Tells Java's AWT that the window manager does not reparent windows.
#   Without this, Java paints into a buffer that the compositor never reads,
#   resulting in a permanent blank/white window.
#   This is an environment variable — it cannot be set from inside the JVM.
#
# sun.java2d.xrender=false  (also set in Main.java's static block as a belt-
#   and-suspenders measure, but -D here ensures it is always applied first.)

export _JAVA_AWT_WM_NONREPARENTING=1

exec java \
  -Dsun.java2d.xrender=false \
  -jar "$(dirname "$0")/TravelPlanner.jar" "$@"
