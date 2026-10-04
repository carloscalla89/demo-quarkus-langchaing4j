package pe.com.incoda.demoquarkuslangchaing4j.domain.model;

/**
 * Objeto de valor de dominio con las condiciones meteorológicas actuales.
 *
 * @param temperatureC            temperatura del aire en grados Celsius
 * @param apparentTemperatureC    temperatura percibida ("sensación térmica") en grados Celsius
 * @param relativeHumidityPercent humedad relativa en porcentaje
 * @param weatherCode             código meteorológico WMO
 * @param description             descripción legible del código WMO
 * @param windSpeedKmh            velocidad del viento en kilómetros por hora
 * @param observedAt              instante de observación (ISO-8601)
 */
public record WeatherSnapshot(
        double temperatureC,
        double apparentTemperatureC,
        int relativeHumidityPercent,
        int weatherCode,
        String description,
        double windSpeedKmh,
        String observedAt) {
}
