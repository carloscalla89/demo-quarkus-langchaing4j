package pe.com.incoda.demoquarkuslangchaing4j.domain.dto.agentic;

import java.util.List;

/**
 * Contrato de salida REST del asistente agentic enrutador (E2).
 *
 * @param answer      respuesta en lenguaje natural del experto seleccionado
 * @param intent      intención detectada ({@code WEATHER}, {@code GEOCODING}, {@code TRIP} o {@code UNKNOWN})
 * @param agentsUsed  nombres de los agentes que participaron
 * @param generatedAt instante en que se generó la respuesta
 */
public record AgenticAssistantAnswerDto(
        String answer,
        String intent,
        List<String> agentsUsed,
        String generatedAt) {
}
