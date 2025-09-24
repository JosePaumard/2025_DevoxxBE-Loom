package org.paumard.server.travel.model.A_WeatherQuery;


import org.paumard.server.travel.model.city.Cities;
import org.paumard.server.travel.model.city.City;
import org.paumard.server.travel.model.response.WeatherResponse;
import org.paumard.server.travel.model.weather.WeatherAgencies;

import java.util.concurrent.Callable;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

public class A_WeatherQuery {

    void main() throws Exception {
        var cities = Cities.read();

        var atlanta = cities.byName("Atlanta");

        var weatherForecast = queryWeatherForecastFor(atlanta);
        IO.println(weatherForecast);
    }

    public static WeatherResponse queryWeatherForecastFor(City city) throws InterruptedException {

        var agencies = WeatherAgencies.read();

        var globalWeather = agencies.first();
        var starWeather = agencies.get(1);
        var planetWeather = agencies.get(2);

        Callable<WeatherResponse> queryGlobalWeather =
              WeatherQueryBuilder.from(globalWeather).forCity(city);
        Callable<WeatherResponse> queryStarWeather =
              WeatherQueryBuilder.from(starWeather).forCity(city);
        Callable<WeatherResponse> queryPlanetWeather =
              WeatherQueryBuilder.from(planetWeather).forCity(city);
        Callable<WeatherResponse> failingQuery =
              () -> {
                  Thread.sleep(10);
                  throw new RuntimeException("Failing query");
              };

        try (var scope = StructuredTaskScope.open(
              Joiner.<WeatherResponse>anySuccessfulResultOrThrow()
        )) {

            scope.fork(queryGlobalWeather);
            scope.fork(queryStarWeather);
            scope.fork(queryPlanetWeather);
            scope.fork(failingQuery);

            var result = scope.join();

            return result;
        }
    }
}
