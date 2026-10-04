package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.output.client.openmeteo;

import io.quarkus.rest.client.reactive.ClientQueryParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * Cliente REST (MicroProfile) para la API pública de Open-Meteo. La URL base se
 * configura bajo la clave {@code open-meteo} (quarkus.rest-client.open-meteo.*).
 * No requiere API key para uso no comercial.
 */
@RegisterRestClient(configKey = "open-meteo")
@Path("/")
public interface OpenMeteoClient {

    /** Campos solicitados para las condiciones actuales. */
    String CURRENT_FIELDS =
            "temperature_2m,apparent_temperature,relative_humidity_2m,weather_code,wind_speed_10m";

    /** Campos solicitados para el pronóstico diario. */
    String DAILY_FIELDS =
            "temperature_2m_max,temperature_2m_min,weather_code,precipitation_probability_max";

    /**
     * Pronóstico para unas coordenadas.
     *
     * @param latitude  latitud en grados decimales
     * @param longitude longitud en grados decimales
     * @param days      número de días de pronóstico (1..7)
     * @return la respuesta cruda de Open-Meteo
     */
    @GET
    @Path("/v1/forecast")
    @ClientQueryParam(name = "current", value = CURRENT_FIELDS)
    @ClientQueryParam(name = "daily", value = DAILY_FIELDS)
    @ClientQueryParam(name = "timezone", value = "auto")
    OpenMeteoResponse forecast(
            @QueryParam("latitude") double latitude,
            @QueryParam("longitude") double longitude,
            @QueryParam("forecast_days") int days);
}
