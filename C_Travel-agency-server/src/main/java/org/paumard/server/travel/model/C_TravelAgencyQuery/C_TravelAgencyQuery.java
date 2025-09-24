package org.paumard.server.travel.model.C_TravelAgencyQuery;

import org.paumard.server.travel.model.A_WeatherQuery.A_WeatherQuery;
import org.paumard.server.travel.model.B_CompanyQuery.B_CompanyQuery;
import org.paumard.server.travel.model.C_TravelAgencyQuery.model.Travel;
import org.paumard.server.travel.model.city.Cities;
import org.paumard.server.travel.model.city.City;
import org.paumard.server.travel.model.response.CompanyFlightPrice;
import org.paumard.server.travel.model.response.TravelComponent;
import org.paumard.server.travel.model.response.WeatherResponse;
import org.paumard.server.travel.model.weather.Weather;

import java.util.concurrent.Callable;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.function.Predicate;

public class C_TravelAgencyQuery {

    public static ScopedValue<String> LICENCE_KEY = ScopedValue.newInstance();

    void main() throws InterruptedException {

        var cities = Cities.read();

        var atlanta = cities.byName("Atlanta");
        var chicago = cities.byName("Chicago");

        var phoenix = cities.byName("Phoenix");
        var philadelphia = cities.byName("Philadelphia");

        var travel =
              ScopedValue.where(LICENCE_KEY, "Valid key")
                    .call(() -> queryTravel(phoenix, philadelphia));

        IO.println(travel);
    }

    public static Travel queryTravel(City from, City to) throws InterruptedException {
        Callable<CompanyFlightPrice> companyFlightPriceTask =
              () -> B_CompanyQuery.queryFlightPrice(from, to);

        Callable<WeatherResponse> weatherResponseTask =
              () -> A_WeatherQuery.queryWeatherForecastFor(to);

        var predicate = new Predicate<Subtask<? extends TravelComponent>>() {

            volatile WeatherResponse weatherResponse;
            volatile CompanyFlightPrice companyFlightPrice;

            public boolean test(Subtask<? extends TravelComponent> travelComponent) {
                return switch (travelComponent.state()) {
                    case UNAVAILABLE, FAILED -> throw new IllegalStateException("Ooops!");
                    case SUCCESS -> {
                        switch (travelComponent.get()) {
                            case CompanyFlightPrice companyFlightPrice -> {
                                this.companyFlightPrice = companyFlightPrice;
                                yield true;
                            }
                            case WeatherResponse weatherResponse -> {
                                this.weatherResponse = weatherResponse;
                                yield false;
                            }
                        }
                    }
                };
            }
        };

        try (var scope = StructuredTaskScope.open(
              Joiner.allUntil(predicate)
        )) {

            scope.fork(companyFlightPriceTask);
            scope.fork(weatherResponseTask);

            var stream = scope.join();

            var companyFlightPrice = predicate.companyFlightPrice;
            var weatherResonse = predicate.weatherResponse;

            if (weatherResonse instanceof WeatherResponse.Ok(Weather weather)) {
                return new Travel.TravelWithWeather(
                      companyFlightPrice.company(), companyFlightPrice.flight(), companyFlightPrice.price(),
                      weather);
            } else {
                return new Travel.TravelNoWeather(
                      companyFlightPrice.company(), companyFlightPrice.flight(), companyFlightPrice.price());
            }
        }
    }
}
