package org.paumard.server.travel.model.response;

import org.paumard.server.travel.model.company.Company;
import org.paumard.server.travel.model.flight.Flight;

import java.util.Objects;

public sealed interface CompanyResponse {

    sealed interface Priced extends CompanyResponse {
        int price();
    }

    sealed interface Fail extends CompanyResponse {}
    
    record PricedSimpleFlight(Company company, Flight.SimpleFlight simpleFlight, int price)
            implements Priced {
        public PricedSimpleFlight {
            Objects.requireNonNull(company);
            Objects.requireNonNull(simpleFlight);
        }
    }

    record PricedMultilegFlight(Company company, Flight.MultilegFlight multilegFlight, int price)
            implements Priced {
        public PricedMultilegFlight {
            Objects.requireNonNull(company);
            Objects.requireNonNull(multilegFlight);
        }
    }

    record NoFlight(Company company, String message)
            implements Fail {
        public NoFlight {
            Objects.requireNonNull(company);
            Objects.requireNonNull(message);
        }
    }

    record Error(Company company, String message)
            implements Fail {
        public Error {
            Objects.requireNonNull(company);
            Objects.requireNonNull(message);
        }
    }
}