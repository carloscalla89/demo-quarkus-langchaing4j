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
 * Segundo paso del planificador de viajes (E1): obtiene el pronóstico del
 * destino ya resuelto.
 */
public interface ForecastAgent {

    /**
     * Obtiene el pronóstico del clima para la ubicación resuelta.
     *
     * @param location ubicación resuelta en el paso anterior
     * @param days     número de días de pronóstico
     * @return resumen del clima
     */
    @UserMessage("""
            Obtén el pronóstico del clima para la ubicación usando la herramienta get_weather_by_city.
            Resume en pocas líneas las condiciones actuales y el pronóstico de {{days}} días.
            Incluye la atribución "Datos meteorológicos de Open-Meteo.com".
            Ubicación: '{{location}}'
            """)
    @Agent(description = "Obtiene el pronóstico del clima del destino", outputKey = "weather")
    String forecast(String location, int days);

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
