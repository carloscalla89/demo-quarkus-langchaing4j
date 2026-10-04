package pe.com.incoda.demoquarkuslangchaing4j.domain.dto;

/**
 * Contrato de salida por REST con la respuesta del agente meteorológico.
 *
 * @param answer respuesta generada por el modelo
 */
public record WeatherAnswerDto(String answer) {
}
