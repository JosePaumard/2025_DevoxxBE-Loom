package org.paumard.server.travel.model.B_CompanyQuery;

import io.helidon.webclient.api.WebClient;
import org.paumard.server.travel.model.city.City;
import org.paumard.server.travel.model.company.Company;
import org.paumard.server.travel.model.flight.travel.Flight;
import org.paumard.server.travel.model.response.CompanyServerResponse;
import org.paumard.server.travel.model.util.ServerUtil;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public interface CompanyQueryBuilder
      extends Callable<CompanyServerResponse> {

    interface CompanySupplier extends Supplier<Company> {

        default Company company() {
            return get();
        }

        default CompanyAndDestinationSupplier toFlyFrom(City from) {
            return () -> new CompanyAndDeparture(company(), from);
        }
    }

    record CompanyAndDeparture(Company company, City departure) {
    }

    interface CompanyAndDestinationSupplier
          extends Supplier<CompanyAndDeparture> {

        default CompanyAndDeparture companyAndDestination() {
            return get();
        }

        default CompanyQueryBuilder to(City destination) {
            return () -> {
                var company = companyAndDestination().company();
                var cityFrom = companyAndDestination().departure();
                var flight = Flight.from(cityFrom).to(destination);
                var response = WebClient.builder()
                      .baseUri(ServerUtil.COMPANY_SERVER_URI.get()).build()
                      .post("/company/" + company.tag())
                      .submit(flight);

                return CompanyServerResponse.of(response);
            };
        }
    }

    static CompanySupplier from(Company company) {
        return () -> company;
    }
}
