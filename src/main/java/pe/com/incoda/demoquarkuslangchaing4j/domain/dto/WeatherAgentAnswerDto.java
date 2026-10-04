package pe.com.incoda.demoquarkuslangchaing4j.domain.dto;

import java.time.LocalDateTime;
import java.util.List;

import pe.com.incoda.demoquarkuslangchaing4j.domain.model.WeatherToolResult;

/**
 * Contrato de salida estructurado del agente meteorológico. Combina la respuesta
 * en lenguaje natural del modelo con los datos obtenidos por las tools.
 *
 * @param answer      respuesta en lenguaje natural del modelo
 * @param city        ciudad consultada (si la tool la devolvió)
 * @param timezone    zona horaria del reporte
 * @param units       sistema de unidades ({@code "metric"})
 * @param current     condiciones meteorológicas actuales
 * @param daily       pronóstico diario
 * @param toolsUsed   nombres de las tools invocadas por el modelo
 * @param observedAt  instante de observación del dato meteorológico
 * @param generatedAt instante en que se generó la respuesta
 */
public record WeatherAgentAnswerDto(
        String answer,
        String city,
        String timezone,
        String units,
        WeatherReportDto.CurrentWeatherDto current,
        List<WeatherReportDto.DailyForecastDto> daily,
        List<String> toolsUsed,
        String observedAt,
        String generatedAt) {

    /**
     * Construye el DTO a partir de la respuesta del modelo y del resultado de la
     * tool meteorológica (puede ser {@code null} si el modelo no la invocó).
     *
     * @param answer    respuesta en lenguaje natural del modelo
     * @param toolsUsed nombres de las tools invocadas
     * @param result    resultado de la tool meteorológica, o {@code null}
     * @return el DTO estructurado
     */
    public static WeatherAgentAnswerDto from(String answer, List<String> toolsUsed, WeatherToolResult result) {
        if (result == null) {
            return new WeatherAgentAnswerDto(
                    answer, null, null, null, null, List.of(), toolsUsed, null, LocalDateTime.now().toString());
        }
        return new WeatherAgentAnswerDto(
                answer,
                result.location(),
                result.timezone(),
                result.units(),
                WeatherReportDto.fromSnapshot(result.current()),
                result.daily() == null ? List.of() : result.daily().stream()
                        .map(WeatherReportDto::fromDaily)
                        .toList(),
                toolsUsed,
                result.current() == null ? null : result.current().observedAt(),
                LocalDateTime.now().toString());
    }
}
