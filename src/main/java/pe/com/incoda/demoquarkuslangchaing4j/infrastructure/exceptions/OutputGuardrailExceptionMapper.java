package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.exceptions;

import dev.langchain4j.guardrail.OutputGuardrailException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import pe.com.incoda.demoquarkuslangchaing4j.domain.dto.WeatherAnswerDto;

/**
 * Traduce el fallo definitivo de un guardrail de salida (tras agotar los
 * reintentos) a una respuesta HTTP 502.
 */
@Provider
public class OutputGuardrailExceptionMapper implements ExceptionMapper<OutputGuardrailException> {

    /**
     * Construye la respuesta HTTP 502.
     *
     * @param exception excepción lanzada por el guardrail de salida
     * @return respuesta 502 con el mensaje del guardrail
     */
    @Override
    public Response toResponse(OutputGuardrailException exception) {
        return Response.status(Response.Status.BAD_GATEWAY)
                .type(MediaType.APPLICATION_JSON)
                .entity(new WeatherAnswerDto(exception.getMessage()))
                .build();
    }
}
