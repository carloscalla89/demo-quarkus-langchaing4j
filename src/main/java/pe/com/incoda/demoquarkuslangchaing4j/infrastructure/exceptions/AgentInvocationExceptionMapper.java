package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.exceptions;

import dev.langchain4j.agentic.agent.AgentInvocationException;
import dev.langchain4j.guardrail.InputGuardrailException;
import dev.langchain4j.guardrail.OutputGuardrailException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import pe.com.incoda.demoquarkuslangchaing4j.domain.dto.WeatherAnswerDto;

/**
 * Traduce los fallos de los agentes del workflow agentic. El framework agentic
 * envuelve las excepciones de guardrail en {@link AgentInvocationException}, por
 * lo que este mapper recorre la cadena de causas y aplica el mismo contrato que
 * los mappers directos: guardrail de entrada → 400, de salida → 502, otros → 500.
 */
@Provider
public class AgentInvocationExceptionMapper implements ExceptionMapper<AgentInvocationException> {

    /**
     * Construye la respuesta HTTP adecuada según la causa raíz.
     *
     * @param exception excepción lanzada al invocar un agente
     * @return 400 para fallos de guardrail de entrada, 502 para los de salida, 500 en otro caso
     */
    @Override
    public Response toResponse(AgentInvocationException exception) {
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof InputGuardrailException inputGuardrail) {
                return json(Response.Status.BAD_REQUEST, inputGuardrail.getMessage());
            }
            if (cause instanceof OutputGuardrailException outputGuardrail) {
                return json(Response.Status.BAD_GATEWAY, outputGuardrail.getMessage());
            }
        }
        return json(Response.Status.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    private static Response json(Response.Status status, String message) {
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(new WeatherAnswerDto(message))
                .build();
    }
}
