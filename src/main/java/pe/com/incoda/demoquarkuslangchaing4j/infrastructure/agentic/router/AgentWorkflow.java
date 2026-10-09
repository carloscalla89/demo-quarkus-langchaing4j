package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.router;

import dev.langchain4j.agentic.declarative.SequenceAgent;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.service.MemoryId;

import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.common.IntentRouterAgent;

/**
 * Workflow secuencial E2 (asistente agentic). Primero clasifica la intención y
 * luego enruta al experto correspondiente.
 *
 * <p>Devuelve {@link ResultWithAgenticScope} para exponer la intención detectada
 * y los agentes invocados junto a la respuesta.
 */
public interface AgentWorkflow {

    /**
     * Atiende una petición enrutándola al experto adecuado.
     *
     * @param sessionId identificador de sesión (memoria de chat)
     * @param request   petición del usuario
     * @return la respuesta junto al {@code AgenticScope}
     */
    @SequenceAgent(outputKey = "response",
            subAgents = { IntentRouterAgent.class, ExpertDispatcher.class })
    ResultWithAgenticScope<String> ask(@MemoryId String sessionId, String request);
}
