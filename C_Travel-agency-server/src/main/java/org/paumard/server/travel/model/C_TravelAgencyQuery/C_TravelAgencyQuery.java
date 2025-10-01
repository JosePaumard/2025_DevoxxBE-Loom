package org.paumard.server.travel.model.C_TravelAgencyQuery;

import org.paumard.server.travel.model.A_WeatherQuery.A_WeatherQuery;
import org.paumard.server.travel.model.B_CompanyQuery.B_CompanyQuery;
import org.paumard.server.travel.model.City;
import org.paumard.server.travel.model.Company;
import org.paumard.server.travel.model.CompanyFlightPrice;
import org.paumard.server.travel.model.Parser;
import org.paumard.server.travel.model.Travel;
import org.paumard.server.travel.model.Weather;
import org.paumard.server.travel.model.WeatherAgency;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;
import java.util.concurrent.StructuredTaskScope.Subtask.State;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class C_TravelAgencyQuery {

    private static final Random RANDOM = new Random();

    private static final ScopedValue<String> LICENCE_KEY = ScopedValue.newInstance();

    public static Optional<Travel> travelQuery(List<WeatherAgency> agencies, List<Company> companies, City from, City to) throws InterruptedException {
        try (var scope = StructuredTaskScope.open(
            Joiner.allUntil(subtask -> switch (subtask.state()) {
                case UNAVAILABLE, FAILED -> false;
                case SUCCESS -> subtask.get() instanceof Optional<?> optional &&
                    optional.isPresent() &&
                    optional.orElseThrow() instanceof CompanyFlightPrice;
            }))) {

            var weatherTask = scope.fork(() -> A_WeatherQuery.queryWeatherForecastFor(agencies, to));
            var flightPriceTask = scope.fork(() -> B_CompanyQuery.queryBestFlightPrice(companies, from, to));

            scope.join();

            var weatherOpt =
                weatherTask.state() == State.SUCCESS ? weatherTask.get() : Optional.<Weather>empty();
            return flightPriceTask.get()
                .map(flightPrice ->
                    new Travel(flightPrice.company().name(), flightPrice.flight(), flightPrice.price(), weatherOpt));
        }
    }

    static void main() throws InterruptedException, IOException {
        var cities = Parser.parse(Path.of("files", "us-cities.txt"), City::parseLine);
        var cityByName = cities.stream().collect(toMap(City::name, identity()));
        var agencies = Parser.parse(Path.of("files", "weather-agencies.txt"), WeatherAgency::parseLine);
        var companies = Parser.parse(Path.of("files", "companies.txt"),
            line -> Company.parseLine(line, cities, flightAvailabilityRate -> RANDOM.nextInt(0, 100) <= flightAvailabilityRate));

        var atlanta = cityByName.get("Atlanta");
        var chicago = cityByName.get("Chicago");

        var phoenix = cityByName.get("Phoenix");
        var philadelphia = cityByName.get("Philadelphia");

        IO.println(travelQuery(agencies, companies, atlanta, chicago));
        //IO.println(travelQuery(agencies, companies, phoenix, philadelphia));
    }
}
