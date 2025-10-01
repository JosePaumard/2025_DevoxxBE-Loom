package org.paumard.server.travel.model.util;

import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import org.paumard.server.travel.model.Flight;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;

public final class FlightJsonDeserializer implements JsonbDeserializer<Flight> {
  @Override
  public Flight deserialize(JsonParser parser,
                            DeserializationContext ctx,
                            Type rtType) {

    var jsonObject = parser.getObject();

    var implClass = jsonObject.containsKey("via") ?
        Flight.Multileg.class :
        Flight.Direct.class;

    try (var jsonBuilder = JsonbBuilder.create()) {
      return jsonBuilder.fromJson(jsonObject.toString(), implClass);
    } catch (Exception ex) {
      throw new UncheckedIOException(new IOException(ex));
    }
  }
}