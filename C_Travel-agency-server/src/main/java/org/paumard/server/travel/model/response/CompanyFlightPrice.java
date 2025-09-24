package org.paumard.server.travel.model.response;

import org.paumard.server.travel.model.company.Company;
import org.paumard.server.travel.model.flight.Flight;

import static org.paumard.server.travel.model.C_TravelAgencyQuery.C_TravelAgencyQuery.LICENCE_KEY;

public record CompanyFlightPrice(
      Company company, Flight flight, int price)
      implements TravelComponent {

    public CompanyFlightPrice {
        if (LICENCE_KEY.isBound() && LICENCE_KEY.get().equals("Valid key")) {
            IO.println("Valid key read for LICENCE_KEY");
        } else {
            throw new IllegalStateException("LICENCE_KEY is not set or invalid");
        }
    }

}
