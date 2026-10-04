package pe.com.incoda.demoquarkuslangchaing4j.domain.model;

import java.util.Map;

/**
 * Códigos meteorológicos WMO devueltos por Open-Meteo como números, mapeados a
 * descripciones legibles. Utilidad de dominio pura, sin dependencias de framework.
 */
public final class WeatherCode {

    private static final Map<Integer, String> DESCRIPTIONS = Map.ofEntries(
            Map.entry(0, "Clear sky"),
            Map.entry(1, "Mainly clear"),
            Map.entry(2, "Partly cloudy"),
            Map.entry(3, "Overcast"),
            Map.entry(45, "Fog"),
            Map.entry(48, "Depositing rime fog"),
            Map.entry(51, "Light drizzle"),
            Map.entry(53, "Moderate drizzle"),
            Map.entry(55, "Dense drizzle"),
            Map.entry(56, "Light freezing drizzle"),
            Map.entry(57, "Dense freezing drizzle"),
            Map.entry(61, "Slight rain"),
            Map.entry(63, "Moderate rain"),
            Map.entry(65, "Heavy rain"),
            Map.entry(66, "Light freezing rain"),
            Map.entry(67, "Heavy freezing rain"),
            Map.entry(71, "Slight snowfall"),
            Map.entry(73, "Moderate snowfall"),
            Map.entry(75, "Heavy snowfall"),
            Map.entry(77, "Snow grains"),
            Map.entry(80, "Slight rain showers"),
            Map.entry(81, "Moderate rain showers"),
            Map.entry(82, "Violent rain showers"),
            Map.entry(85, "Slight snow showers"),
            Map.entry(86, "Heavy snow showers"),
            Map.entry(95, "Thunderstorm"),
            Map.entry(96, "Thunderstorm with slight hail"),
            Map.entry(97, "Heavy thunderstorm"),
            Map.entry(99, "Thunderstorm with heavy hail"));

    private WeatherCode() {
    }

    /**
     * Traduce un código WMO a su descripción legible.
     *
     * @param code código meteorológico WMO
     * @return la descripción asociada o {@code "Unknown"} si el código no se reconoce
     */
    public static String describe(int code) {
        return DESCRIPTIONS.getOrDefault(code, "Unknown");
    }
}
