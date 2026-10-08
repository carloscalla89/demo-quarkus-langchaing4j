package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.guardrails;

import java.util.Locale;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.guardrail.OutputGuardrail;
import dev.langchain4j.guardrail.OutputGuardrailRequest;
import dev.langchain4j.guardrail.OutputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Guardrail de salida del asistente meteorológico. Comprueba que la respuesta no
 * esté vacía y que incluya la atribución obligatoria de Open-Meteo. Si falla,
 * pide al modelo que reintente con {@code retry}.
 */
@ApplicationScoped
public class WeatherOutputGuardrail implements OutputGuardrail {

    /** Fragmento que debe aparecer en la respuesta (comparado en minúsculas). */
    private static final String ATTRIBUTION_MARKER = "open-meteo";

    /**
     * Valida la respuesta generada por el modelo.
     *
     * @param request petición con la respuesta del modelo
     * @return resultado de la validación
     */
    @Override
    public OutputGuardrailResult validate(OutputGuardrailRequest request) {
        AiMessage message = request.responseFromLLM() == null ? null : request.responseFromLLM().aiMessage();
        String text = message == null ? null : message.text();
        if (text == null || text.isBlank()) {
            return retry("La respuesta está vacía; responde con el resumen del clima.");
        }
        if (!text.toLowerCase(Locale.ROOT).contains(ATTRIBUTION_MARKER)) {
            return retry("Falta la atribución 'Datos meteorológicos de Open-Meteo.com'.");
        }
        return success();
    }
}
