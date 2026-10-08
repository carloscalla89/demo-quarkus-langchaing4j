package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.input.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import pe.com.incoda.demoquarkuslangchaing4j.domain.dto.TripPlanAnswerDto;
import pe.com.incoda.demoquarkuslangchaing4j.domain.dto.TripPlanRequestDto;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.trip.TripPlannerWorkflow;

/**
 * Endpoint temporal (F1) para validar el workflow E1 de forma aislada.
 * Inyecta {@link TripPlannerWorkflow}, lo que además registra sus parámetros de
 * entrada como claves del {@code AgenticScope} en build-time.
 */
@Path("/trip/plan")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TripPlannerResource {

    private final TripPlannerWorkflow tripPlannerWorkflow;

    @Inject
    public TripPlannerResource(TripPlannerWorkflow tripPlannerWorkflow) {
        this.tripPlannerWorkflow = tripPlannerWorkflow;
    }

    /**
     * Planifica un viaje a partir de una petición en lenguaje natural.
     *
     * @param request petición del usuario
     * @return 200 con el itinerario; 400 si la petición está vacía
     */
    @POST
    public Response plan(TripPlanRequestDto request) {
        if (request == null || request.request() == null || request.request().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new TripPlanAnswerDto("request must not be blank"))
                    .build();
        }
        return Response.ok(new TripPlanAnswerDto(tripPlannerWorkflow.plan(request.request()))).build();
    }
}
