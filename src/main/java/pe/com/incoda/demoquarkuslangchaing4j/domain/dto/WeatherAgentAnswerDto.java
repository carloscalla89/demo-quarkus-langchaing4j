package pe.com.incoda.demoquarkuslangchaing4j.domain.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrato de salida estructurado del asistente agentic. Combina la respuesta en
 * lenguaje natural del workflow con la intención detectada y los agentes usados.
 *
 * <p>Los campos meteorológicos ({@code city}, {@code timezone}, {@code units},
 * {@code current}, {@code daily}, {@code observedAt}) quedan opcionales: solo se
 * rellenan cuando el intent es {@code WEATHER} y el experto devuelve datos
 * estructurados; en el resto de intenciones son {@code null} o vacíos.
 *
 * @param answer      respuesta en lenguaje natural
 * @param intent      intención detectada (WEATHER, GEOCODING, TRIP, UNKNOWN)
 * @param agentsUsed  nombres de los agentes invocados
 * @param city        ciudad consultada (opcional)
 * @param timezone    zona horaria del reporte (opcional)
 * @param units       sistema de unidades (opcional)
 * @param current     condiciones meteorológicas actuales (opcional)
 * @param daily       pronóstico diario (opcional)
 * @param toolsUsed   nombres de las tools invocadas (opcional)
 * @param observedAt  instante de observación del dato meteorológico (opcional)
 * @param generatedAt instante en que se generó la respuesta
 */
public record WeatherAgentAnswerDto(
        String answer,
        String intent,
        List<String> agentsUsed,
        String city,
        String timezone,
        String units,
        WeatherReportDto.CurrentWeatherDto current,
        List<WeatherReportDto.DailyForecastDto> daily,
        List<String> toolsUsed,
        String observedAt,
        String generatedAt) {

    /**
     * Construye el DTO a partir del resultado del workflow agentic.
     *
     * @param answer     respuesta en lenguaje natural
     * @param intent     intención detectada
     * @param agentsUsed nombres de los agentes invocados
     * @return el DTO estructurado
     */
    public static WeatherAgentAnswerDto from(String answer, String intent, List<String> agentsUsed) {
        return new WeatherAgentAnswerDto(
                answer,
                intent,
                agentsUsed == null ? List.of() : agentsUsed,
                null,
                null,
                null,
                null,
                List.of(),
                List.of(),
                null,
                LocalDateTime.now().toString());
    }
}
