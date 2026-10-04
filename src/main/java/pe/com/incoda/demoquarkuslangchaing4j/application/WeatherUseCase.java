package pe.com.incoda.demoquarkuslangchaing4j.application;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import pe.com.incoda.demoquarkuslangchaing4j.domain.model.WeatherReport;
import pe.com.incoda.demoquarkuslangchaing4j.domain.service.geocoding.GeocodingService;
import pe.com.incoda.demoquarkuslangchaing4j.domain.service.weather.WeatherService;

/**
 * Caso de uso de aplicación que orquesta las consultas meteorológicas. Depende
 * solo de los puertos de dominio (clima + geocodificación), nunca de detalles de
 * infraestructura.
 */
@ApplicationScoped
public class WeatherUseCase {

    /** Días de pronóstico por defecto. */
    public static final int DEFAULT_DAYS = 7;

    private final WeatherService weatherService;
    private final GeocodingService geocodingService;

    @Inject
    public WeatherUseCase(WeatherService weatherService, GeocodingService geocodingService) {
        this.weatherService = weatherService;
        this.geocodingService = geocodingService;
    }

    /**
     * Obtiene el reporte meteorológico para unas coordenadas.
     *
     * @param latitude  latitud en grados decimales
     * @param longitude longitud en grados decimales
     * @return el reporte o vacío si el proveedor no devuelve datos
     */
    public Optional<WeatherReport> forecast(double latitude, double longitude) {
        return forecast(latitude, longitude, DEFAULT_DAYS);
    }

    /**
     * Obtiene el reporte meteorológico para unas coordenadas y unos días.
     *
     * @param latitude  latitud en grados decimales
     * @param longitude longitud en grados decimales
     * @param days      número de días de pronóstico (1..7)
     * @return el reporte o vacío si el proveedor no devuelve datos
     */
    public Optional<WeatherReport> forecast(double latitude, double longitude, int days) {
        return weatherService.forecast(latitude, longitude, days);
    }

    /**
     * Resuelve la dirección a coordenadas (geocodificación) y luego obtiene el
     * pronóstico para esas coordenadas.
     *
     * @param address dirección o lugar a consultar
     * @return el reporte o vacío si no se pudo geocodificar o no hay datos
     */
    public Optional<WeatherReport> forecastByAddress(String address) {
        return forecastByAddress(address, DEFAULT_DAYS);
    }

    /**
     * Resuelve la dirección a coordenadas y obtiene el pronóstico para unos días.
     *
     * @param address dirección o lugar a consultar
     * @param days    número de días de pronóstico (1..7)
     * @return el reporte o vacío si no se pudo geocodificar o no hay datos
     */
    public Optional<WeatherReport> forecastByAddress(String address, int days) {
        return geocodingService.search(address)
                .flatMap(location -> weatherService.forecast(location.latitude(), location.longitude(), days));
    }
}
