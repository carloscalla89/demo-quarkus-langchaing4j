package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.tools;

import java.util.List;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import pe.com.incoda.demoquarkuslangchaing4j.application.GeocodingUseCase;
import pe.com.incoda.demoquarkuslangchaing4j.application.WeatherUseCase;
import pe.com.incoda.demoquarkuslangchaing4j.domain.model.GeoLocation;
import pe.com.incoda.demoquarkuslangchaing4j.domain.model.WeatherReport;
import pe.com.incoda.demoquarkuslangchaing4j.domain.model.WeatherToolResult;

/**
 * Herramientas (function calling) que exponen el stack meteorológico real al
 * LLM: geocodificación (Nominatim) y pronóstico (Open-Meteo), a través de los
 * casos de uso de aplicación.
 *
 * <p>Cada método anotado con {@code @Tool} puede ser invocado por el modelo. Lo
 * que el LLM ve es la descripción de {@code @Tool} y de {@code @P}; este Javadoc
 * es solo documentación para desarrolladores.
 *
 * <p>Devuelven {@link WeatherToolResult} o {@link GeoLocation}, que LangChain4j
 * serializa a JSON para el modelo y que la capa de aplicación reutiliza para
 * construir la respuesta REST estructurada.
 */
@ApplicationScoped
public class WeatherTools {

    /** Atribución obligatoria de las fuentes de datos. */
    static final String ATTRIBUTION =
            "Datos meteorológicos de Open-Meteo.com (CC BY 4.0); "
                    + "geocodificación por OpenStreetMap/Nominatim (ODbL)";

    /** Número máximo de días de pronóstico admitidos. */
    private static final int MAX_DAYS = 7;

    /** Número de coincidencias de geocodificación que se devuelven. */
    private static final int GEOCODE_LIMIT = 5;

    private final WeatherUseCase weatherUseCase;
    private final GeocodingUseCase geocodingUseCase;

    /**
     * Construye las tools con los casos de uso de aplicación.
     *
     * @param weatherUseCase   caso de uso de pronóstico (geocodificación + clima)
     * @param geocodingUseCase caso de uso de geocodificación
     */
    @Inject
    public WeatherTools(WeatherUseCase weatherUseCase, GeocodingUseCase geocodingUseCase) {
        this.weatherUseCase = weatherUseCase;
        this.geocodingUseCase = geocodingUseCase;
    }

    /**
     * Obtiene el reporte meteorológico completo de una ciudad, encadenando la
     * geocodificación de la dirección y el pronóstico por coordenadas.
     *
     * @param city nombre de la ciudad (p. ej. {@code "Lima"})
     * @param days número de días de pronóstico (1..7)
     * @return resultado compacto con condiciones actuales, pronóstico y atribución
     * @throws IllegalArgumentException si no se puede geocodificar la ciudad
     */
    @Tool(name = "get_weather_by_city",
          value = "Devuelve el clima actual y el pronóstico de hasta 7 días de una ciudad. "
                + "Úsala cuando el usuario pregunte por el clima de un lugar concreto.")
    public WeatherToolResult getWeatherByCity(
            @P("Nombre de la ciudad, por ejemplo 'Lima' o 'Barcelona'") String city,
            @P(value = "Número de días de pronóstico (1-7); por defecto 7", defaultValue = "7") int days) {
        WeatherReport report = weatherUseCase.forecastByAddress(city, clampDays(days))
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la ciudad: " + city));
        return toToolResult(city, report);
    }

    /**
     * Obtiene el reporte meteorológico para unas coordenadas concretas.
     *
     * @param latitude  latitud en grados decimales (WGS84)
     * @param longitude longitud en grados decimales (WGS84)
     * @param days      número de días de pronóstico (1..7)
     * @return resultado compacto con condiciones actuales, pronóstico y atribución
     * @throws IllegalArgumentException si el proveedor no devuelve datos
     */
    @Tool(name = "get_weather_by_coordinates",
          value = "Devuelve el clima para una latitud y longitud concretas.")
    public WeatherToolResult getWeatherByCoordinates(
            @P("Latitud en grados decimales (WGS84)") double latitude,
            @P("Longitud en grados decimales (WGS84)") double longitude,
            @P(value = "Número de días de pronóstico (1-7); por defecto 7", defaultValue = "7") int days) {
        WeatherReport report = weatherUseCase.forecast(latitude, longitude, clampDays(days))
                .orElseThrow(() -> new IllegalArgumentException("Sin datos para esas coordenadas"));
        return toToolResult(latitude + "," + longitude, report);
    }

    /**
     * Geocodificación directa devolviendo varias coincidencias para desambiguar.
     *
     * @param address ciudad o dirección (p. ej. {@code "Santiago"})
     * @return hasta {@value #GEOCODE_LIMIT} coincidencias
     * @throws IllegalArgumentException si no hay coincidencias
     */
    @Tool(name = "geocode_city",
          value = "Convierte el nombre de una ciudad o dirección en coordenadas. "
                + "Devuelve hasta 5 coincidencias; si hay varias, pide aclaración al usuario.")
    public List<GeoLocation> geocodeCity(
            @P("Ciudad o dirección, por ejemplo 'Lima, Perú'") String address) {
        List<GeoLocation> results = geocodingUseCase.searchAll(address, GEOCODE_LIMIT);
        if (results.isEmpty()) {
            throw new IllegalArgumentException("No se encontró: " + address);
        }
        return results;
    }

    /**
     * Geocodificación inversa: obtiene la ubicación a partir de coordenadas.
     *
     * @param latitude  latitud en grados decimales
     * @param longitude longitud en grados decimales
     * @return la ubicación correspondiente
     * @throws IllegalArgumentException si no hay coincidencias
     */
    @Tool(name = "reverse_geocode",
          value = "Obtiene la ciudad a partir de una latitud y longitud.")
    public GeoLocation reverseGeocode(
            @P("Latitud") double latitude, @P("Longitud") double longitude) {
        return geocodingUseCase.reverse(latitude, longitude)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la ubicación"));
    }

    private static WeatherToolResult toToolResult(String location, WeatherReport report) {
        return new WeatherToolResult(
                location,
                report.timezone(),
                "metric",
                report.current(),
                report.daily(),
                ATTRIBUTION);
    }

    private static int clampDays(int days) {
        if (days < 1) {
            return 1;
        }
        return Math.min(days, MAX_DAYS);
    }
}
