package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.router;

import dev.langchain4j.agentic.declarative.SequenceAgent;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;

import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.common.IntentRouterAgent;

/**
 * E2 — Workflow raíz del asistente agentic. Encadena la clasificación de
 * intención ({@link IntentRouterAgent}) con el enrutador condicional
 * ({@link ExpertDispatcher}).
 *
 * <p>Devuelve {@link ResultWithAgenticScope} para poder exponer la intención
 * detectada y los agentes invocados junto a la respuesta.
 */
public interface AssistantWorkflow {

    /**
     * Atiende una petición en lenguaje natural de principio a fin.
     *
     * @param request petición del usuario
     * @return la respuesta junto con el {@code AgenticScope} de la ejecución
     */
    @SequenceAgent(
            outputKey = "response",
            subAgents = { IntentRouterAgent.class, ExpertDispatcher.class })
    ResultWithAgenticScope<String> ask(String request);
}
