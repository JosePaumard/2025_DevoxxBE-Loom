package org.paumard.server.travel.model.B_CompanyQuery.model;

import org.paumard.server.travel.model.company.Company;
import org.paumard.server.travel.model.response.CompanyServerResponse;

import java.util.concurrent.StructuredTaskScope;

public record CompanySubTask(
      Company company,
      StructuredTaskScope.Subtask<CompanyServerResponse> subtask) {
}
