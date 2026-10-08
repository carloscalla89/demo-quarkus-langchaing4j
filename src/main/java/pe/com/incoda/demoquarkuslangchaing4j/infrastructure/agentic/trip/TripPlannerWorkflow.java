package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.trip;

import dev.langchain4j.agentic.declarative.Output;
import dev.langchain4j.agentic.declarative.SequenceAgent;

/**
 * E1 — Workflow secuencial de planificación de viajes.
 *
 * <p>Encadena la resolución del destino, el pronóstico, la propuesta de
 * actividades y la composición del itinerario compartiendo un único
 * {@code AgenticScope}. El resultado final (el itinerario) se escribe bajo la
 * clave {@code response} para poder anidarse dentro de
 * {@code AssistantWorkflow} (E2).
 */
public interface TripPlannerWorkflow {

    /**
     * Planifica un viaje a partir de una petición en lenguaje natural.
     *
     * @param request petición del usuario (destino, días y preferencias)
     * @return el itinerario generado
     */
    @SequenceAgent(
            outputKey = "response",
            subAgents = {
                    DestinationResolverAgent.class,
                    ForecastAgent.class,
                    ActivityPlannerAgent.class,
                    ItineraryComposerAgent.class })
    String plan(String request);

    /**
     * Extrae el itinerario del {@code AgenticScope} como resultado del workflow.
     *
     * @param itinerary salida de {@link ItineraryComposerAgent} (clave {@code itinerary})
     * @return el itinerario final
     */
    @Output
    static String output(String itinerary) {
        return itinerary;
    }
}
