package pe.com.incoda.demoquarkuslangchaing4j.domain.dto.agentic;

import java.util.List;

/**
 * Contrato de salida REST del planificador de viajes agentic (E1).
 *
 * @param itinerary   itinerario en lenguaje natural
 * @param destination destino solicitado
 * @param days        número de días planificados
 * @param agentsUsed  nombres de los agentes que participaron
 * @param generatedAt instante en que se generó la respuesta
 */
public record TripPlanDto(
        String itinerary,
        String destination,
        Integer days,
        List<String> agentsUsed,
        String generatedAt) {
}
