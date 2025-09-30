package org.paumard.server.weather.model;


import java.util.List;
import java.util.Objects;

public record Weather(Type weather, String agency) {
  public Weather {
    Objects.requireNonNull(weather);
    Objects.requireNonNull(agency);
  }

  public enum Type {
    Sunny, Cloudy, Rainy;
  }

  public static final List<Type> TYPES = List.of(Type.values());
}
