package pe.com.incoda.demoquarkuslangchaing4j.application.agentic;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import pe.com.incoda.demoquarkuslangchaing4j.domain.dto.agentic.TripPlanDto;
import pe.com.incoda.demoquarkuslangchaing4j.domain.dto.agentic.TripPlannerRequestDto;
import pe.com.incoda.demoquarkuslangchaing4j.domain.model.agentic.TripPlan;
import pe.com.incoda.demoquarkuslangchaing4j.domain.service.agentic.TripPlanner;

/**
 * Servicio de aplicación que envuelve el planificador de viajes agentic (E1) y
 * traduce su resultado de dominio a un DTO REST.
 */
@ApplicationScoped
public class TripPlannerService {

    /** Número de días por defecto cuando el usuario no lo indica. */
    private static final int DEFAULT_DAYS = 3;

    private final TripPlanner tripPlanner;

    @Inject
    public TripPlannerService(TripPlanner tripPlanner) {
        this.tripPlanner = tripPlanner;
    }

    /**
     * Planifica un viaje a partir de la petición REST.
     *
     * @param request destino, días, preferencias y sesión
     * @return itinerario estructurado
     */
    public TripPlanDto plan(TripPlannerRequestDto request) {
        String sessionId = (request.sessionId() == null || request.sessionId().isBlank())
                ? UUID.randomUUID().toString()
                : request.sessionId();
        int days = request.days() == null ? DEFAULT_DAYS : request.days();
        String preferences = request.preferences() == null ? "" : request.preferences();

        TripPlan plan = tripPlanner.plan(sessionId, request.destination(), days, preferences);

        List<String> agentsUsed = plan.agentsUsed() == null ? List.of() : plan.agentsUsed();
        return new TripPlanDto(
                plan.itinerary(),
                plan.destination(),
                plan.days(),
                agentsUsed,
                LocalDateTime.now().toString());
    }
}
