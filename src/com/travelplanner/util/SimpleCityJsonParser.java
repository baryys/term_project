package com.travelplanner.util;

import com.travelplanner.model.City;
import com.travelplanner.model.WeatherState;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class SimpleCityJsonParser {

    private SimpleCityJsonParser() {}

    /**
     * Parses a flat JSON array of city objects from the given path.
     * The path is resolved first against the classpath, then the file system.
     */
    public static List<City> parse(String path) throws IOException {
        String json = readContent(path);
        List<City> cities = new ArrayList<>();
        for (String block : extractObjectBlocks(json)) {
            try {
                cities.add(parseCity(block));
            } catch (Exception e) {
                throw new IOException("Failed to parse city entry: " + e.getMessage(), e);
            }
        }
        return cities;
    }

    // -------------------------------------------------------------------------
    // I/O
    // -------------------------------------------------------------------------

    private static String readContent(String path) throws IOException {
        // 1. Classpath — works when running from a JAR or when the IDE has
        //    copied resources into the output directory.
        InputStream is = SimpleCityJsonParser.class.getClassLoader()
                .getResourceAsStream(path);

        // 2. resources/<path> relative to the working directory — works when
        //    running directly from IntelliJ / VS Code whose classpath points
        //    only at compiled classes and has not copied the resources folder.
        if (is == null) {
            File resourcesFile = new File("resources", path);
            if (resourcesFile.isFile()) {
                is = new FileInputStream(resourcesFile);
            }
        }

        // 3. Bare file path — last resort (e.g. absolute path passed by caller).
        if (is == null) {
            File direct = new File(path);
            if (direct.isFile()) {
                is = new FileInputStream(direct);
            }
        }

        if (is == null) {
            throw new FileNotFoundException(
                    "Cannot find '" + path + "'. Searched: classpath, " +
                    "resources/" + path + ", ./" + path);
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[4096];
            int n;
            while ((n = reader.read(buf)) != -1) {
                sb.append(buf, 0, n);
            }
            return sb.toString();
        }
    }

    // -------------------------------------------------------------------------
    // Structural parsing — extract top-level { } blocks from a JSON array
    // -------------------------------------------------------------------------

    private static List<String> extractObjectBlocks(String json) {
        List<String> blocks = new ArrayList<>();
        int i = 0;
        int len = json.length();

        while (i < len) {
            // Advance to the next opening brace
            while (i < len && json.charAt(i) != '{') i++;
            if (i >= len) break;

            int start = i;
            int depth = 0;

            while (i < len) {
                char c = json.charAt(i);
                if (c == '"') {
                    // Skip over a JSON string (handles \" escapes)
                    i++;
                    while (i < len) {
                        char s = json.charAt(i);
                        if (s == '\\') {
                            i++; // skip the escaped character
                        } else if (s == '"') {
                            break;
                        }
                        i++;
                    }
                } else if (c == '{') {
                    depth++;
                } else if (c == '}') {
                    if (--depth == 0) {
                        blocks.add(json.substring(start, i + 1));
                        i++;
                        break;
                    }
                }
                i++;
            }
        }
        return blocks;
    }

    // -------------------------------------------------------------------------
    // Field-level parsing
    // -------------------------------------------------------------------------

    private static City parseCity(String block) {
        String name         = readStringField(block, "name");
        int    population   = Integer.parseInt(readValueField(block, "population"));
        double area         = Double.parseDouble(readValueField(block, "area"));
        double temperature  = Double.parseDouble(readValueField(block, "currentTemperature"));
        WeatherState weather = WeatherState.valueOf(
                readStringField(block, "currentWeatherState").toUpperCase());
        return new City(name, population, area, temperature, weather);
    }

    /**
     * Returns the string value (without quotes) of a JSON string field.
     * Handles basic escape sequences: \n \t \r \" \\.
     */
    private static String readStringField(String json, String key) {
        int afterKey = requireKey(json, key);
        int colon    = json.indexOf(':', afterKey);
        int i        = skipWhitespace(json, colon + 1);

        if (i >= json.length() || json.charAt(i) != '"') {
            throw new IllegalArgumentException(
                    "Expected a quoted string value for key: " + key);
        }
        i++; // skip opening quote

        StringBuilder sb = new StringBuilder();
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                i++;
                sb.append(unescape(json.charAt(i)));
            } else if (c == '"') {
                break;
            } else {
                sb.append(c);
            }
            i++;
        }
        return sb.toString();
    }

    /**
     * Returns the raw token of a non-string JSON field (number, boolean, null).
     */
    private static String readValueField(String json, String key) {
        int afterKey = requireKey(json, key);
        int colon    = json.indexOf(':', afterKey);
        int i        = skipWhitespace(json, colon + 1);

        int end = i;
        while (end < json.length()) {
            char c = json.charAt(end);
            if (c == ',' || c == '}' || Character.isWhitespace(c)) break;
            end++;
        }
        String raw = json.substring(i, end).trim();
        if (raw.isEmpty()) {
            throw new IllegalArgumentException("Missing value for key: " + key);
        }
        return raw;
    }

    /**
     * Finds "key" in json and returns the index immediately after its closing quote.
     * Throws if the key is absent.
     */
    private static int requireKey(String json, String key) {
        String token = "\"" + key + "\"";
        int idx = json.indexOf(token);
        if (idx == -1) {
            throw new IllegalArgumentException("Key not found in JSON object: " + key);
        }
        return idx + token.length();
    }

    private static int skipWhitespace(String json, int i) {
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) i++;
        return i;
    }

    private static char unescape(char c) {
        switch (c) {
            case 'n':  return '\n';
            case 't':  return '\t';
            case 'r':  return '\r';
            case '"':  return '"';
            case '\\': return '\\';
            default:   return c;
        }
    }
}
