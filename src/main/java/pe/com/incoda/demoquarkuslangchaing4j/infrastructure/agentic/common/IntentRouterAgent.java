package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.common;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.RetrievalAugmentorSupplier;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.service.UserMessage;

import pe.com.incoda.demoquarkuslangchaing4j.domain.model.agentic.RequestIntent;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.support.NoOpRetrievalAugmentor;

/**
 * Agente enrutador: clasifica la petición del usuario en una de las intenciones
 * soportadas y la escribe en el {@code AgenticScope} bajo la clave {@code intent}.
 */
public interface IntentRouterAgent {

    /**
     * Clasifica la intención de la petición.
     *
     * @param request petición del usuario
     * @return la intención detectada
     */
    @UserMessage("""
            Clasifica la intención de la petición del usuario en una de estas categorías:
            - WEATHER: pregunta por el clima o el tiempo.
            - GEOCODING: pide coordenadas o la ubicación de un lugar.
            - TRIP: pide planificar un viaje, un itinerario o actividades.
            - UNKNOWN: cualquier otra cosa.
            Responde únicamente con la categoría, sin explicaciones.
            Petición: '{{request}}'
            """)
    @Agent(description = "Clasifica la intención del usuario", outputKey = "intent")
    RequestIntent classify(String request);

    /**
     * Excluye a este agente del {@code RetrievalAugmentor} global.
     *
     * @return augmentor no-op
     */
    @RetrievalAugmentorSupplier
    static RetrievalAugmentor retrievalAugmentor() {
        return new NoOpRetrievalAugmentor();
    }
}
