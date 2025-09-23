package org.paumard.server.travel.model.B_CompanyQuery;


import org.paumard.server.travel.model.city.Cities;
import org.paumard.server.travel.model.company.Companies;
import org.paumard.server.travel.model.response.CompanyServerResponse;
import org.paumard.server.travel.model.response.CompanyServerResponse.MultilegFlight;
import org.paumard.server.travel.model.response.CompanyServerResponse.NoFlight;
import org.paumard.server.travel.model.response.CompanyServerResponse.SimpleFlight;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

public class B_CompanyQuery {

    void main() throws Exception {
        var cities = Cities.read();
        var companies = Companies.readCompanies();

        var airPenguin = companies.companies().get(0);
        var norwegianParrots = companies.companies().get(1);
        var gammaAirlines = companies.companies().get(2);
        var crustyAlbatros = companies.companies().get(3);
        var diamondAirlines = companies.companies().get(4);

        var atlanta = cities.byName("Atlanta");
        var chicago = cities.byName("Chicago");

        var tasks = companies.companies().stream()
              .map(company -> CompanyQueryBuilder.from(company)
                    .toFlyFrom(atlanta).to(chicago))
              .toList();
//        tasks.add(() -> {
//            Thread.sleep(800);
//            throw new RuntimeException("Failing query");
//        });

        try (var scope = StructuredTaskScope.open(
              StructuredTaskScope.Joiner.awaitAll()
        )) {

            var subTasks = tasks.stream()
                  .map(scope::fork)
                  .toList();

            scope.join();

            var bestPrice = bestPriceFrom(subTasks);
            IO.println(bestPrice);
        }
    }

    private static Integer bestPriceFrom(List<Subtask<CompanyServerResponse>> subTasks) {
        return subTasks.stream().map(Subtask::get)
              .<Integer>mapMulti((response, downstream) -> {
                  switch (response) {
                      case SimpleFlight(_, int price) -> downstream.accept(price);
                      case MultilegFlight(_, int price) -> downstream.accept(price);
                      case NoFlight _, CompanyServerResponse.Error _ -> {
                      }
                  }
              })
              .min(Comparator.naturalOrder())
              .orElseThrow();
    }
}
