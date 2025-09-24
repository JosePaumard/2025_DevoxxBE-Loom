package org.paumard.server.travel.model.response;

import org.paumard.server.travel.model.company.Company;
import org.paumard.server.travel.model.flight.Flight;

public record CompanyFlightPrice(
      Company company, Flight flight, int price)
      implements TravelComponent {
}
