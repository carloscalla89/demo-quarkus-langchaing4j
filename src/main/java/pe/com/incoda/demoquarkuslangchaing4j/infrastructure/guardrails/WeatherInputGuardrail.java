package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.guardrails;

import java.util.Locale;
import java.util.Set;

import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailRequest;
import dev.langchain4j.guardrail.InputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Guardrail de entrada del agente meteorológico. Valida que la pregunta no esté
 * vacía, no sea excesivamente larga y trate sobre clima o ubicaciones. Es una
 * heurística por palabras clave (no invoca otro LLM) para mantener el coste bajo.
 *
 * <p>Si falla, LangChain4j lanza una {@code InputGuardrailException} que el
 * {@code InputGuardrailExceptionMapper} traduce a HTTP 400.
 */
@ApplicationScoped
public class WeatherInputGuardrail implements InputGuardrail {

    /** Longitud máxima admitida para la pregunta. */
    private static final int MAX_LENGTH = 500;

    /**
     * Marca del bloque de instrucciones que el framework agentic añade al mensaje
     * de usuario de los agentes que devuelven un enum. Debe excluirse de la
     * validación: contiene los nombres de las categorías ("WEATHER", "TRIP"), que
     * colisionan con las palabras clave del dominio.
     */
    private static final String ENUM_INSTRUCTIONS_MARKER = "You must answer strictly";

    /** Palabras clave que indican una consulta de clima o ubicación. */
    private static final Set<String> KEYWORDS = Set.of(
            // clima (es)
            "clima", "tiempo", "temperatura", "lluvia", "pronóstico", "pronostico",
            "viento", "humedad", "grados", "frío", "frio", "calor", "sol", "cielo",
            "tormenta", "nieve", "nublado",
            // ubicación (es)
            "coordenada", "coordenadas", "ubicación", "ubicacion", "latitud", "longitud",
            "dónde", "donde",
            // viajes (es)
            "viaje", "viajar", "viajero", "itinerario", "planifica", "planificar",
            "actividad", "actividades", "turismo", "visita", "visitar", "destino",
            // clima (en)
            "weather", "temperature", "rain", "forecast", "wind", "humidity",
            "degrees", "cold", "hot", "sun", "sky", "storm", "snow", "cloud",
            // ubicación (en)
            "coordinates", "location", "latitude", "longitude", "where",
            // viajes (en)
            "trip", "travel", "itinerary", "activities", "visit", "destination");

    /**
     * Valida la pregunta del usuario.
     *
     * @param request petición con el mensaje del usuario
     * @return resultado de la validación
     */
    @Override
    public InputGuardrailResult validate(InputGuardrailRequest request) {
        String raw = request.userMessage() == null ? null : request.userMessage().singleText();
        if (raw == null || raw.isBlank()) {
            return failure("La pregunta no puede estar vacía.");
        }
        String text = requestText(raw);
        if (text.length() > MAX_LENGTH) {
            return failure("La pregunta es demasiado larga (máximo " + MAX_LENGTH + " caracteres).");
        }
        String lower = text.toLowerCase(Locale.ROOT);
        boolean relevant = KEYWORDS.stream().anyMatch(lower::contains);
        if (!relevant) {
            return failure("Solo puedo responder preguntas relacionadas con el clima, ubicaciones o viajes.");
        }
        return success();
    }

    /**
     * Aísla la petición del usuario del bloque de instrucciones de formato que el
     * framework agentic añade a los agentes que devuelven un enum.
     *
     * @param raw mensaje de usuario tal y como lo recibe el guardrail
     * @return la petición del usuario, sin las instrucciones del framework
     */
    private static String requestText(String raw) {
        int marker = raw.indexOf(ENUM_INSTRUCTIONS_MARKER);
        String candidate = marker >= 0 ? raw.substring(0, marker) : raw;
        candidate = candidate.strip();
        return candidate.isBlank() ? raw.strip() : candidate;
    }
}
