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
 * Agente hoja (E1) que obtiene el pronóstico del destino usando
 * {@code get_weather_by_city}. Lee {@code request} y {@code location} del
 * {@code AgenticScope} y escribe el resultado bajo la clave {@code weather}.
 */
public interface ForecastAgent {

    /**
     * Obtiene un resumen del clima esperado para el destino resuelto.
     *
     * @param request  petición original del usuario
     * @param location destino resuelto por {@link DestinationResolverAgent}
     * @return resumen del clima en pocas líneas
     */
    @SystemMessage("""
        Eres un meteorólogo. Usa la herramienta get_weather_by_city para el destino indicado.
        Resume en 2 líneas la temperatura, la descripción y el viento.
        Incluye la atribución "Datos meteorológicos de Open-Meteo.com".
        Nunca inventes cifras.
        """)
    @UserMessage("""
        Destino resuelto: {location}
        Petición del usuario: {request}
        """)
    @Agent(description = "Obtiene el clima esperado para el destino del viaje.", outputKey = "weather")
    String forecast(String request, String location);

    @ToolsSupplier
    static Object[] tools(@CdiBean WeatherTools weatherTools) {
        return new Object[] { weatherTools };
    }

    @RetrievalAugmentorSupplier
    static RetrievalAugmentor retrievalAugmentor() {
        return NoOpRetrievalAugmentor.INSTANCE;
    }
}
