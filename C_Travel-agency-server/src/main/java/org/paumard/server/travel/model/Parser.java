package org.paumard.server.travel.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

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
