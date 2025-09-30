package org.paumard.server.weather.model;

import java.util.Objects;

public record City(int id, String name) {
  public City {
    Objects.requireNonNull(name);
  }


  public static City parseLine(String line) {
    line = line.trim();
    var indexOfFirstSpace = line.indexOf(' ');
    var id = Integer.parseInt(line.substring(0, indexOfFirstSpace));
    var name = line.substring(indexOfFirstSpace + 1);
    return new City(id, name);
  }
}