package org.paumard.server.weather.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class Parser {
  private Parser() {}

  public static <T> List<T> parse(Path path, Function<String, T> mapper) throws IOException {
    try (var lines = Files.lines(path)) {
      return lines
          .filter(line -> !line.isEmpty() && !line.startsWith("#"))
          .map(mapper)
          .toList();
    }
  }
}
