package org.paumard.server.travel.model;

import java.util.Optional;

public record Travel(Company company, Flight flight, int price, Optional<Weather> weather) {
}
