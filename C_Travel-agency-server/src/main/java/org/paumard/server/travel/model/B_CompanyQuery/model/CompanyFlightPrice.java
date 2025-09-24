package org.paumard.server.travel.model.B_CompanyQuery.model;

import org.paumard.server.travel.model.company.Company;
import org.paumard.server.travel.model.flight.Flight;

public record CompanyFlightPrice(
      Company company,
      Flight flight, int price) {
}
