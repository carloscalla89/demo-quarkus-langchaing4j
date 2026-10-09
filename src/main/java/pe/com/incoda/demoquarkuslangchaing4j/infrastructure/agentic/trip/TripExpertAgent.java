package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.trip;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.RetrievalAugmentorSupplier;
import dev.langchain4j.agentic.declarative.ToolsSupplier;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.agentic.runtime.CdiBean;

import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.support.NoOpRetrievalAugmentor;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.tools.TravelTools;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.tools.WeatherTools;

/**
 * Experto en viajes del flujo agentic enrutador (E2). A diferencia de E1 (que es
 * un workflow secuencial estructurado), este agente parte de la petición libre
 * del usuario y, con las tools disponibles, compone un itinerario directamente.
 *
 * <p>Se usa como rama TRIP del {@link pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.router.ExpertDispatcher}
 * porque el router sólo dispone de la petición en texto libre, no de los campos
 * estructurados que requiere {@link TripPlannerWorkflow}.
 */
public interface TripExpertAgent {

    /**
     * Planifica un viaje a partir de una petición en lenguaje natural.
     *
     * @param request petición del usuario
     * @return itinerario propuesto
     */
    @UserMessage("""
            Eres un planificador de viajes. A partir de la petición, usa las herramientas disponibles
            (geocode_city, get_weather_by_city, suggest_activity) y redacta un itinerario conciso por días.
            Incluye la atribución "Datos meteorológicos de Open-Meteo.com" si usas datos de clima.
            Petición: '{{request}}'
            """)
    @Agent(description = "Planificador de viajes", outputKey = "response")
    String plan(String request);

    /**
     * Proporciona las tools meteorológicas y de viaje al agente.
     *
     * @param weatherTools bean CDI con las tools meteorológicas
     * @param travelTools  bean CDI con las tools de viaje
     * @return objetos con métodos {@code @Tool}
     */
    @ToolsSupplier
    static Object[] tools(@CdiBean WeatherTools weatherTools, @CdiBean TravelTools travelTools) {
        return new Object[] { weatherTools, travelTools };
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
