package pe.com.incoda.demoquarkuslangchaing4j.domain.dto;

/**
 * Contrato de salida temporal del planificador de viajes (E1).
 *
 * @param itinerary itinerario generado por el workflow
 */
public record TripPlanAnswerDto(String itinerary) {
}
