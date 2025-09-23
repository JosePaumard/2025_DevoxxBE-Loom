package org.paumard.server.travel.model.A_WeatherQuery;


import org.paumard.server.travel.model.city.Cities;
import org.paumard.server.travel.model.response.WeatherResponse;
import org.paumard.server.travel.model.weather.WeatherAgencies;

import java.util.List;
import java.util.concurrent.Callable;

public class A_WeatherQuery {

    void main() throws Exception {
        var agencies = WeatherAgencies.read();
        var cities = Cities.read();

        var globalWeather = agencies.first();
        var starWeather = agencies.get(1);
        var planetWeather = agencies.get(2);
        var atlanta = cities.byName("Atlanta");

        var queriedAgencies =
              List.of(globalWeather, starWeather, planetWeather);

        Callable<WeatherResponse> queryGlobalWeather =
              WeatherQueryBuilder.from(globalWeather).forCity(atlanta);

        var weatherResponse = queryGlobalWeather.call();
        IO.println(weatherResponse);
    }
}
