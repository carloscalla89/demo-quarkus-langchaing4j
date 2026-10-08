package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.input.rest;

import java.util.Set;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import pe.com.incoda.demoquarkuslangchaing4j.application.WeatherAssistantService;
import pe.com.incoda.demoquarkuslangchaing4j.domain.dto.WeatherAnswerDto;
import pe.com.incoda.demoquarkuslangchaing4j.domain.dto.WeatherQuestionDto;

/**
 * Adaptador de entrada (infrastructure.input.rest) que expone el asistente
 * meteorológico por REST. Se comunica solo con tipos de domain.dto.
 */
@Path("/weather/assistant")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WeatherAssistantResource {

    private static final int MAX_QUESTION_LENGTH = 500;
    private static final Set<String> SUPPORTED_LANGUAGES = Set.of("es", "en");

    private final WeatherAssistantService service;

    @Inject
    public WeatherAssistantResource(WeatherAssistantService service) {
        this.service = service;
    }

    /**
     * Pregunta al asistente meteorológico.
     *
     * @param request pregunta y opciones del usuario
     * @return 200 con {@code WeatherAssistantAnswerDto}; 400 si la entrada es inválida
     */
    @POST
    public Response ask(WeatherQuestionDto request) {
        String error = validate(request);
        if (error != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new WeatherAnswerDto(error))
                    .build();
        }
        return Response.ok(service.ask(request)).build();
    }

    private static String validate(WeatherQuestionDto request) {
        if (request == null || request.question() == null || request.question().isBlank()) {
            return "question must not be blank";
        }
        if (request.question().length() > MAX_QUESTION_LENGTH) {
            return "question must not exceed " + MAX_QUESTION_LENGTH + " characters";
        }
        if (request.language() != null && !SUPPORTED_LANGUAGES.contains(request.language())) {
            return "language must be one of: " + SUPPORTED_LANGUAGES;
        }
        if (request.units() != null && !"metric".equals(request.units())) {
            return "units must be 'metric'";
        }
        if (request.days() != null && (request.days() < 1 || request.days() > 7)) {
            return "days must be between 1 and 7";
        }
        return null;
    }
}
