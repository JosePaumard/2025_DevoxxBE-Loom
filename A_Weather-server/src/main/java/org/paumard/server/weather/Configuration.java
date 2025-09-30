package org.paumard.server.weather;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public record Configuration(String host, int port) {
  public static Configuration parse(Path path) throws IOException {
    var properties = new Properties();
    try (var reader = Files.newBufferedReader(path)) {
      properties.load(reader);

      var host = properties.getProperty("weather-agencies.host");
      var port = Integer.parseInt(properties.getProperty("weather-agencies.port"));
      return new Configuration(host, port);
    }
  }
}
