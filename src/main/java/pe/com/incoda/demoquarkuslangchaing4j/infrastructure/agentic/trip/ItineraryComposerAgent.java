package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.trip;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.RetrievalAugmentorSupplier;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.support.NoOpRetrievalAugmentor;

/**
 * Agente hoja (E1) que compone el itinerario final a partir del destino, el
 * clima y las actividades. No usa tools: solo razona con el contexto del
 * {@code AgenticScope} y escribe el resultado bajo la clave {@code itinerary}.
 */
public interface ItineraryComposerAgent {

    /**
     * Redacta el itinerario final del viaje.
     *
     * @param request    petición original del usuario (incluye días y preferencias)
     * @param location   destino resuelto
     * @param weather    clima esperado
     * @param activities actividades propuestas
     * @return itinerario en texto
     */
    @SystemMessage("""
        Eres un planificador de viajes. Compón un itinerario claro y realista.
        Usa solo la información del contexto: destino, clima y actividades.
        Incluye la atribución "Datos meteorológicos de Open-Meteo.com".
        No inventes lugares que no aparezcan en el contexto.
        """)
    @UserMessage("""
        Destino: {location}
        Clima esperado: {weather}
        Actividades propuestas: {activities}
        Petición del usuario: {request}

        Redacta el itinerario (día a día si el usuario indica días) en español.
        """)
    @Agent(description = "Compone el itinerario final del viaje.", outputKey = "itinerary")
    String compose(String request, String location, String weather, String activities);

    @RetrievalAugmentorSupplier
    static RetrievalAugmentor retrievalAugmentor() {
        return NoOpRetrievalAugmentor.INSTANCE;
    }
}
