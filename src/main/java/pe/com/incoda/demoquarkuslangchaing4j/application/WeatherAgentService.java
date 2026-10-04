package pe.com.incoda.demoquarkuslangchaing4j.application;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import dev.langchain4j.service.Result;
import dev.langchain4j.service.tool.ToolExecution;

import pe.com.incoda.demoquarkuslangchaing4j.domain.dto.WeatherAgentAnswerDto;
import pe.com.incoda.demoquarkuslangchaing4j.domain.dto.WeatherQuestionDto;
import pe.com.incoda.demoquarkuslangchaing4j.domain.model.WeatherToolResult;
import pe.com.incoda.demoquarkuslangchaing4j.domain.service.weather.WeatherAssistant;

/**
 * Servicio de aplicación que envuelve el asistente meteorológico (puerto de
 * dominio {@link WeatherAssistant}) y traduce su {@code Result} a una respuesta
 * REST estructurada ({@link WeatherAgentAnswerDto}).
 */
@ApplicationScoped
public class WeatherAgentService {

    private final WeatherAssistant agent;

    @Inject
    public WeatherAgentService(WeatherAssistant agent) {
        this.agent = agent;
    }

    /**
     * Envía la pregunta al agente meteorológico y estructura la respuesta.
     *
     * @param request pregunta y opciones del usuario
     * @return respuesta estructurada con el texto del modelo y los datos de las tools
     */
    public WeatherAgentAnswerDto ask(WeatherQuestionDto request) {
        String memoryId = (request.sessionId() == null || request.sessionId().isBlank())
                ? UUID.randomUUID().toString()
                : request.sessionId();

        Result<String> result = agent.ask(memoryId, buildUserMessage(request));

        List<String> toolsUsed = result.toolExecutions().stream()
                .map(execution -> execution.request().name())
                .distinct()
                .toList();

        WeatherToolResult toolResult = result.toolExecutions().stream()
                .map(ToolExecution::resultObject)
                .filter(WeatherToolResult.class::isInstance)
                .map(WeatherToolResult.class::cast)
                .reduce((first, second) -> second)
                .orElse(null);

        return WeatherAgentAnswerDto.from(result.content(), toolsUsed, toolResult);
    }

    /**
     * Construye el mensaje de usuario, añadiendo el contexto opcional recibido.
     *
     * @param request pregunta y opciones del usuario
     * @return mensaje de usuario enriquecido
     */
    private static String buildUserMessage(WeatherQuestionDto request) {
        StringBuilder message = new StringBuilder(request.question());
        List<String> hints = new ArrayList<>();
        if (request.city() != null && !request.city().isBlank()) {
            hints.add("ciudad=" + request.city());
        }
        if (request.days() != null) {
            hints.add("días=" + request.days());
        }
        if (request.language() != null && !request.language().isBlank()) {
            hints.add("idioma=" + request.language());
        }
        if (request.units() != null && !request.units().isBlank()) {
            hints.add("unidades=" + request.units());
        }
        if (!hints.isEmpty()) {
            message.append("\n[Contexto: ").append(String.join("; ", hints)).append("]");
        }
        return message.toString();
    }
}
