package org.paumard.server.travel.model;

import java.util.Objects;

public record CompanyFlightPrice(Company company, Flight flight, int price) {
  public CompanyFlightPrice {
    Objects.requireNonNull(company);
    Objects.requireNonNull(flight);
  }
}