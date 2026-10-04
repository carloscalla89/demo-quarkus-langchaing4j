package pe.com.incoda.demoquarkuslangchaing4j.domain.dto;

import java.util.List;

import pe.com.incoda.demoquarkuslangchaing4j.domain.model.DailyForecast;
import pe.com.incoda.demoquarkuslangchaing4j.domain.model.WeatherReport;
import pe.com.incoda.demoquarkuslangchaing4j.domain.model.WeatherSnapshot;

/**
 * Contrato expuesto por la API REST de entrada para un reporte meteorológico.
 * Se comunica únicamente con este DTO; los modelos de dominio
 * {@link WeatherReport}, {@link WeatherSnapshot} y {@link DailyForecast} nunca
 * se exponen hacia afuera.
 *
 * @param timezone zona horaria del reporte (p. ej. {@code "America/Lima"})
 * @param current  condiciones meteorológicas actuales
 * @param daily    pronóstico diario
 */
public record WeatherReportDto(String timezone, CurrentWeatherDto current, List<DailyForecastDto> daily) {

    /**
     * Condiciones meteorológicas actuales expuestas por REST.
     *
     * @param temperatureC            temperatura del aire en grados Celsius
     * @param apparentTemperatureC    temperatura percibida en grados Celsius
     * @param relativeHumidityPercent humedad relativa en porcentaje
     * @param weatherCode             código meteorológico WMO
     * @param description             descripción legible del código WMO
     * @param windSpeedKmh            velocidad del viento en km/h
     * @param observedAt              instante de observación (ISO-8601)
     */
    public record CurrentWeatherDto(
            double temperatureC,
            double apparentTemperatureC,
            int relativeHumidityPercent,
            int weatherCode,
            String description,
            double windSpeedKmh,
            String observedAt) {
    }

    /**
     * Pronóstico de un día expuesto por REST.
     *
     * @param date                            fecha del pronóstico (ISO-8601)
     * @param temperatureMaxC                 temperatura máxima en grados Celsius
     * @param temperatureMinC                 temperatura mínima en grados Celsius
     * @param weatherCode                     código meteorológico WMO
     * @param description                     descripción legible del código WMO
     * @param precipitationProbabilityPercent probabilidad máxima de precipitación en porcentaje
     */
    public record DailyForecastDto(
            String date,
            double temperatureMaxC,
            double temperatureMinC,
            int weatherCode,
            String description,
            int precipitationProbabilityPercent) {
    }

    /**
     * Convierte el agregado de dominio en su DTO de salida.
     *
     * @param report reporte meteorológico de dominio
     * @return el DTO equivalente listo para serializar
     */
    public static WeatherReportDto from(WeatherReport report) {
        return new WeatherReportDto(
                report.timezone(),
                fromSnapshot(report.current()),
                report.daily().stream().map(WeatherReportDto::fromDaily).toList());
    }

    /**
     * Convierte las condiciones actuales de dominio en su DTO.
     *
     * @param snapshot condiciones actuales de dominio
     * @return el DTO equivalente
     */
    public static CurrentWeatherDto fromSnapshot(WeatherSnapshot snapshot) {
        return new CurrentWeatherDto(
                snapshot.temperatureC(),
                snapshot.apparentTemperatureC(),
                snapshot.relativeHumidityPercent(),
                snapshot.weatherCode(),
                snapshot.description(),
                snapshot.windSpeedKmh(),
                snapshot.observedAt());
    }

    /**
     * Convierte el pronóstico diario de dominio en su DTO.
     *
     * @param forecast pronóstico diario de dominio
     * @return el DTO equivalente
     */
    public static DailyForecastDto fromDaily(DailyForecast forecast) {
        return new DailyForecastDto(
                forecast.date(),
                forecast.temperatureMaxC(),
                forecast.temperatureMinC(),
                forecast.weatherCode(),
                forecast.description(),
                forecast.precipitationProbabilityPercent());
    }
}
