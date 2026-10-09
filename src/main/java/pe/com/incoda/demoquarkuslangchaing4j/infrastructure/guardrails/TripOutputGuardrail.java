package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.guardrails;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.guardrail.OutputGuardrail;
import dev.langchain4j.guardrail.OutputGuardrailException;
import dev.langchain4j.guardrail.OutputGuardrailRequest;
import dev.langchain4j.guardrail.OutputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Guardrail de salida del planificador de viajes agentic. Comprueba que el
 * itinerario no esté vacío.
 *
 * <p><strong>Nota de implementación:</strong> al igual que el guardrail de
 * entrada, se invoca explícitamente mediante {@link #check(String)} porque la
 * extensión agentic no procesa {@code @OutputGuardrails}. Si falla, el
 * {@code OutputGuardrailExceptionMapper} existente lo traduce a HTTP 502.
 */
@ApplicationScoped
public class TripOutputGuardrail implements OutputGuardrail {

    /**
     * Valida el itinerario como guardrail LangChain4j.
     *
     * @param request petición con la respuesta del modelo
     * @return resultado de la validación
     */
    @Override
    public OutputGuardrailResult validate(OutputGuardrailRequest request) {
        AiMessage message = request.responseFromLLM() == null ? null : request.responseFromLLM().aiMessage();
        String text = message == null ? null : message.text();
        try {
            check(text);
            return success();
        } catch (OutputGuardrailException e) {
            return retry("El itinerario no puede estar vacío.");
        }
    }

    /**
     * Valida el itinerario y lanza {@link OutputGuardrailException} si está vacío.
     *
     * @param itinerary texto generado por el workflow
     * @throws OutputGuardrailException si el itinerario está vacío
     */
    public void check(String itinerary) {
        if (itinerary == null || itinerary.isBlank()) {
            throw new OutputGuardrailException("El itinerario generado está vacío.");
        }
    }
}
