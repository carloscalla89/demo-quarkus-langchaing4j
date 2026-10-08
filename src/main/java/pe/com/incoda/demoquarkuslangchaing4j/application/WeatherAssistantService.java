package pe.com.incoda.demoquarkuslangchaing4j.application;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import pe.com.incoda.demoquarkuslangchaing4j.domain.dto.WeatherAgentAnswerDto;
import pe.com.incoda.demoquarkuslangchaing4j.domain.dto.WeatherQuestionDto;
import pe.com.incoda.demoquarkuslangchaing4j.domain.model.AssistantAnswer;
import pe.com.incoda.demoquarkuslangchaing4j.domain.service.weather.WeatherAssistant;

/**
 * Servicio de aplicación que envuelve el asistente agentic (puerto de dominio
 * {@link WeatherAssistant}) y traduce su {@link AssistantAnswer} a una respuesta
 * REST estructurada ({@link WeatherAgentAnswerDto}).
 */
@ApplicationScoped
public class WeatherAssistantService {

    private final WeatherAssistant weatherAssistant;

    @Inject
    public WeatherAssistantService(WeatherAssistant weatherAssistant) {
        this.weatherAssistant = weatherAssistant;
    }

    /**
     * Envía la pregunta al workflow agentic y estructura la respuesta.
     *
     * @param request pregunta y opciones del usuario
     * @return respuesta estructurada con la intención y los agentes usados
     */
    public WeatherAgentAnswerDto ask(WeatherQuestionDto request) {
        String sessionId = (request.sessionId() == null || request.sessionId().isBlank())
                ? UUID.randomUUID().toString()
                : request.sessionId();

        AssistantAnswer answer = weatherAssistant.ask(sessionId, buildUserMessage(request));

        return WeatherAgentAnswerDto.from(answer.answer(), answer.intent(), answer.agentsUsed());
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
