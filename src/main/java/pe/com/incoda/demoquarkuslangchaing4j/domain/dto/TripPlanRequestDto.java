package pe.com.incoda.demoquarkuslangchaing4j.domain.dto;

/**
 * Contrato de entrada temporal para el planificador de viajes (E1).
 *
 * @param request petición del usuario en lenguaje natural (destino, días y preferencias)
 */
public record TripPlanRequestDto(String request) {
}
