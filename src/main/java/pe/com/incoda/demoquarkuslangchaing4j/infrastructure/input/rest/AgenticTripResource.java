package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.input.rest;

import java.util.Set;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import pe.com.incoda.demoquarkuslangchaing4j.application.agentic.TripPlannerService;
import pe.com.incoda.demoquarkuslangchaing4j.domain.dto.WeatherAnswerDto;
import pe.com.incoda.demoquarkuslangchaing4j.domain.dto.agentic.TripPlannerRequestDto;

/**
 * Adaptador de entrada REST del planificador de viajes agentic (E1).
 *
 * <p>Coexiste con {@code /weather/assistant} (asistente meteorológico clásico)
 * sin interferir con él.
 */
@Path("/agent/trip")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AgenticTripResource {

    private static final int MAX_DESTINATION_LENGTH = 120;
    private static final int MAX_PREFERENCES_LENGTH = 500;
    private static final Set<String> SUPPORTED_LANGUAGES = Set.of("es", "en");

    private final TripPlannerService service;

    @Inject
    public AgenticTripResource(TripPlannerService service) {
        this.service = service;
    }

    /**
     * Planifica un viaje con el workflow agentic.
     *
     * @param request destino, días y preferencias
     * @return 200 con {@code TripPlanDto}; 400 si la entrada es inválida
     */
    @POST
    public Response plan(TripPlannerRequestDto request) {
        String error = validate(request);
        if (error != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new WeatherAnswerDto(error))
                    .build();
        }
        return Response.ok(service.plan(request)).build();
    }

    private static String validate(TripPlannerRequestDto request) {
        if (request == null || request.destination() == null || request.destination().isBlank()) {
            return "destination must not be blank";
        }
        if (request.destination().length() > MAX_DESTINATION_LENGTH) {
            return "destination must not exceed " + MAX_DESTINATION_LENGTH + " characters";
        }
        if (request.preferences() != null && request.preferences().length() > MAX_PREFERENCES_LENGTH) {
            return "preferences must not exceed " + MAX_PREFERENCES_LENGTH + " characters";
        }
        if (request.language() != null && !SUPPORTED_LANGUAGES.contains(request.language())) {
            return "language must be one of: " + SUPPORTED_LANGUAGES;
        }
        if (request.days() != null && (request.days() < 1 || request.days() > 7)) {
            return "days must be between 1 and 7";
        }
        return null;
    }
}
