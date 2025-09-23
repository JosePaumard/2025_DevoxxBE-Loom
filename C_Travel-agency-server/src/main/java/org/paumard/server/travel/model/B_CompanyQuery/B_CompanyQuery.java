package org.paumard.server.travel.model.B_CompanyQuery;


import org.paumard.server.travel.model.city.Cities;
import org.paumard.server.travel.model.company.Companies;

import java.util.concurrent.StructuredTaskScope;
import java.util.stream.Collectors;

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
              .collect(Collectors.toList());
        tasks.add(() -> {
            Thread.sleep(800);
            throw new RuntimeException("Failing query");
        });

        try (var scope = StructuredTaskScope.open()) {

            var subTasks = tasks.stream()
                  .map(scope::fork)
                  .toList();

            try {
                scope.join();
            } catch (StructuredTaskScope.FailedException _) {
                IO.println("Failed exception caught");
            }

            subTasks.forEach(subTask -> {
                IO.println(subTask.state());
                if (subTask.state() == StructuredTaskScope.Subtask.State.SUCCESS) {
                    IO.println(subTask.get());
                }
            });

        }
    }
}
