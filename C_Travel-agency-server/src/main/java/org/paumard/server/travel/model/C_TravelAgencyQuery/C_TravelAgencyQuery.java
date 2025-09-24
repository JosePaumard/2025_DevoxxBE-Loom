package org.paumard.server.travel.model.C_TravelAgencyQuery;

import org.paumard.server.travel.model.A_WeatherQuery.A_WeatherQuery;
import org.paumard.server.travel.model.B_CompanyQuery.B_CompanyQuery;
import org.paumard.server.travel.model.B_CompanyQuery.model.CompanyFlightPrice;
import org.paumard.server.travel.model.C_TravelAgencyQuery.model.Travel;
import org.paumard.server.travel.model.city.Cities;
import org.paumard.server.travel.model.city.City;
import org.paumard.server.travel.model.response.WeatherResponse;
import org.paumard.server.travel.model.weather.Weather;

import java.util.concurrent.Callable;
import java.util.concurrent.StructuredTaskScope;

public class C_TravelAgencyQuery {

    void main() throws InterruptedException {

        var cities = Cities.read();

        var atlanta = cities.byName("Atlanta");
        var chicago = cities.byName("Chicago");

        var phoenix = cities.byName("Phoenix");
        var philadelphia = cities.byName("Philadelphia");

        var travel = queryTravel(phoenix, philadelphia);
        IO.println(travel);
    }

    public static Travel queryTravel(City from, City to) throws InterruptedException {
        Callable<CompanyFlightPrice> companyFlightPriceTask =
              () -> B_CompanyQuery.queryFlightPrice(from, to);

        Callable<WeatherResponse> weatherResponseTask =
              () -> A_WeatherQuery.queryWeatherForecastFor(to);

        try (var scope = StructuredTaskScope.open()) {

            var companyFlightPriceSubTask =  scope.fork(companyFlightPriceTask);
            var weatherResponseSubTask = scope.fork(weatherResponseTask);

            scope.join();

            var companyFlightPrice = companyFlightPriceSubTask.get();
            var weatherResonse = weatherResponseSubTask.get();

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
