package org.paumard.server.travel.model.dto;

import org.paumard.server.travel.model.response.CompanyResponse;
import org.paumard.server.travel.model.weather.Weather;

import java.util.Objects;

public record PricedTravelWithWeatherDTO(CompanyResponse.Priced travel, Weather weather) {

    public PricedTravelWithWeatherDTO {
        Objects.requireNonNull(travel);
        Objects.requireNonNull(weather);
    }
}
