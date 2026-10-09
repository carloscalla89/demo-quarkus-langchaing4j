package pe.com.incoda.demoquarkuslangchaing4j.domain.dto.agentic;

/**
 * Contrato de entrada REST del planificador de viajes agentic (E1).
 *
 * @param destination destino del viaje (obligatorio)
 * @param days        número de días, 1..7 (opcional; por defecto 3)
 * @param preferences preferencias del viajero (opcional)
 * @param sessionId   identificador de sesión para aislar la memoria (opcional)
 * @param language    idioma de la respuesta, p. ej. {@code "es"} o {@code "en"} (opcional)
 */
public record TripPlannerRequestDto(
        String destination,
        Integer days,
        String preferences,
        String sessionId,
        String language) {
}
