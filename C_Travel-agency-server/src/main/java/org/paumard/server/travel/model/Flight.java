package org.paumard.server.travel.model;

import java.util.Objects;

public sealed interface Flight {

  record Direct(City from, City to) implements Flight {
    public Direct {
      Objects.requireNonNull(from, "The departure city is not defined");
      Objects.requireNonNull(to, "The destination city is not defined");
      if (to.equals(from)) {
        throw new IllegalArgumentException("To and from are the same city");
      }
    }
  }

  record Multileg(City from, City via, City to) implements Flight {
    public Multileg {
      Objects.requireNonNull(from);
      Objects.requireNonNull(via);
      Objects.requireNonNull(to);
      if (from.equals(via)) {
        throw new IllegalArgumentException("From and Via are the same city");
      }
      if (from.equals(to)) {
        throw new IllegalArgumentException("From and To are the same city");
      }
      if (via.equals(to)) {
        throw new IllegalArgumentException("Via and To are the same city");
      }
    }
  }
}
