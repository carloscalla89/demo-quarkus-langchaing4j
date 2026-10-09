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
 * Experto en clima del flujo agentic. Reutiliza {@link WeatherTools} para
 * responder consultas meteorológicas.
 */
public interface WeatherExpertAgent {

    /**
     * Responde una consulta de clima usando las tools disponibles.
     *
     * @param request petición del usuario
     * @return resumen del clima
     */
    @UserMessage("""
            Eres un meteorólogo. Responde en el idioma del usuario (por defecto español) en un máximo de 3 líneas.
            SIEMPRE invoca una herramienta (get_weather_by_city, get_weather_by_coordinates o geocode_city)
            antes de responder. Nunca inventes cifras.
            Incluye la atribución "Datos meteorológicos de Open-Meteo.com".
            Petición: '{{request}}'
            """)
    @Agent(description = "Experto en clima", outputKey = "response")
    String answer(String request);

    /**
     * Proporciona las tools meteorológicas al agente.
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
