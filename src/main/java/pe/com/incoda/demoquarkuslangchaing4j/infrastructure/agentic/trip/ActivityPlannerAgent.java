package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.trip;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.RetrievalAugmentorSupplier;
import dev.langchain4j.agentic.declarative.ToolsSupplier;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.agentic.runtime.CdiBean;

import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.support.NoOpRetrievalAugmentor;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.tools.TravelTools;

/**
 * Tercer paso del planificador de viajes (E1): propone actividades turísticas
 * usando {@link TravelTools}.
 */
public interface ActivityPlannerAgent {

    /**
     * Propone actividades para el destino según el clima y las preferencias.
     *
     * @param location    ubicación resuelta
     * @param weather     resumen del clima
     * @param preferences preferencias del viajero
     * @return lista de actividades sugeridas
     */
    @UserMessage("""
            Propón actividades para el viaje usando la herramienta suggest_activity.
            Destino: '{{location}}'
            Clima: '{{weather}}'
            Preferencias: '{{preferences}}'
            Devuelve una lista breve y clara de actividades.
            """)
    @Agent(description = "Propone actividades turísticas para el destino", outputKey = "activities")
    String activities(String location, String weather, String preferences);

    /**
     * Proporciona las tools de viaje al agente.
     *
     * @param travelTools bean CDI con las tools de viaje
     * @return objetos con métodos {@code @Tool}
     */
    @ToolsSupplier
    static Object[] tools(@CdiBean TravelTools travelTools) {
        return new Object[] { travelTools };
    }

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
