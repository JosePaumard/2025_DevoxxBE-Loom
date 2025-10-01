package org.paumard.server.travel;

import io.helidon.common.uri.UriInfo;
import io.helidon.webclient.api.ClientUri;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.function.Supplier;

public final class Client {
    private Client() {}

    private static final Supplier<Configuration> CONFIGURATION =
        StableValue.supplier(() -> {
              try {
                return Configuration.parse(Path.of("server.properties"));
              } catch (IOException e) {
                throw new UncheckedIOException(e);  // kids do not do that !
              }
            });

    private static final Supplier<ClientUri> WEATHER_SERVER_URI =
        StableValue.supplier(
            () -> createServerURI(CONFIGURATION.get().weatherAgency()));

    private static final Supplier<ClientUri> COMPANY_SERVER_URI =
        StableValue.supplier(
            () -> createServerURI(CONFIGURATION.get().company()));

    private static ClientUri createServerURI(Configuration.Conf conf) {
        var weatherServerUriInfo = UriInfo.builder()
            .host(conf.host())
            .port(conf.port())
            .build();
        return ClientUri.create(weatherServerUriInfo);
    }

    public static ClientUri getWeatherServerURI() {
      return WEATHER_SERVER_URI.get();
    }

    public static ClientUri getCompanyServerURI() {
        return COMPANY_SERVER_URI.get();
    }
}
