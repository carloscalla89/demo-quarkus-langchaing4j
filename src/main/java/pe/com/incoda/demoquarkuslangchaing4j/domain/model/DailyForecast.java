package pe.com.incoda.demoquarkuslangchaing4j.domain.model;

/**
 * Objeto de valor de dominio con el pronóstico de un día.
 *
 * @param date                             fecha del pronóstico (ISO-8601, {@code yyyy-MM-dd})
 * @param temperatureMaxC                  temperatura máxima del día en grados Celsius
 * @param temperatureMinC                  temperatura mínima del día en grados Celsius
 * @param weatherCode                      código meteorológico WMO más severo del día
 * @param description                      descripción legible del código WMO
 * @param precipitationProbabilityPercent  probabilidad máxima de precipitación en porcentaje
 */
public record DailyForecast(
        String date,
        double temperatureMaxC,
        double temperatureMinC,
        int weatherCode,
        String description,
        int precipitationProbabilityPercent) {
}
