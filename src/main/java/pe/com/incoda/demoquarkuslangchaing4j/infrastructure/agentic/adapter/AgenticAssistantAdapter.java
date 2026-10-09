package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.adapter;

import java.util.List;

import dev.langchain4j.agentic.scope.AgentInvocation;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import pe.com.incoda.demoquarkuslangchaing4j.domain.model.agentic.AgenticAnswer;
import pe.com.incoda.demoquarkuslangchaing4j.domain.model.agentic.RequestIntent;
import pe.com.incoda.demoquarkuslangchaing4j.domain.service.agentic.AgenticAssistant;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.router.AgentWorkflow;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.guardrails.AgenticInputGuardrail;

/**
 * Adaptador que implementa el puerto de dominio {@link AgenticAssistant}
 * delegando en el workflow agentic {@link AgentWorkflow} y extrayendo del
 * {@code AgenticScope} la intención detectada y los agentes invocados.
 */
@ApplicationScoped
public class AgenticAssistantAdapter implements AgenticAssistant {

    private final AgentWorkflow workflow;
    private final AgenticInputGuardrail inputGuardrail;

    @Inject
    public AgenticAssistantAdapter(AgentWorkflow workflow, AgenticInputGuardrail inputGuardrail) {
        this.workflow = workflow;
        this.inputGuardrail = inputGuardrail;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AgenticAnswer ask(String sessionId, String request) {
        inputGuardrail.check(request);

        ResultWithAgenticScope<String> result = workflow.ask(sessionId, request);

        RequestIntent intent = result.agenticScope().readState("intent", RequestIntent.UNKNOWN);

        List<String> agentsUsed = result.agenticScope().agentInvocations().stream()
                .map(AgentInvocation::agentName)
                .distinct()
                .toList();

        return new AgenticAnswer(result.result(), intent, agentsUsed);
    }
}
