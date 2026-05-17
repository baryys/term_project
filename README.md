# Travel Planner System

A desktop application built with plain Java SE and Swing as a Software Design Patterns term project. No external libraries — only the standard Java class library. Charts are drawn manually with `Graphics2D`.

## Design Patterns

| Pattern | Where it is used |
|---------|-----------------|
| **Singleton** | `CityRepository` — cities are loaded from JSON once and shared across the app |
| **Strategy** | `CitySortStrategy` — swap sort order (name, population, area) at runtime |
| **Iterator** | `WeatherCityIterator` — filter the city list by weather state without exposing the collection |
| **Observer** | `WeatherReportProvider` — background thread updates temperatures every 3 s and notifies all UI panels |
| **Decorator** | `CityActivity` chain — attach activities (museum, park, shopping, city centre) to a trip and accumulate cost and hours |

## Features

- Live temperature and weather simulation (updates every 3 seconds)
- Sort cities by name, population, or area
- Filter cities by weather state (Sunny / Cloudy / Rainy / Snowy)
- Activity planner with running cost and hours total
- Temperature bar chart and weather distribution pie chart — both repaint on every live update
- 15 pre-loaded cities across four continents

## Project Structure

```
term_project/
├── src/com/travelplanner/
│   ├── Main.java
│   ├── model/          City, WeatherState
│   ├── repository/     CityRepository  (Singleton)
│   ├── util/           SimpleCityJsonParser
│   ├── strategy/       CitySortStrategy, CitySorter, *SortStrategy
│   ├── iterator/       WeatherCityIterator, Abstract + 4 concrete iterators
│   ├── observer/       WeatherObserver, WeatherSubject, WeatherReportProvider
│   ├── decorator/      CityActivity, BaseCityActivity, CityActivityDecorator, 4 decorators
│   └── ui/             MainFrame, ControlPanel, AllCitiesPanel, WeatherFilterPanel,
│                       PlannerPanel, TemperatureBarChartPanel, WeatherPieChartPanel
├── resources/
│   └── cities.json
├── manifest.txt
├── build.sh            (Linux / macOS)
├── build.bat           (Windows)
└── run.sh              (Linux — required on Wayland)
```

## Prerequisites

| | Requirement |
|-|-------------|
| **Java** | JDK 8 or later (`javac` and `java` must be on `PATH`) |
| **OS** | Arch Linux, Ubuntu 20.04+, or Windows 10/11 |

Verify your Java installation:

```
java -version
javac -version
```

---

## Build

### Arch Linux

Install the JDK if needed:

```bash
sudo pacman -S jdk-openjdk
```

Build:

```bash
bash build.sh
```

### Ubuntu

Install the JDK if needed:

```bash
sudo apt update
sudo apt install default-jdk
```

Build:

```bash
bash build.sh
```

To do a clean build (wipes `out/` and the old JAR first):

```bash
bash build.sh clean
```

### Windows

Install the JDK from <https://adoptium.net> and make sure `javac` is on your `PATH` (the installer offers a checkbox for this).

Open **Command Prompt** or **PowerShell** in the project folder, then run:

```bat
build.bat
```
(If cmd gives a cmdlet error, double rigt click the build.bat file in file explorer)
A `build.bat` script is provided in the project root. It does the same four steps as `build.sh`: prepare the output directory, compile all sources, copy resources, and package the JAR.

---

## Run

### Arch Linux (X11 or Wayland)

If you are on a **Wayland compositor** (Niri, Sway, Hyprland, i3 under XWayland, etc.) use the provided wrapper script — it sets the environment variables that Java needs to paint correctly through the XWayland bridge:

```bash
bash run.sh
```

If you are on a plain **X11** session you can also launch the JAR directly:

```bash
java -jar TravelPlanner.jar
```

### Ubuntu (X11 or Wayland)

Ubuntu's default GNOME session runs under Mutter/XWayland. Use the wrapper:

```bash
bash run.sh
```

Or directly on X11:

```bash
java -jar TravelPlanner.jar
```

### Windows

```bat
java -jar TravelPlanner.jar
```

Or double-click `TravelPlanner.jar` if your JDK installation associated `.jar` files with `java.exe`.

---

## Wayland note

On tiling Wayland compositors Java's AWT assumes a reparenting window manager and paints into a buffer the compositor never reads, producing a blank white window. `run.sh` fixes this with two settings:

- `_JAVA_AWT_WM_NONREPARENTING=1` — tells AWT that windows are not reparented (must be set before the JVM starts, so it lives in the shell script).
- `-Dsun.java2d.xrender=false` — disables the XRender pipeline which is broken on XWayland.

These settings have no effect on Windows or plain X11 and are safe to use everywhere.

---

## Data

City data is loaded from `resources/cities.json` (bundled inside the JAR at build time). Each entry has a name, population, area, starting temperature, and starting weather state. The live simulation mutates temperature by up to ±2 °C per tick and randomly changes weather state with a 1-in-5 chance.
