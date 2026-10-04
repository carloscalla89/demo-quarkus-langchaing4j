package pe.com.incoda.demoquarkuslangchaing4j.domain.dto;

/**
 * Contrato de entrada por REST para preguntar al agente meteorológico.
 *
 * @param question  pregunta del usuario en lenguaje natural (obligatoria)
 * @param sessionId identificador de sesión para aislar la memoria de chat (opcional)
 * @param language  idioma de la respuesta, p. ej. {@code "es"} o {@code "en"} (opcional)
 * @param units     sistema de unidades; actualmente solo {@code "metric"} (opcional)
 * @param days      número de días de pronóstico deseados, 1..7 (opcional)
 * @param city      ciudad forzada, evita que el modelo la infiera (opcional)
 */
public record WeatherQuestionDto(
        String question,
        String sessionId,
        String language,
        String units,
        Integer days,
        String city) {
}
