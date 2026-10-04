package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.output.client.openmeteo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payload del proveedor para un pronóstico de Open-Meteo. Vive en
 * infrastructure/output/client/openmeteo porque es un detalle de la API externa,
 * no un contrato que expongamos.
 *
 * @param timezone zona horaria resuelta por el proveedor
 * @param current  condiciones actuales
 * @param daily    pronóstico diario
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenMeteoResponse(
        @JsonProperty("timezone") String timezone,
        @JsonProperty("current") Current current,
        @JsonProperty("daily") Daily daily) {

    /**
     * Condiciones meteorológicas actuales.
     *
     * @param time                instante de observación (ISO-8601)
     * @param temperature         temperatura del aire en grados Celsius
     * @param apparentTemperature sensación térmica en grados Celsius
     * @param relativeHumidity    humedad relativa en porcentaje
     * @param weatherCode         código meteorológico WMO
     * @param windSpeed           velocidad del viento en km/h
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Current(
            @JsonProperty("time") String time,
            @JsonProperty("temperature_2m") Double temperature,
            @JsonProperty("apparent_temperature") Double apparentTemperature,
            @JsonProperty("relative_humidity_2m") Integer relativeHumidity,
            @JsonProperty("weather_code") Integer weatherCode,
            @JsonProperty("wind_speed_10m") Double windSpeed) {
    }

    /**
     * Pronóstico diario. Son listas paralelas: el índice {@code i} corresponde al
     * mismo día en todas ellas.
     *
     * @param time                          fechas (ISO-8601)
     * @param temperatureMax                temperaturas máximas (°C)
     * @param temperatureMin                temperaturas mínimas (°C)
     * @param weatherCode                   códigos WMO
     * @param precipitationProbabilityMax   probabilidad máxima de precipitación (%)
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Daily(
            @JsonProperty("time") List<String> time,
            @JsonProperty("temperature_2m_max") List<Double> temperatureMax,
            @JsonProperty("temperature_2m_min") List<Double> temperatureMin,
            @JsonProperty("weather_code") List<Integer> weatherCode,
            @JsonProperty("precipitation_probability_max") List<Integer> precipitationProbabilityMax) {
    }
}
