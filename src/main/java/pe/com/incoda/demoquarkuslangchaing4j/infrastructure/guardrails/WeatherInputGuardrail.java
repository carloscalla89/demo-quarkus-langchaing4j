package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.guardrails;

import java.util.Locale;
import java.util.Set;

import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailRequest;
import dev.langchain4j.guardrail.InputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Guardrail de entrada del asistente meteorológico. Valida que la pregunta no esté
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

    /** Palabras clave que indican una consulta de clima o ubicación. */
    private static final Set<String> KEYWORDS = Set.of(
            // clima (es)
            "clima", "tiempo", "temperatura", "lluvia", "pronóstico", "pronostico",
            "viento", "humedad", "grados", "frío", "frio", "calor", "sol", "cielo",
            "tormenta", "nieve", "nublado",
            // ubicación (es)
            "coordenada", "coordenadas", "ubicación", "ubicacion", "latitud", "longitud",
            "dónde", "donde",
            // clima (en)
            "weather", "temperature", "rain", "forecast", "wind", "humidity",
            "degrees", "cold", "hot", "sun", "sky", "storm", "snow", "cloud",
            // ubicación (en)
            "coordinates", "location", "latitude", "longitude", "where");

    /**
     * Valida la pregunta del usuario.
     *
     * @param request petición con el mensaje del usuario
     * @return resultado de la validación
     */
    @Override
    public InputGuardrailResult validate(InputGuardrailRequest request) {
        String text = request.userMessage() == null ? null : request.userMessage().singleText();
        if (text == null || text.isBlank()) {
            return failure("La pregunta no puede estar vacía.");
        }
        if (text.length() > MAX_LENGTH) {
            return failure("La pregunta es demasiado larga (máximo " + MAX_LENGTH + " caracteres).");
        }
        String lower = text.toLowerCase(Locale.ROOT);
        boolean relevant = KEYWORDS.stream().anyMatch(lower::contains);
        if (!relevant) {
            return failure("Solo puedo responder preguntas relacionadas con el clima o ubicaciones.");
        }
        return success();
    }
}
