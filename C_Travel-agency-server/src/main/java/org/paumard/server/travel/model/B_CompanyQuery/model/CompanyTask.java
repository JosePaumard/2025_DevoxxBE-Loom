package org.paumard.server.travel.model.B_CompanyQuery.model;

import org.paumard.server.travel.model.company.Company;
import org.paumard.server.travel.model.response.CompanyServerResponse;

import java.util.concurrent.Callable;

public record CompanyTask(
      Company company,
      Callable<CompanyServerResponse> task) {
}
