package pe.com.incoda.demoquarkuslangchaing4j.domain.model.agentic;

import java.util.List;

/**
 * Resultado del planificador de viajes agentic (E1).
 *
 * @param itinerary  itinerario en lenguaje natural compuesto por el workflow
 * @param destination destino solicitado
 * @param days        número de días planificados
 * @param agentsUsed  nombres de los agentes que participaron en el workflow
 */
public record TripPlan(
        String itinerary,
        String destination,
        int days,
        List<String> agentsUsed) {
}
