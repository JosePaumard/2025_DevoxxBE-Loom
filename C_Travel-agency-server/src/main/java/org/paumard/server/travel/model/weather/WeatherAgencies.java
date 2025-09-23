package org.paumard.server.travel.model.weather;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public record WeatherAgencies(List<WeatherAgency> weatherAgencies) {

    public WeatherAgency first() {
        return this.weatherAgencies.getFirst();
    }

    public static WeatherAgencies read() {
        return readWeatherAgenciesFrom("weather-agencies.txt");
    }

    public static WeatherAgencies readWeatherAgenciesFrom(String fileName) {
        Path agencies = Path.of("files", fileName);
        try (var lines = Files.lines(agencies)) {
            var weatherAgencies = lines.filter(line -> !line.startsWith("#"))
                  .map(WeatherAgency::of)
                  .toList();
            return new WeatherAgencies(weatherAgencies);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public WeatherAgency get(int index) {
        return this.weatherAgencies.get(index);
    }
}
