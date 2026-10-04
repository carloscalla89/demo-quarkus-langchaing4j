package pe.com.incoda.demoquarkuslangchaing4j.domain.service.weather;

import java.util.Optional;

import pe.com.incoda.demoquarkuslangchaing4j.domain.model.WeatherReport;

/**
 * Puerto de dominio (salida) para pronósticos meteorológicos. Las capas de
 * dominio y aplicación dependen solo de esta abstracción; la implementación
 * concreta vive en {@code infrastructure/output/client/openmeteo}.
 */
public interface WeatherService {

    /**
     * Obtiene las condiciones actuales y el pronóstico diario para las coordenadas.
     *
     * @param latitude  latitud en grados decimales (WGS84)
     * @param longitude longitud en grados decimales (WGS84)
     * @param days      número de días de pronóstico (1..7)
     * @return el reporte meteorológico o vacío si el proveedor no devuelve datos
     */
    Optional<WeatherReport> forecast(double latitude, double longitude, int days);
}
