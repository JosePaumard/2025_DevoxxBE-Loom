package org.paumard.server.travel.model.B_CompanyQuery;


import io.helidon.http.Status;
import io.helidon.webclient.api.WebClient;
import jakarta.json.Json;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.paumard.server.travel.Client;
import org.paumard.server.travel.model.City;
import org.paumard.server.travel.model.Company;
import org.paumard.server.travel.model.CompanyFlightPrice;
import org.paumard.server.travel.model.Flight;
import org.paumard.server.travel.model.Parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class B_CompanyQuery {

    private static final Random RANDOM = new Random();

    public record FlightPrice(Flight fligth, int price) {}

    private static Optional<FlightPrice> companyQuery(Company company, City from, City to) throws IOException {
        try (var response = WebClient.builder()
            .baseUri(Client.getCompanyServerURI())
            .build()
            .post("/company/" + company.tag())
            .submit(new Flight.Direct(from, to))) {
            if (response.status() == Status.OK_200) {
                var jsonText = response.as(String.class);
                IO.println(jsonText);

                var config = new JsonbConfig()
                    .withDeserializers(new FlightJsonDeserializer());

                try(var jsonb = JsonbBuilder.create(config)) {
                    var price = jsonb.fromJson(jsonText, FlightPrice.class);
                    return Optional.of(price);
                } catch (Exception e) {
                  throw new IOException(e);
                }
            }
            return Optional.empty();
        }
    }

    public static Optional<CompanyFlightPrice> queryFlightPrice(List<Company> companies, City from, City to) throws InterruptedException {
        try (var scope = StructuredTaskScope.open(
            Joiner.<Optional<CompanyFlightPrice>>awaitAll())) {

            var subtasks = companies.stream()
                .map(company -> scope.fork(() -> {
                    var flightPriceOpt = companyQuery(company, from, to);
                    return flightPriceOpt.map(flightPrice -> new CompanyFlightPrice(company, flightPrice.fligth, flightPrice.price));
                }))
                .toList();

            scope.join();

            return subtasks.stream()
                .filter(subtask -> subtask.state() == StructuredTaskScope.Subtask.State.SUCCESS)
                .flatMap(subtask -> subtask.get().stream())
                .min(Comparator.comparing(CompanyFlightPrice::price));
        }
    }

    static void main() throws Exception {
        var cities = Parser.parse(Path.of("files", "us-cities.txt"), City::parseLine);
        var cityByName = cities.stream().collect(toMap(City::name, identity()));
        var companies = Parser.parse(Path.of("files", "companies.txt"),
            line -> Company.parseLine(line, cities, flightAvailabilityRate -> RANDOM.nextInt(0, 100) <= flightAvailabilityRate));

        var phoenix = cityByName.get("Phoenix");
        var philadelphia = cityByName.get("Philadelphia");

        var airPenguin = companies.get(0);
        var norwegianParrots = companies.get(1);
        var gammaAirlines = companies.get(2);
        var crustyAlbatros = companies.get(3);
        var diamondAirlines = companies.get(4);
        var trustedCompanies = List.of(airPenguin,norwegianParrots,gammaAirlines, crustyAlbatros,diamondAirlines);

        var companyFlightPriceOpt = queryFlightPrice(trustedCompanies, phoenix, philadelphia);
        IO.println(companyFlightPriceOpt);
    }
}
