package org.paumard.server.travel.model.B_CompanyQuery;

import jakarta.json.JsonObject;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import org.paumard.server.travel.model.Flight;

import java.lang.reflect.Type;

public final class FlightJsonDeserializer implements JsonbDeserializer<Flight> {
    @Override
    public Flight deserialize(JsonParser parser,
                                  DeserializationContext ctx,
                                  Type rtType) {
        var json = parser.getObject();
        if (json.containsKey("via")) {
            return ctx.deserialize(Flight.Multileg.class, parser);
        } else {
            return ctx.deserialize(Flight.Direct.class, parser);
        }
    }
}