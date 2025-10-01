package org.paumard.server.travel.model;

import java.util.Objects;
import java.util.regex.Pattern;

public record WeatherAgency(String name, String tag, int average, int dispersion) {
  public WeatherAgency {
    Objects.requireNonNull(name);
    Objects.requireNonNull(tag);
  }

  public static WeatherAgency parseLine(String line) {
    var elements = Pattern.compile(",").splitAsStream(line).toArray(String[]::new);
    var name = elements[0].trim();
    var tag = elements[1].trim();
    int average = Integer.parseInt(elements[2].trim());
    int dispersion = Integer.parseInt(elements[3].trim());
    return new WeatherAgency(name, tag, average, dispersion);
  }
}
