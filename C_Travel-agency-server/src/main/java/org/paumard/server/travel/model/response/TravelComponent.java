package org.paumard.server.travel.model.response;

public sealed interface TravelComponent
permits CompanyFlightPrice, WeatherResponse {
}
