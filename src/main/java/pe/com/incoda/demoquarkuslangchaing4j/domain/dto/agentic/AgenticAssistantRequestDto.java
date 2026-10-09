package pe.com.incoda.demoquarkuslangchaing4j.domain.dto.agentic;

/**
 * Contrato de entrada REST del asistente agentic enrutador (E2).
 *
 * @param request   petición del usuario en lenguaje natural (obligatoria)
 * @param sessionId identificador de sesión para aislar la memoria (opcional)
 * @param language  idioma de la respuesta, p. ej. {@code "es"} o {@code "en"} (opcional)
 */
public record AgenticAssistantRequestDto(
        String request,
        String sessionId,
        String language) {
}
