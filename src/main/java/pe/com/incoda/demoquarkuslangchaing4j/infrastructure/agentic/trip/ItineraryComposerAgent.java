package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.trip;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.RetrievalAugmentorSupplier;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.service.UserMessage;

import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.support.NoOpRetrievalAugmentor;

/**
 * Cuarto y último paso del planificador de viajes (E1): compone el itinerario
 * final a partir de la ubicación, el clima y las actividades. No usa tools.
 */
public interface ItineraryComposerAgent {

    /**
     * Redacta el itinerario final.
     *
     * @param location    ubicación resuelta
     * @param weather     resumen del clima
     * @param activities  actividades propuestas
     * @param days        número de días
     * @param preferences preferencias del viajero
     * @return itinerario en lenguaje natural
     */
    @UserMessage("""
            Redacta un itinerario de viaje de {{days}} días para '{{location}}'.
            Usa el pronóstico del clima: '{{weather}}'.
            Usa estas actividades: '{{activities}}'.
            Preferencias del viajero: '{{preferences}}'.
            Estructura el itinerario por días y sé conciso.
            """)
    @Agent(description = "Compone el itinerario final del viaje", outputKey = "itinerary")
    String compose(String location, String weather, String activities, int days, String preferences);

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
