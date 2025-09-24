package org.paumard.server.travel.model.response;

import org.paumard.server.travel.model.weather.Weather;
import org.paumard.server.travel.model.weather.WeatherAgency;

public sealed interface WeatherResponse
    extends TravelComponent{

    record Ok(Weather weather) implements WeatherResponse {}
    record Error(WeatherAgency weatherAgency) implements WeatherResponse {}
}
