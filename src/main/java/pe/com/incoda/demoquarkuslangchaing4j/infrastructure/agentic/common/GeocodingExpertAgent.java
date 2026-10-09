package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.common;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.RetrievalAugmentorSupplier;
import dev.langchain4j.agentic.declarative.ToolsSupplier;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.agentic.runtime.CdiBean;

import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.support.NoOpRetrievalAugmentor;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.tools.WeatherTools;

/**
 * Experto en geocodificación del flujo agentic. Reutiliza {@link WeatherTools}
 * para resolver ubicaciones y coordenadas.
 */
public interface GeocodingExpertAgent {

    /**
     * Resuelve la ubicación o las coordenadas solicitadas.
     *
     * @param request petición del usuario
     * @return ubicación o coordenadas
     */
    @UserMessage("""
            Eres un experto en geocodificación. Usa la herramienta geocode_city (o reverse_geocode)
            para obtener las coordenadas o la ubicación solicitadas.
            Si hay varias coincidencias, menciónalas para que el usuario elija.
            Petición: '{{request}}'
            """)
    @Agent(description = "Experto en geocodificación", outputKey = "response")
    String answer(String request);

    /**
     * Proporciona las tools meteorológicas/geocodificación al agente.
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
