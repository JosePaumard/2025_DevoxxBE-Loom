package org.paumard.server.company.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DirectFlightParser {
  public static Map<Flight.Direct, Integer> parseFlights(Path path, List<City> cities) throws IOException {
    var cityById = cities.stream()
        .collect(Collectors.toMap(City::id, Function.identity()));

    try (var lines = Files.lines(path);) {
      var pricePerFlight = new HashMap<Flight.Direct, Integer>();
      var table = lines.skip(2).toList();
      var fromId = 1;
      for (var line : table) {
        var elements = line.trim().split(" ");
        var prices = Arrays.stream(elements)
            .skip(1)
            .filter(Predicate.not(String::isBlank))
            .toList();
        var toId = 1;
        while (toId < prices.size()) {
          if (!prices.get(toId - 1).equals("-")) {
            var price = Integer.parseInt(prices.get(toId - 1));
            var from = cityById.get(fromId);
            var to = cityById.get(toId);
            pricePerFlight.put(new Flight.Direct(from, to), price);
            pricePerFlight.put(new Flight.Direct(to, from), price);
          }
          toId++;
        }
        fromId++;
      }
      return pricePerFlight;
    }
  }
}
