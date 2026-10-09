package pe.com.incoda.demoquarkuslangchaing4j.application.agentic;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import pe.com.incoda.demoquarkuslangchaing4j.domain.dto.agentic.AgenticAssistantAnswerDto;
import pe.com.incoda.demoquarkuslangchaing4j.domain.dto.agentic.AgenticAssistantRequestDto;
import pe.com.incoda.demoquarkuslangchaing4j.domain.model.agentic.AgenticAnswer;
import pe.com.incoda.demoquarkuslangchaing4j.domain.service.agentic.AgenticAssistant;

/**
 * Servicio de aplicación que envuelve el asistente agentic enrutador (E2) y
 * traduce su resultado de dominio a un DTO REST.
 */
@ApplicationScoped
public class AgenticAssistantService {

    private final AgenticAssistant agenticAssistant;

    @Inject
    public AgenticAssistantService(AgenticAssistant agenticAssistant) {
        this.agenticAssistant = agenticAssistant;
    }

    /**
     * Atiende una petición enrutándola al experto adecuado.
     *
     * @param request petición, sesión e idioma
     * @return respuesta con la intención detectada y los agentes usados
     */
    public AgenticAssistantAnswerDto ask(AgenticAssistantRequestDto request) {
        String sessionId = (request.sessionId() == null || request.sessionId().isBlank())
                ? UUID.randomUUID().toString()
                : request.sessionId();

        AgenticAnswer answer = agenticAssistant.ask(sessionId, request.request());

        List<String> agentsUsed = answer.agentsUsed() == null ? List.of() : answer.agentsUsed();
        return new AgenticAssistantAnswerDto(
                answer.answer(),
                answer.intent() == null ? null : answer.intent().name(),
                agentsUsed,
                LocalDateTime.now().toString());
    }
}
