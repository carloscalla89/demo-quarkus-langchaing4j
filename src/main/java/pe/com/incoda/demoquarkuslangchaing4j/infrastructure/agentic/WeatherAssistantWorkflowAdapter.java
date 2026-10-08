package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import dev.langchain4j.agentic.scope.AgentInvocation;
import dev.langchain4j.agentic.scope.AgenticScope;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;

import pe.com.incoda.demoquarkuslangchaing4j.domain.model.AssistantAnswer;
import pe.com.incoda.demoquarkuslangchaing4j.domain.service.weather.WeatherAssistant;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.router.AssistantWorkflow;

/**
 * Adaptador de infraestructura que implementa el puerto de dominio
 * {@link WeatherAssistant} delegando en el workflow agentic
 * {@link AssistantWorkflow} y traduciendo su {@code AgenticScope} a
 * {@link AssistantAnswer}.
 */
@ApplicationScoped
public class WeatherAssistantWorkflowAdapter implements WeatherAssistant {

    private final AssistantWorkflow assistantWorkflow;

    @Inject
    public WeatherAssistantWorkflowAdapter(AssistantWorkflow assistantWorkflow) {
        this.assistantWorkflow = assistantWorkflow;
    }

    /**
     * Ejecuta el workflow agentic y extrae la intención y los agentes usados.
     *
     * @param sessionId identificador de sesión
     * @param question  pregunta del usuario
     * @return respuesta, intención y agentes invocados
     */
    @Override
    public AssistantAnswer ask(String sessionId, String question) {
        ResultWithAgenticScope<String> result = assistantWorkflow.ask(question);
        AgenticScope scope = result.agenticScope();

        Object intent = scope == null ? null : scope.readState("intent");
        List<String> agentsUsed = (scope == null || scope.agentInvocations() == null)
                ? List.of()
                : scope.agentInvocations().stream()
                        .map(AgentInvocation::agentName)
                        .distinct()
                        .toList();

        return new AssistantAnswer(result.result(), intent == null ? null : intent.toString(), agentsUsed);
    }
}
