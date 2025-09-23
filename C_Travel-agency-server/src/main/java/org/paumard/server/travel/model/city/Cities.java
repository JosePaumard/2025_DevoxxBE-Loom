package org.paumard.server.travel.model.city;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public record Cities(Map<String, City> cityByName) {

    public static Cities read() {
        return readCitiesFrom("us-cities.txt");
    }

    public static Cities readCitiesFrom(String fileName) {
        try (var lines = Files.lines(Path.of("files", fileName));) {

            var cities = lines
                  .filter(line -> !line.startsWith("#"))
                  .filter(line -> !line.isEmpty())
                  .map(Cities::ofCity).toList();
            var cityByName = cities.stream()
                  .collect(Collectors.collectingAndThen(
                        Collectors.toMap(City::name, Function.identity()),
                        Collections::unmodifiableMap)
                  );
            return new Cities(cityByName);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static City ofCity(String line) {
        line = line.trim();
        var indexOfFirstSpace = line.indexOf(' ');
        var name = line.substring(indexOfFirstSpace + 1);
        return new City(name);
    }

    public City byName(String cityName) {
        return cityByName.get(cityName);
    }
}