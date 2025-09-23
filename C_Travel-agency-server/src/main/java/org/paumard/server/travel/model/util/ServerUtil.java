package org.paumard.server.travel.model.util;

import io.helidon.common.uri.UriInfo;
import io.helidon.webclient.api.ClientUri;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.function.Supplier;

public class ServerUtil {
    public static final Supplier<Properties> properties =
          StableValue.supplier(
                () -> readPropertiesFrom("server.properties"));

    public static final Supplier<ClientUri> WEATHER_SERVER_URI =
          StableValue.supplier(
                () -> createWeatherServerURI(properties.get()));
    public static final Supplier<ClientUri> COMPANY_SERVER_URI =
          StableValue.supplier(
                () -> createCompanyServerURI(properties.get()));

    public static Properties readPropertiesFrom(String fileName) {
        var properties = new Properties();
        try (var reader = Files.newBufferedReader(Path.of(fileName))) {
            properties.load(reader);
            return properties;
        } catch (IOException e) {
            System.out.println("Error reading properties file " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static ClientUri createWeatherServerURI(Properties properties) {
        var WEATHER_SERVER_HOST = properties.getProperty("weather-agencies.host");
        var WEATHER_SERVER_PORT = Integer.parseInt(properties.getProperty("weather-agencies.port"));
        var weatherServerUriInfo = UriInfo.builder()
              .host(WEATHER_SERVER_HOST)
              .port(WEATHER_SERVER_PORT)
              .build();
        return ClientUri.create(weatherServerUriInfo);
    }

    private static ClientUri createCompanyServerURI(Properties properties) {
        var COMPANY_SERVER_HOST = properties.getProperty("companies.host");
        var COMPANY_SERVER_PORT = Integer.parseInt(properties.getProperty("companies.port"));
        var companyServerUriInfo = UriInfo.builder()
              .host(COMPANY_SERVER_HOST)
              .port(COMPANY_SERVER_PORT)
              .build();
        return ClientUri.create(companyServerUriInfo);
    }
}
