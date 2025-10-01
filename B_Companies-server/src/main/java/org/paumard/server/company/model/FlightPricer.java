package org.paumard.server.company.model;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

public final class FlightPricer {
  private FlightPricer() {
  }

  public static OptionalInt price(Company company, Flight flight, Map<Flight.Direct, Integer> priceMap, int delta) {
    Objects.requireNonNull(company);
    Objects.requireNonNull(flight);
    Objects.requireNonNull(priceMap);

    var pricingStrategy = company.pricingStrategy();
    var servedCities = company.servedCities();

    return switch (flight) {
      case Flight.Direct(City from, City to) -> {
        if (!servedCities.contains(from) || !servedCities.contains(to)) {
          yield OptionalInt.empty();
        }
        var flightPrice = priceMap.get(flight);
        if (flightPrice == null) {
          yield OptionalInt.empty();
        }
        yield OptionalInt.of((flightPrice * (pricingStrategy + delta)) / 100);
      }

      case Flight.Multileg(City from, City via, City to) -> {
        if (!servedCities.contains(from) || !servedCities.contains(via) || !servedCities.contains(to)) {
          yield OptionalInt.empty();
        }
        var firstLegPrice = priceMap.get(new Flight.Direct(from, via));
        var secondLegPrice = priceMap.get(new Flight.Direct(via, to));
        if (firstLegPrice == null || secondLegPrice == null) {
          yield OptionalInt.empty();
        }
        yield OptionalInt.of((firstLegPrice + secondLegPrice) * (pricingStrategy + delta) / 125);
      }
    };
  }
}
