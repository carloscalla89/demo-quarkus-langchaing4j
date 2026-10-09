package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.guardrails;

import java.util.Locale;
import java.util.Set;

import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailException;
import dev.langchain4j.guardrail.InputGuardrailRequest;
import dev.langchain4j.guardrail.InputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Guardrail de entrada del flujo agentic. Acepta peticiones relacionadas con
 * clima, ubicación/geocodificación o viajes, y rechaza el resto.
 *
 * <p><strong>Nota de implementación:</strong> la extensión agentic no procesa
 * {@code @InputGuardrails} sobre métodos de agente/workflow, por lo que este
 * guardrail se invoca explícitamente desde los adaptadores mediante
 * {@link #check(String)}. Al lanzar {@link InputGuardrailException}, el
 * {@code InputGuardrailExceptionMapper} existente lo traduce a HTTP 400.
 *
 * <p>Se distingue de {@link WeatherInputGuardrail} (que rechazaría peticiones de
 * viaje) para no interferir con el asistente meteorológico.
 */
@ApplicationScoped
public class AgenticInputGuardrail implements InputGuardrail {

    /** Longitud máxima admitida para la petición. */
    private static final int MAX_LENGTH = 500;

    /** Palabras clave que indican una consulta válida para el flujo agentic. */
    private static final Set<String> KEYWORDS = Set.of(
            // clima (es)
            "clima", "tiempo", "temperatura", "lluvia", "pronóstico", "pronostico",
            "viento", "humedad", "grados", "frío", "frio", "calor", "sol", "cielo",
            "tormenta", "nieve", "nublado",
            // ubicación (es)
            "coordenada", "coordenadas", "ubicación", "ubicacion", "latitud", "longitud",
            "dónde", "donde", "lugar",
            // viaje (es)
            "viaje", "viajar", "itinerario", "planifica", "planificar", "planear",
            "ruta", "recorrido", "destino", "actividades", "vacaciones", "turismo",
            // clima (en)
            "weather", "temperature", "rain", "forecast", "wind", "humidity",
            "degrees", "cold", "hot", "sun", "sky", "storm", "snow", "cloud",
            // ubicación (en)
            "coordinates", "location", "latitude", "longitude", "where",
            // viaje (en)
            "trip", "travel", "itinerary", "plan", "route", "activities", "vacation");

    /**
     * Valida la petición como guardrail LangChain4j.
     *
     * @param request petición con el mensaje del usuario
     * @return resultado de la validación
     */
    @Override
    public InputGuardrailResult validate(InputGuardrailRequest request) {
        String text = request.userMessage() == null ? null : request.userMessage().singleText();
        try {
            check(text);
            return success();
        } catch (InputGuardrailException e) {
            return failure(e.getMessage());
        }
    }

    /**
     * Valida la petición del usuario y lanza {@link InputGuardrailException} si no
     * es válida. Se invoca explícitamente desde los adaptadores del flujo agentic.
     *
     * @param text petición del usuario
     * @throws InputGuardrailException si la petición está vacía, es demasiado larga
     *                                 o no está relacionada con clima, ubicación o viajes
     */
    public void check(String text) {
        if (text == null || text.isBlank()) {
            throw new InputGuardrailException("La petición no puede estar vacía.");
        }
        if (text.length() > MAX_LENGTH) {
            throw new InputGuardrailException(
                    "La petición es demasiado larga (máximo " + MAX_LENGTH + " caracteres).");
        }
        String lower = text.toLowerCase(Locale.ROOT);
        if (KEYWORDS.stream().noneMatch(lower::contains)) {
            throw new InputGuardrailException(
                    "Solo puedo responder sobre clima, ubicaciones o planificación de viajes.");
        }
    }
}
