package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.router;

import dev.langchain4j.agentic.declarative.ActivationCondition;
import dev.langchain4j.agentic.declarative.ConditionalAgent;

import pe.com.incoda.demoquarkuslangchaing4j.domain.model.agentic.RequestIntent;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.common.GeocodingExpertAgent;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.common.WeatherExpertAgent;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.trip.TripExpertAgent;

/**
 * Enrutador condicional E2. Según la intención detectada por
 * {@code IntentRouterAgent} (clave {@code intent} del {@code AgenticScope}),
 * activa el experto correspondiente.
 */
public interface ExpertDispatcher {

    /**
     * Despacha la petición al experto activado.
     *
     * @param request petición del usuario
     * @return respuesta del experto
     */
    @ConditionalAgent(outputKey = "response",
            subAgents = { WeatherExpertAgent.class, GeocodingExpertAgent.class, TripExpertAgent.class })
    String dispatch(String request);

    /**
     * Activa el experto en clima cuando la intención es {@code WEATHER}.
     *
     * @param intent intención detectada
     * @return {@code true} si aplica
     */
    @ActivationCondition(WeatherExpertAgent.class)
    static boolean weather(RequestIntent intent) {
        return intent == RequestIntent.WEATHER;
    }

    /**
     * Activa el experto en geocodificación cuando la intención es {@code GEOCODING}.
     *
     * @param intent intención detectada
     * @return {@code true} si aplica
     */
    @ActivationCondition(GeocodingExpertAgent.class)
    static boolean geocoding(RequestIntent intent) {
        return intent == RequestIntent.GEOCODING;
    }

    /**
     * Activa el experto en viajes cuando la intención es {@code TRIP}.
     *
     * @param intent intención detectada
     * @return {@code true} si aplica
     */
    @ActivationCondition(TripExpertAgent.class)
    static boolean trip(RequestIntent intent) {
        return intent == RequestIntent.TRIP;
    }
}
