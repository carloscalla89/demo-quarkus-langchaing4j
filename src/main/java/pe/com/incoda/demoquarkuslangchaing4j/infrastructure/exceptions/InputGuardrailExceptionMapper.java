package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.exceptions;

import dev.langchain4j.guardrail.InputGuardrailException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import pe.com.incoda.demoquarkuslangchaing4j.domain.dto.WeatherAnswerDto;

/**
 * Traduce el fallo de un guardrail de entrada (p. ej. pregunta fuera de dominio)
 * a una respuesta HTTP 400 con el mensaje del guardrail.
 */
@Provider
public class InputGuardrailExceptionMapper implements ExceptionMapper<InputGuardrailException> {

    /**
     * Construye la respuesta HTTP 400.
     *
     * @param exception excepción lanzada por el guardrail de entrada
     * @return respuesta 400 con el mensaje del guardrail
     */
    @Override
    public Response toResponse(InputGuardrailException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(new WeatherAnswerDto(exception.getMessage()))
                .build();
    }
}
