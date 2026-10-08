package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.common;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.RetrievalAugmentorSupplier;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.support.NoOpRetrievalAugmentor;

/**
 * E2 — Experto de reserva. Responde peticiones no soportadas ({@code UNKNOWN})
 * con un mensaje que orienta al usuario sobre lo que sí puede hacer.
 */
public interface FallbackExpertAgent {

    /**
     * Responde una petición fuera de las intenciones soportadas.
     *
     * @param request petición del usuario
     * @return mensaje orientativo
     */
    @SystemMessage("""
        Eres un asistente de un servicio meteorológico. La petición del usuario no
        corresponde a clima, geocodificación ni planificación de viajes.
        Responde en el idioma de la petición (por defecto español), en 2 líneas,
        indicando que solo puedes ayudar con el clima, coordenadas o planes de viaje.
        """)
    @UserMessage("{request}")
    @Agent(description = "Responde peticiones no soportadas.", outputKey = "response")
    String answer(String request);

    @RetrievalAugmentorSupplier
    static RetrievalAugmentor retrievalAugmentor() {
        return NoOpRetrievalAugmentor.INSTANCE;
    }
}
