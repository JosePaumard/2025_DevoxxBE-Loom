package org.paumard.server.travel.model.flight.travel;

import org.paumard.server.travel.model.city.Cities;

import java.util.Objects;

public record MultiLegFlight(Flight flight1, Flight flight2) implements Travel {

    public MultiLegFlight {
        Objects.requireNonNull(flight1);
        Objects.requireNonNull(flight2);
        if (!flight1.to().equals(flight2.from())) {
            throw new IllegalArgumentException("Flights need to connect");
        }
    }
}
