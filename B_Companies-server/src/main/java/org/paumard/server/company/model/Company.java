package org.paumard.server.company.model;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public record Company(String name, String tag, int pricingStrategy, Set<City> servedCities,
                      int average, int dispersion) {

  public Company {
    Objects.requireNonNull(name);
    Objects.requireNonNull(tag);
    Objects.requireNonNull(servedCities);
    if (pricingStrategy < 0) {
      throw new IllegalArgumentException("Price strategy should be greater than 0");
    }
    if (servedCities.isEmpty()) {
      throw new IllegalArgumentException("The company " + name + " has no served cities");
    }
    servedCities = Set.copyOf(servedCities);
  }

  public static Company parseLine(String line, List<City> cities, IntPredicate availabilityFilter) {
    Pattern pattern = Pattern.compile("""
        (?<name>[ a-zA-Z]+) \
        (?<pricingStrategy>\\d+) \
        (?<flightAvailabilityRate>\\d+) \
        (?<tag>[a-z\\-]+) \
        (?<average>\\d+) \
        (?<dispersion>\\d+)$""");

    var matcher = pattern.matcher(line);
    if (!matcher.matches()) {
      throw new IllegalStateException("Line [" + line + "] does not match");
    }
    var name = matcher.group("name");
    var pricingStrategy = Integer.parseInt(matcher.group("pricingStrategy"));
    var flightAvailabilityRate = Integer.parseInt(matcher.group("flightAvailabilityRate"));
    var tag = matcher.group("tag");
    var average = Integer.parseInt(matcher.group("average"));
    var dispersion = Integer.parseInt(matcher.group("dispersion"));
    var servedCities = cities.stream()
        .filter(_ -> availabilityFilter.test(flightAvailabilityRate))
        .collect(Collectors.toSet());
    return new Company(name, tag, pricingStrategy, servedCities, average, dispersion);
  }
}
