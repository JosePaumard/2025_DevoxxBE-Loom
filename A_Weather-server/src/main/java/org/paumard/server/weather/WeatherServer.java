package org.paumard.server.weather;

import io.helidon.common.config.Config;
import io.helidon.http.media.MediaContext;
import io.helidon.http.media.jsonb.JsonbSupport;
import io.helidon.http.media.jsonp.JsonpSupport;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.staticcontent.StaticContentFeature;
import org.paumard.server.weather.model.City;
import org.paumard.server.weather.model.Parser;
import org.paumard.server.weather.model.Weather;
import org.paumard.server.weather.model.WeatherAgency;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class WeatherServer {

    private static final Random RANDOM = new Random();

    private static void sleepFor(int average, int dispersion) throws InterruptedException {
        Thread.sleep(RANDOM.nextInt(average - dispersion, average + dispersion));
    }

    public static Weather.Type randomWeawerType() {
        return Weather.TYPES.get(RANDOM.nextInt(0, Weather.TYPES.size()));
    }

    private static void registerCities(HttpRouting.Builder routingBuilder, List<City> cities) {
        routingBuilder.get("/cities", (_, response) -> {
            response.send(cities.stream()
                .sorted(Comparator.comparing(City::name))
                .toList());
        });
    }

    public record WeatherAgencyDTO(String name, String tag) { }

    private static void registerWeatherAgencies(HttpRouting.Builder routingBuilder, List<WeatherAgency> weatherAgencies) {
        routingBuilder.get("/weather-agencies", (_, response) -> {
            response.send(weatherAgencies.stream()
                .map(agency -> new WeatherAgencyDTO(agency.name(), agency.tag()))
                .toList());
        });
    }

    private static void registerEachWeatherAgency(HttpRouting.Builder routingBuilder, List<WeatherAgency> weatherAgencies) {
        for (var agency : weatherAgencies) {
            routingBuilder.post("/weather/" + agency.tag(), (request, response) -> {
                var city = request.content().as(City.class);
                WeatherServer.sleepFor(agency.average(), agency.dispersion());
                response.send(new Weather(randomWeawerType(), agency.name()));
            });
        }
    }

    static void main() throws IOException {

        var cities = Parser.parse(Path.of("files", "us-cities.txt"), City::parseLine);
        var agencies = Parser.parse(Path.of("files", "weather-agencies.txt"), WeatherAgency::parseLine);

        var config = Configuration.parse(Path.of("server.properties"));

        var routingBuilder = HttpRouting.builder();

        routingBuilder.get("/whoami", (_, res) -> {
            res.send("Current thread: " + Thread.currentThread());
        });

        registerCities(routingBuilder, cities);

        registerWeatherAgencies(routingBuilder, agencies);
        registerEachWeatherAgency(routingBuilder, agencies);

        var webServer = WebServer.builder()
                .address(InetAddress.getLocalHost())
                 .host(config.host())
                .port(config.port())
                .addFeature(
                        StaticContentFeature.builder()
                                .addClasspath(b -> b.location("/static-content").welcome("index.html").context("/"))
                                .build())
                .routing(routingBuilder)
                .mediaContext(MediaContext.builder()
                        .mediaSupportsDiscoverServices(false)
                        .addMediaSupport(JsonpSupport.create())
                        .addMediaSupport(JsonbSupport.create(Config.empty()))
                        .build())
                .build();

        webServer.start();
    }


}