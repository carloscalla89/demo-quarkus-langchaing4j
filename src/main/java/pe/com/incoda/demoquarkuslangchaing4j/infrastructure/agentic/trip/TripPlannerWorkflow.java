package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.trip;

import dev.langchain4j.agentic.declarative.SequenceAgent;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.service.MemoryId;

/**
 * Workflow secuencial E1 (Trip Planner). Encadena la resolución del destino, el
 * pronóstico, la propuesta de actividades y la composición del itinerario.
 *
 * <p>Los parámetros del método se escriben en el {@code AgenticScope} con su
 * nombre; cada sub-agente lee lo que necesita y escribe su salida bajo su
 * {@code outputKey}.
 */
public interface TripPlannerWorkflow {

    /**
     * Planifica un viaje.
     *
     * @param sessionId   identificador de sesión (memoria de chat)
     * @param destination destino del viaje
     * @param days        número de días
     * @param preferences preferencias del viajero
     * @return el itinerario junto al {@code AgenticScope}
     */
    @SequenceAgent(outputKey = "itinerary",
            subAgents = { DestinationResolverAgent.class, ForecastAgent.class,
                    ActivityPlannerAgent.class, ItineraryComposerAgent.class })
    ResultWithAgenticScope<String> plan(@MemoryId String sessionId, String destination, int days, String preferences);
}
