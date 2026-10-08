package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.common;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.RetrievalAugmentorSupplier;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.guardrail.InputGuardrails;

import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.support.NoOpRetrievalAugmentor;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.guardrails.WeatherInputGuardrail;

/**
 * E2 — Primer agente del workflow: clasifica la intención del usuario en
 * {@link RequestIntent}. Es el punto de entrada efectivo del sistema, por lo
 * que aquí se aplica el guardrail de entrada (validación de dominio).
 *
 * <p>Escribe el resultado en el {@code AgenticScope} bajo la clave
 * {@code intent}, que consumen las condiciones de activación de
 * {@code ExpertDispatcher}.
 */
public interface IntentRouterAgent {

    /**
     * Clasifica la intención de la petición del usuario.
     *
     * @param request petición del usuario (entrada del workflow)
     * @return la intención detectada
     */
    @SystemMessage("""
        Clasificas la intención de una petición de un asistente meteorológico.
        Categorías posibles:
        - WEATHER: pregunta por el clima o el tiempo de un lugar.
        - GEOCODING: pide coordenadas de un lugar, o el lugar de unas coordenadas.
        - TRIP: pide planificar un viaje, itinerario o actividades.
        - UNKNOWN: cualquier otra cosa.
        Responde ÚNICAMENTE con la categoría, sin explicaciones ni texto adicional.
        """)
    @UserMessage("{request}")
    @Agent(description = "Clasifica la intención del usuario.", outputKey = "intent")
    @InputGuardrails(WeatherInputGuardrail.class)
    RequestIntent classify(String request);

    @RetrievalAugmentorSupplier
    static RetrievalAugmentor retrievalAugmentor() {
        return NoOpRetrievalAugmentor.INSTANCE;
    }
}
