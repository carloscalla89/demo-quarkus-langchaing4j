package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.router;

import dev.langchain4j.agentic.declarative.ActivationCondition;
import dev.langchain4j.agentic.declarative.ConditionalAgent;

import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.common.FallbackExpertAgent;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.common.GeocodingExpertAgent;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.common.RequestIntent;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.common.WeatherExpertAgent;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.trip.TripPlannerWorkflow;

/**
 * E2 — Enrutador condicional. Activa un único experto según la intención
 * detectada por {@code IntentRouterAgent} ({@code intent} en el
 * {@code AgenticScope}). Todos los expertos escriben su respuesta bajo la clave
 * {@code response}.
 */
public interface ExpertDispatcher {

    /**
     * Enruta la petición al experto adecuado según la intención.
     *
     * @param request petición del usuario
     * @param intent  intención detectada por {@code IntentRouterAgent}
     * @return la respuesta del experto activado
     */
    @ConditionalAgent(
            outputKey = "response",
            subAgents = {
                    WeatherExpertAgent.class,
                    GeocodingExpertAgent.class,
                    TripPlannerWorkflow.class,
                    FallbackExpertAgent.class })
    String dispatch(String request, RequestIntent intent);

    @ActivationCondition(WeatherExpertAgent.class)
    static boolean weather(RequestIntent intent) {
        return intent == RequestIntent.WEATHER;
    }

    @ActivationCondition(GeocodingExpertAgent.class)
    static boolean geocoding(RequestIntent intent) {
        return intent == RequestIntent.GEOCODING;
    }

    @ActivationCondition(TripPlannerWorkflow.class)
    static boolean trip(RequestIntent intent) {
        return intent == RequestIntent.TRIP;
    }

    @ActivationCondition(FallbackExpertAgent.class)
    static boolean fallback(RequestIntent intent) {
        return intent == null || intent == RequestIntent.UNKNOWN;
    }
}
