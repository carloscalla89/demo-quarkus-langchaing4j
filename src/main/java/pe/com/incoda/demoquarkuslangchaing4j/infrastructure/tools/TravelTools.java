package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Herramientas (function calling) de apoyo a la planificación de viajes. A
 * diferencia de {@link WeatherTools}, no consulta proveedores externos: aplica
 * una heurística determinista sobre el clima esperado y las preferencias del
 * viajero para proponer actividades.
 *
 * <p>Lo que el LLM ve es la descripción de {@code @Tool} y de {@code @P}; este
 * Javadoc es solo documentación para desarrolladores.
 */
@ApplicationScoped
public class TravelTools {

    /**
     * Sugiere actividades turísticas para un destino en función del clima y las
     * preferencias indicadas.
     *
     * @param city        ciudad o lugar del viaje
     * @param weather     resumen del clima esperado (p. ej. {@code "lluvia ligera, 18°C"})
     * @param preferences preferencias del viajero (p. ej. {@code "cultura, gastronomía"})
     * @return lista de actividades sugeridas, nunca vacía
     */
    @Tool(name = "suggest_activity",
          value = "Sugiere actividades turísticas para un destino según el clima esperado "
                + "y las preferencias del viajero. Devuelve una lista de actividades concretas.")
    public List<String> suggestActivity(
            @P("Ciudad o lugar del viaje") String city,
            @P(value = "Resumen del clima esperado (p. ej. 'lluvia ligera, 18°C')", required = false) String weather,
            @P(value = "Preferencias del viajero (p. ej. 'cultura, gastronomía')", required = false) String preferences) {

        String place = (city == null || city.isBlank()) ? "el destino" : city.trim();
        String climate = weather == null ? "" : weather.toLowerCase(Locale.ROOT);
        boolean indoor = climate.contains("lluvia") || climate.contains("tormenta")
                || climate.contains("nieve") || climate.contains("frío") || climate.contains("frio")
                || climate.contains("rain") || climate.contains("storm") || climate.contains("snow");
        boolean outdoor = climate.contains("despejado") || climate.contains("soleado")
                || climate.contains("calor") || climate.contains("clear") || climate.contains("sun");

        List<String> activities = new ArrayList<>();
        if (indoor) {
            activities.add("Museos y centros culturales de " + place);
            activities.add("Gastronomía local en mercados cubiertos de " + place);
        } else if (outdoor) {
            activities.add("Paseo por el centro histórico y miradores de " + place);
            activities.add("Parques y rutas al aire libre en " + place);
        } else {
            activities.add("Recorrido por el centro histórico de " + place);
            activities.add("Visita a mercados y puntos gastronómicos de " + place);
        }
        if (preferences != null && !preferences.isBlank()) {
            activities.add("Actividades de " + preferences.trim() + " en " + place);
        }
        return activities;
    }
}
