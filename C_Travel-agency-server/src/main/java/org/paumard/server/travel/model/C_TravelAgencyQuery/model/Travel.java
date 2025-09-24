package org.paumard.server.travel.model.C_TravelAgencyQuery.model;

import org.paumard.server.travel.model.company.Company;
import org.paumard.server.travel.model.flight.Flight;
import org.paumard.server.travel.model.weather.Weather;

public sealed interface Travel {

    record TravelWithWeather(Company company, Flight flight, int price, Weather weather)
          implements Travel {
    }

    record TravelNoWeather(Company company, Flight flight, int price)
          implements Travel {
    }
}
