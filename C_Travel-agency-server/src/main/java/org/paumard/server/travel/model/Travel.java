package org.paumard.server.travel.model;

import java.util.Objects;
import java.util.Optional;

public record Travel(String companyName, Flight flight, int price, Optional<Weather> weather) {
  public Travel {
    Objects.requireNonNull(companyName);
    Objects.requireNonNull(flight);
    Objects.requireNonNull(weather);
  }
}
