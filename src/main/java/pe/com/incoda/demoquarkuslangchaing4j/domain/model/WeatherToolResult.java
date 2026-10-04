package pe.com.incoda.demoquarkuslangchaing4j.domain.model;

import java.util.List;

/**
 * Resultado compacto de una tool meteorológica. Es lo que se envía al LLM (en
 * JSON) y lo que permite construir una respuesta REST estructurada sin volver a
 * consultar al proveedor.
 *
 * @param location    nombre legible del lugar consultado
 * @param timezone    zona horaria resuelta por el proveedor
 * @param units       sistema de unidades usado ({@code "metric"})
 * @param current     condiciones meteorológicas actuales
 * @param daily       pronóstico diario (recortado a los días solicitados)
 * @param attribution texto de atribución obligatorio de las fuentes
 */
public record WeatherToolResult(
        String location,
        String timezone,
        String units,
        WeatherSnapshot current,
        List<DailyForecast> daily,
        String attribution) {
}
