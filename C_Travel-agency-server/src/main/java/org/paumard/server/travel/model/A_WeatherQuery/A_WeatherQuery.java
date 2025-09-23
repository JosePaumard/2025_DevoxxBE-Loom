package org.paumard.server.travel.model.A_WeatherQuery;


import org.paumard.server.travel.model.city.Cities;
import org.paumard.server.travel.model.response.WeatherResponse;
import org.paumard.server.travel.model.weather.WeatherAgencies;

import java.util.concurrent.Callable;
import java.util.concurrent.StructuredTaskScope;
import java.util.stream.Stream;

public class A_WeatherQuery {

    void main() throws Exception {
        var agencies = WeatherAgencies.read();
        var cities = Cities.read();

        var globalWeather = agencies.first();
        var starWeather = agencies.get(1);
        var planetWeather = agencies.get(2);
        var atlanta = cities.byName("Atlanta");

        Callable<WeatherResponse> queryGlobalWeather =
              WeatherQueryBuilder.from(globalWeather).forCity(atlanta);
        Callable<WeatherResponse> queryStarWeather =
              WeatherQueryBuilder.from(starWeather).forCity(atlanta);
        Callable<WeatherResponse> queryPlanetWeather =
              WeatherQueryBuilder.from(planetWeather).forCity(atlanta);
        Callable<WeatherResponse> failingQuery =
              () -> {
                  Thread.sleep(800);
                  throw new RuntimeException("Failing query");
              };

        try (var scope = StructuredTaskScope.open()) {

            var subTask1 = scope.fork(queryGlobalWeather);
            var subTask2 = scope.fork(queryStarWeather);
            var subTask3 = scope.fork(queryPlanetWeather);

            scope.join();

            Stream.of(subTask1, subTask2, subTask3)
                  .forEach(subTask -> {
                      IO.println(subTask.state());
                      if (subTask.state() == StructuredTaskScope.Subtask.State.SUCCESS) {
                          IO.println(subTask.get());
                      }
                  });
        }
    }
}
