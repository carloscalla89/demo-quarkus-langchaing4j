package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.tools;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Herramientas de apoyo al trabajo agentic para sugerir actividades turísticas.
 *
 * <p>A diferencia de {@link WeatherTools}, no llama a servicios externos: es una
 * función determinista y sin red, lo que la hace ideal para probar el flujo
 * agentic sin coste ni dependencias. El LLM decide cuándo invocarla; el método
 * {@code suggest_activity} construye una lista de ideas a partir del destino, el
 * clima y las preferencias.
 */
@ApplicationScoped
public class TravelTools {

    /**
     * Sugiere actividades turísticas según el destino, el clima y las preferencias.
     *
     * @param destination ciudad o destino del viaje
     * @param weather     resumen del clima (opcional); si indica lluvia se priorizan planes bajo techo
     * @param preferences preferencias del viajero (opcional), p. ej. cultura, gastronomía o naturaleza
     * @return lista de actividades sugeridas (sin repetir)
     */
    @Tool(name = "suggest_activity",
          value = "Sugiere actividades turísticas para un destino según el clima y las preferencias. "
                + "Úsala al planificar un viaje.")
    public List<String> suggestActivity(
            @P("Ciudad o destino del viaje") String destination,
            @P(value = "Resumen del clima del destino", required = false) String weather,
            @P(value = "Preferencias del viajero (cultura, gastronomía, naturaleza, aventura...)", required = false)
            String preferences) {

        String place = (destination == null || destination.isBlank()) ? "el destino" : destination.trim();
        String prefs = preferences == null ? "" : preferences.toLowerCase(Locale.ROOT);
        String sky = weather == null ? "" : weather.toLowerCase(Locale.ROOT);
        boolean rainy = containsAny(sky, "lluvia", "rain", "tormenta", "storm", "precipit", "nieve", "snow");

        Set<String> activities = new LinkedHashSet<>();
        activities.add("Recorrer el centro histórico de " + place);
        activities.add("Probar la gastronomía típica de " + place);

        if (containsAny(prefs, "cultura", "historia", "museo", "arte")) {
            activities.add("Visitar los principales museos y monumentos de " + place);
            activities.add("Hacer un free walking tour histórico por " + place);
        }
        if (containsAny(prefs, "gastronomía", "gastronomia", "comida", "food")) {
            activities.add("Tour gastronómico por mercados locales de " + place);
        }
        if (containsAny(prefs, "naturaleza", "montaña", "montana", "senderismo", "naturaleza")) {
            activities.add("Excursión a miradores y áreas naturales cercanas a " + place);
        }
        if (containsAny(prefs, "aventura", "adrenalina")) {
            activities.add("Actividad de aventura (trekking o canotaje) cerca de " + place);
        }
        if (containsAny(prefs, "compras", "shopping", "artesanía", "artesania")) {
            activities.add("Recorrer ferias de artesanía y mercados de " + place);
        }

        if (rainy) {
            activities.add("Visitar un museo o centro cultural a cubierto en " + place);
        } else {
            activities.add("Disfrutar de una plaza o parque al aire libre en " + place);
        }

        return List.copyOf(activities);
    }

    private static boolean containsAny(String text, String... needles) {
        for (String needle : needles) {
            if (text.contains(needle)) {
                return true;
            }
        }
        return false;
    }
}
