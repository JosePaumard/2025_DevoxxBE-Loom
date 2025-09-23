package org.paumard.server.travel.model.A_WeatherQuery;

import io.helidon.http.Status;
import io.helidon.webclient.api.WebClient;
import org.paumard.server.travel.model.city.City;
import org.paumard.server.travel.model.response.WeatherResponse;
import org.paumard.server.travel.model.util.ServerUtil;
import org.paumard.server.travel.model.weather.Weather;
import org.paumard.server.travel.model.weather.WeatherAgency;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

interface WeatherQueryBuilder
      extends Callable<WeatherResponse> {

    interface WeatherAgencySupplier extends Supplier<WeatherAgency> {
        default WeatherQueryBuilder forCity(City city) {
            return () -> {
                try (var response = WebClient.builder()
                      .baseUri(ServerUtil.WEATHER_SERVER_URI.get()).build()
                      .post("/weather/" + get().tag())
                      .submit(city);) {
                    if (response.status() == Status.OK_200) {
                        var weather = response.as(Weather.class);
                        return new WeatherResponse.Ok(weather);
                    } else {
                        return new WeatherResponse.Error(get());
                    }
                }
            };
        }
    }

    static WeatherAgencySupplier from(WeatherAgency weatherAgency) {
        return () -> weatherAgency;
    }
}
