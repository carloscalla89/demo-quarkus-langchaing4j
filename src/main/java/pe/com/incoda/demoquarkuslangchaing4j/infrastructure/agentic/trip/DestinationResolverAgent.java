package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.trip;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.RetrievalAugmentorSupplier;
import dev.langchain4j.agentic.declarative.ToolsSupplier;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.agentic.runtime.CdiBean;

import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.support.NoOpRetrievalAugmentor;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.tools.WeatherTools;

/**
 * Agente hoja (E1) que resuelve el destino del viaje y sus coordenadas usando la
 * herramienta {@code geocode_city} de {@link WeatherTools}. Escribe el resultado
 * en el {@code AgenticScope} bajo la clave {@code location}.
 */
public interface DestinationResolverAgent {

    /**
     * Resuelve el destino principal de la petición del usuario.
     *
     * @param request petición del usuario (entrada del workflow)
     * @return una línea con el lugar y sus coordenadas
     */
    @SystemMessage("""
        Eres un asistente de viajes. Resuelves el destino principal de la petición.
        SIEMPRE usa la herramienta geocode_city para obtener las coordenadas.
        Si hay varias coincidencias, elige la más relevante.
        Responde en una sola línea con el nombre del lugar y sus coordenadas.
        Nunca inventes coordenadas.
        """)
    @UserMessage("""
        Petición del usuario: {request}
        """)
    @Agent(description = "Resuelve el destino del viaje y sus coordenadas.", outputKey = "location")
    String resolve(String request);

    @ToolsSupplier
    static Object[] tools(@CdiBean WeatherTools weatherTools) {
        return new Object[] { weatherTools };
    }

    @RetrievalAugmentorSupplier
    static RetrievalAugmentor retrievalAugmentor() {
        return NoOpRetrievalAugmentor.INSTANCE;
    }
}
