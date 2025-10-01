package org.paumard.server.company;

import io.helidon.common.config.Config;
import io.helidon.http.Status;
import io.helidon.http.media.MediaContext;
import io.helidon.http.media.jsonb.JsonbSupport;
import io.helidon.http.media.jsonp.JsonpSupport;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.staticcontent.StaticContentFeature;
import org.paumard.server.company.model.FlightPricing;
import org.paumard.server.company.model.Parser;
import org.paumard.server.company.model.City;
import org.paumard.server.company.model.Company;
import org.paumard.server.company.model.Flight;
import org.paumard.server.company.model.DirectFlightParser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class CompanyServer {

  private static final Random RANDOM = new Random(314L);

  private static void sleepFor(int average, int dispersion) throws InterruptedException {
    Thread.sleep(RANDOM.nextInt(average - dispersion, average + dispersion));
  }

  private static void registerCities(HttpRouting.Builder routingBuilder, List<City> cities) {
    routingBuilder.get("/cities", (_, response) -> {
      response.send(cities.stream()
          .sorted(Comparator.comparing(City::name))
          .toList());
    });
  }

  public record CompanyDTO(String name, String tag) {
  }

  private static void registerCompanies(HttpRouting.Builder routing, List<Company> companies) {
    routing.get("/companies", (_, response) -> {
      response.send(companies.stream()
          .map(company -> new CompanyDTO(company.name(), company.tag()))
          .toList());
    });
  }

  public record ErrorMessage(String message) {
  }

  public record FlightRequest(City from, City to) {
  }

  public record FlightPrice(Flight flight, int price) {
  }

  private static void registerEachCompany(HttpRouting.Builder routing, List<Company> companies, Map<Flight.Direct, Integer> priceMap) {
    for (var company : companies) {
      routing.post("/company/" + company.tag(), (serverRequest, response) -> {
        sleepFor(company.average(), company.dispersion());

        var request = serverRequest.content().as(FlightRequest.class);
        var delta = RANDOM.nextInt(-10, +10);

        {
          var flight = new Flight.Direct(request.from, request.to);
          var price = FlightPricing.price(company, flight, priceMap, delta);
          if (price.isPresent()) {
            response.send(new FlightPrice(flight, price.orElseThrow()));
            return;
          }
        }

        var bestFlightOpt = company.servedCities().stream()
            .filter(city -> !city.equals(request.from) & !city.equals(request.to))
            .flatMap(via -> {
              var multileg = new Flight.Multileg(request.from, via, request.to);
              var optPrice = FlightPricing.price(company, multileg, priceMap, delta);
              return optPrice.stream().mapToObj(price -> new FlightPrice(multileg, price));
            })
            .min(Comparator.comparingInt(FlightPrice::price));
        if (bestFlightOpt.isPresent()) {
          response.send(bestFlightOpt.orElseThrow());
          return;
        }

        response.status(Status.NOT_FOUND_404);
        response.send(new ErrorMessage(
            company.name() + " does not serve " + request.from.name() + " to " + request.to.name()));
      });
    }
  }

  static void main() throws IOException {

    var cities = Parser.parse(Path.of("files", "us-cities.txt"), City::parseLine);
    var companies = Parser.parse(Path.of("files", "companies.txt"),
        line -> Company.parseLine(line, cities, flightAvailabilityRate -> RANDOM.nextInt(0, 100) <= flightAvailabilityRate));
    var flightMap = DirectFlightParser.parseFlights(Path.of("files", "flights-and-prices.txt"), cities);

    var config = Configuration.parse(Path.of("server.properties")).company();

    var routingBuilder = HttpRouting.builder();

    routingBuilder.get("/whoami", (_, res) -> {
      res.send("Current thread: " + Thread.currentThread());
    });

    registerCities(routingBuilder, cities);

    registerCompanies(routingBuilder, companies);
    registerEachCompany(routingBuilder, companies, flightMap);

    var webServer = WebServer.builder()
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