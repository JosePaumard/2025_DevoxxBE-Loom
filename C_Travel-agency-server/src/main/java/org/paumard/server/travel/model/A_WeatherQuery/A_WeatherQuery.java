package org.paumard.server.travel.model.A_WeatherQuery;


import io.helidon.http.Status;
import io.helidon.webclient.api.WebClient;
import org.paumard.server.travel.model.City;
import org.paumard.server.travel.model.Parser;
import org.paumard.server.travel.model.Weather;
import org.paumard.server.travel.model.WeatherAgency;
import org.paumard.server.travel.Client;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class A_WeatherQuery {

    private static Optional<Weather> weatherQuery(WeatherAgency agency, City city) {
        try (var response = WebClient.builder()
            .baseUri(Client.getWeatherServerURI())
            .build()
            .post("/weather/" + agency.tag())
            .submit(city)) {
            if (response.status() == Status.OK_200) {
                var weather = response.as(Weather.class);
                return Optional.of(weather);
            }
            return Optional.empty();
        }
    }

    public static Optional<Weather> queryWeatherForecastFor(List<WeatherAgency> agencies, City city) throws InterruptedException {
        try (var scope = StructuredTaskScope.open(
            Joiner.<Optional<Weather>>anySuccessfulResultOrThrow())) {

            for(WeatherAgency agency : agencies) {
                scope.fork(() -> weatherQuery(agency, city));
            }

            /*scope.fork(() -> {
                Thread.sleep(10);
                throw new RuntimeException("Failing query");
            });*/

            return scope.join();
        }
    }

    static void main() throws InterruptedException, IOException {
        var cities = Parser.parse(Path.of("files", "us-cities.txt"), City::parseLine);
        var cityByName = cities.stream().collect(toMap(City::name, identity()));
        var agencies = Parser.parse(Path.of("files", "weather-agencies.txt"), WeatherAgency::parseLine);

        var city = cityByName.get("Atlanta");

        var globalWeather = agencies.get(0);
        var starWeather = agencies.get(1);
        var planetWeather = agencies.get(2);
        var trustedAgencies = List.of(globalWeather, starWeather, planetWeather);

        var weatherOpt = queryWeatherForecastFor(trustedAgencies, city);
        IO.println(weatherOpt.orElseThrow());
    }
}
