package pe.com.incoda.demoquarkuslangchaing4j.domain.model;

import java.util.List;

/**
 * Agregado de dominio que combina las condiciones meteorológicas actuales con el
 * pronóstico diario.
 *
 * @param timezone zona horaria resuelta por el proveedor (p. ej. {@code "America/Lima"})
 * @param current  condiciones meteorológicas actuales
 * @param daily    pronósticos diarios (uno por día)
 */
public record WeatherReport(String timezone, WeatherSnapshot current, List<DailyForecast> daily) {
}
