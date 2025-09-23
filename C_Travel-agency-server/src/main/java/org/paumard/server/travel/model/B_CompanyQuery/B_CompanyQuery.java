package org.paumard.server.travel.model.B_CompanyQuery;


import org.paumard.server.travel.model.city.Cities;
import org.paumard.server.travel.model.company.Companies;
import org.paumard.server.travel.model.company.Company;
import org.paumard.server.travel.model.flight.Flight;
import org.paumard.server.travel.model.response.CompanyServerResponse;
import org.paumard.server.travel.model.response.CompanyServerResponse.MultilegFlight;
import org.paumard.server.travel.model.response.CompanyServerResponse.NoFlight;
import org.paumard.server.travel.model.response.CompanyServerResponse.SimpleFlight;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;
import java.util.concurrent.StructuredTaskScope.Subtask;

public class B_CompanyQuery {

    record CompanyTask(
          Company company,
          Callable<CompanyServerResponse> task) {
    }

    record CompanySubTask(
          Company company,
          Subtask<CompanyServerResponse> subtask) {
    }

    record CompanyFlightPrice(
          Company company,
          Flight flight, int price) {
    }

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

        var phoenix = cities.byName("Phoenix");
        var philadelphia = cities.byName("Philadelphia");

        var companyTasks = companies.companies().stream()
              .map(company ->
                    new CompanyTask(company,
                          CompanyQueryBuilder.from(company)
                                .toFlyFrom(phoenix).to(philadelphia)))
              .toList();
//        tasks.add(() -> {
//            Thread.sleep(800);
//            throw new RuntimeException("Failing query");
//        });

        try (var scope = StructuredTaskScope.open(
              Joiner.<CompanyServerResponse>awaitAll()
        )) {

            var companySubtasks = companyTasks.stream()
                  .map(task ->
                        new CompanySubTask(
                              task.company(),
                              scope.fork(task.task())))
                  .toList();

            scope.join();

            var bestCompanyFlightPrice = getBestCompanyFlightPrice(companySubtasks);
            IO.println(bestCompanyFlightPrice);
        }
    }

    private static CompanyFlightPrice getBestCompanyFlightPrice(List<CompanySubTask> companySubtasks) {
        return companySubtasks.stream()
              .<CompanyFlightPrice>mapMulti((companySubtask, downstream) -> {
                  switch (companySubtask.subtask().get()) {
                      case SimpleFlight(Flight.SimpleFlight flight, int price) ->
                            downstream.accept(new CompanyFlightPrice(
                                  companySubtask.company(),
                                  flight, price));
                      case MultilegFlight(Flight.MultilegFlight flight, int price) ->
                            downstream.accept(new CompanyFlightPrice(
                                  companySubtask.company(),
                                  flight, price));
                      case NoFlight _, CompanyServerResponse.Error _ -> {
                      }
                  }
              })
              .min(Comparator.comparing(CompanyFlightPrice::price))
              .orElseThrow();
    }
}
