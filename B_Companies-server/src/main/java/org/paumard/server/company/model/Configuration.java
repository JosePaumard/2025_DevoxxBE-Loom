package org.paumard.server.company.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class Configuration {
  public record Agency(String host, int port) {}

  public static Agency parseAngencyConf(Path path) throws IOException {
    var properties = new Properties();
    try (var reader = Files.newBufferedReader(path)) {
      properties.load(reader);

      var host = properties.getProperty("weather-agencies.host");
      var port = Integer.parseInt(properties.getProperty("weather-agencies.port"));
      return new Agency(host, port);
    }
  }

  public record Company(String host, int port) {}

  public static Company parseCompanyConf(Path path) throws IOException {
    var properties = new Properties();
    try (var reader = Files.newBufferedReader(path)) {
      properties.load(reader);

      var host = properties.getProperty("companies.host");
      var port = Integer.parseInt(properties.getProperty("companies.port"));
      return new Company(host, port);
    }
  }
}
