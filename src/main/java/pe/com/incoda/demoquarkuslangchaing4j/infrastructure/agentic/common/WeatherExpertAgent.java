package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.common;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.RetrievalAugmentorSupplier;
import dev.langchain4j.agentic.declarative.ToolsSupplier;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.tool.ToolErrorContext;
import io.quarkiverse.langchain4j.HandleToolArgumentError;
import io.quarkiverse.langchain4j.agentic.runtime.CdiBean;

import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.support.NoOpRetrievalAugmentor;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.tools.ToolErrorMessages;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.tools.WeatherTools;

/**
 * E2 — Experto en clima. Responde consultas meteorológicas usando las tools de
 * {@link WeatherTools} y escribe la respuesta bajo la clave {@code response}.
 */
public interface WeatherExpertAgent {

    /**
     * Responde una consulta meteorológica.
     *
     * @param request petición del usuario
     * @return respuesta en lenguaje natural con la atribución obligatoria
     */
    @SystemMessage("""
        Eres un meteorólogo que responde en el idioma de la petición (por defecto español),
        en un máximo de 4 líneas.
        SIEMPRE usa una herramienta (get_weather_by_city, get_weather_by_coordinates,
        geocode_city o reverse_geocode) antes de responder. Nunca respondas de memoria.
        Si una herramienta falla, dilo y pide aclaración.
        Incluye siempre la atribución "Datos meteorológicos de Open-Meteo.com".
        """)
    @UserMessage("{request}")
    @Agent(description = "Responde consultas sobre el clima.", outputKey = "response")
    String answer(String request);

    @ToolsSupplier
    static Object[] tools(@CdiBean WeatherTools weatherTools) {
        return new Object[] { weatherTools };
    }

    @RetrievalAugmentorSupplier
    static RetrievalAugmentor retrievalAugmentor() {
        return NoOpRetrievalAugmentor.INSTANCE;
    }

    @HandleToolArgumentError
    static String handleToolArgumentError(Throwable error, ToolErrorContext context) {
        return ToolErrorMessages.arguments(context, error);
    }
}
