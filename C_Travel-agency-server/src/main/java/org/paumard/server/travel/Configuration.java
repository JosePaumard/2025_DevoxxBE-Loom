package org.paumard.server.travel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class Configuration {
  private final Properties properties;

  private Configuration(Properties properties) {
    this.properties = properties;
  }

  public static Configuration parse(Path path) throws IOException {
    var properties = new Properties();
    try (var reader = Files.newBufferedReader(path)) {
      properties.load(reader);
      return new Configuration(properties);
    }
  }

  public record Conf(String host, int port) {
  }

  public Conf weatherAgency() {
    var host = properties.getProperty("weather-agencies.host");
    var port = Integer.parseInt(properties.getProperty("weather-agencies.port"));
    return new Conf(host, port);
  }

  public Conf company() {
    var host = properties.getProperty("companies.host");
    var port = Integer.parseInt(properties.getProperty("companies.port"));
    return new Conf(host, port);
  }
}
