package pe.com.incoda.demoquarkuslangchaing4j.domain.service.agentic;

import pe.com.incoda.demoquarkuslangchaing4j.domain.model.agentic.TripPlan;

/**
 * Puerto de dominio del planificador de viajes agentic (E1). La capa de
 * aplicación depende de esta abstracción; el workflow de agentes concreto vive
 * en infraestructura.
 */
public interface TripPlanner {

    /**
     * Planifica un viaje.
     *
     * @param sessionId   identificador de sesión para aislar la memoria de chat (puede ser {@code null})
     * @param destination destino del viaje
     * @param days        número de días
     * @param preferences preferencias del viajero (puede ser {@code null})
     * @return el itinerario y los agentes que participaron
     */
    TripPlan plan(String sessionId, String destination, int days, String preferences);
}
