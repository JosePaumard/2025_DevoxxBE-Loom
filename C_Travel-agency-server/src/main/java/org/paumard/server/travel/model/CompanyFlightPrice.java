package org.paumard.server.travel.model;

import java.util.Objects;

public record CompanyFlightPrice(String companyName, Flight flight, int price) {
  public CompanyFlightPrice {
    Objects.requireNonNull(companyName);
    Objects.requireNonNull(flight);
  }
}