package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.trip;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.RetrievalAugmentorSupplier;
import dev.langchain4j.agentic.declarative.ToolsSupplier;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.agentic.runtime.CdiBean;

import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.support.NoOpRetrievalAugmentor;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.tools.WeatherTools;

/**
 * Primer paso del planificador de viajes (E1): resuelve el destino a una
 * ubicación concreta usando {@link WeatherTools#geocodeCity(String)}.
 */
public interface DestinationResolverAgent {

    /**
     * Resuelve el destino a una ubicación.
     *
     * @param destination destino indicado por el usuario
     * @return nombre de la ubicación elegida
     */
    @UserMessage("""
            Resuelve el destino indicado a una ubicación concreta usando la herramienta geocode_city.
            Devuelve únicamente el nombre de la ubicación elegida (ciudad, país).
            Destino: '{{destination}}'
            """)
    @Agent(description = "Resuelve el destino del viaje a una ubicación", outputKey = "location")
    String resolve(String destination);

    /**
     * Proporciona las tools de geocodificación al agente.
     *
     * @param weatherTools bean CDI con las tools
     * @return objetos con métodos {@code @Tool}
     */
    @ToolsSupplier
    static Object[] tools(@CdiBean WeatherTools weatherTools) {
        return new Object[] { weatherTools };
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
