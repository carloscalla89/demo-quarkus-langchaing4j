package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.input.rest;

import java.util.Set;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import pe.com.incoda.demoquarkuslangchaing4j.application.agentic.AgenticAssistantService;
import pe.com.incoda.demoquarkuslangchaing4j.domain.dto.WeatherAnswerDto;
import pe.com.incoda.demoquarkuslangchaing4j.domain.dto.agentic.AgenticAssistantRequestDto;

/**
 * Adaptador de entrada REST del asistente agentic enrutador (E2).
 *
 * <p>Coexiste con {@code /weather/assistant} (asistente meteorológico clásico)
 * sin interferir con él.
 */
@Path("/agent/assistant")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AgenticAssistantResource {

    private static final int MAX_REQUEST_LENGTH = 500;
    private static final Set<String> SUPPORTED_LANGUAGES = Set.of("es", "en");

    private final AgenticAssistantService service;

    @Inject
    public AgenticAssistantResource(AgenticAssistantService service) {
        this.service = service;
    }

    /**
     * Enruta la petición al experto adecuado con el workflow agentic.
     *
     * @param request petición, sesión e idioma
     * @return 200 con {@code AgenticAssistantAnswerDto}; 400 si la entrada es inválida
     */
    @POST
    public Response ask(AgenticAssistantRequestDto request) {
        String error = validate(request);
        if (error != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new WeatherAnswerDto(error))
                    .build();
        }
        return Response.ok(service.ask(request)).build();
    }

    private static String validate(AgenticAssistantRequestDto request) {
        if (request == null || request.request() == null || request.request().isBlank()) {
            return "request must not be blank";
        }
        if (request.request().length() > MAX_REQUEST_LENGTH) {
            return "request must not exceed " + MAX_REQUEST_LENGTH + " characters";
        }
        if (request.language() != null && !SUPPORTED_LANGUAGES.contains(request.language())) {
            return "language must be one of: " + SUPPORTED_LANGUAGES;
        }
        return null;
    }
}
