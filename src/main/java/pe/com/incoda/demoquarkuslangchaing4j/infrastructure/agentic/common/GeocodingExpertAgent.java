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
 * E2 — Experto en geocodificación. Resuelve coordenadas a partir de un lugar
 * (o viceversa) usando las tools de {@link WeatherTools} y escribe la respuesta
 * bajo la clave {@code response}.
 */
public interface GeocodingExpertAgent {

    /**
     * Responde una consulta de geocodificación.
     *
     * @param request petición del usuario
     * @return respuesta en lenguaje natural con las coincidencias o coordenadas
     */
    @SystemMessage("""
        Eres un experto en geocodificación que responde en el idioma de la petición
        (por defecto español), en un máximo de 4 líneas.
        Usa las herramientas geocode_city o reverse_geocode según corresponda.
        Si hay varias coincidencias, preséntalas numeradas y pide al usuario que elija.
        Nunca inventes coordenadas.
        Incluye la atribución "geocodificación por OpenStreetMap/Nominatim (ODbL)".
        """)
    @UserMessage("{request}")
    @Agent(description = "Resuelve coordenadas y lugares (geocodificación).", outputKey = "response")
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
