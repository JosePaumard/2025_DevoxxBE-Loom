package org.paumard.server.travel.model.B_CompanyQuery;


import org.paumard.server.travel.model.city.Cities;
import org.paumard.server.travel.model.company.Companies;

public class B_CompanyQuery {

    void main() throws Exception {
        var cities = Cities.read();
        var companies = Companies.readCompanies();

        var gammaAirLines = companies.companies().get(2);
        var atlanta = cities.byName("Atlanta");
        var chicago = cities.byName("Chicago");


        var companyServerResponseTask =
              CompanyQueryBuilder.from(gammaAirLines)
                    .toFlyFrom(atlanta).to(chicago);

        var companyServerResponse =
              companyServerResponseTask.call();

        IO.println(gammaAirLines);
        IO.println(companyServerResponse);
    }
}
