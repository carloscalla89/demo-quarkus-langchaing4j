package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.adapter;

import java.util.List;

import dev.langchain4j.agentic.scope.AgentInvocation;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import pe.com.incoda.demoquarkuslangchaing4j.domain.model.agentic.TripPlan;
import pe.com.incoda.demoquarkuslangchaing4j.domain.service.agentic.TripPlanner;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.trip.TripPlannerWorkflow;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.guardrails.AgenticInputGuardrail;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.guardrails.TripOutputGuardrail;

/**
 * Adaptador que implementa el puerto de dominio {@link TripPlanner} delegando en
 * el workflow agentic {@link TripPlannerWorkflow}.
 *
 * <p>La validación de entrada/salida se hace aquí (infraestructura) porque la
 * extensión agentic no procesa {@code @InputGuardrails}/{@code @OutputGuardrails}
 * sobre los métodos de workflow.
 */
@ApplicationScoped
public class TripPlannerAdapter implements TripPlanner {

    private final TripPlannerWorkflow workflow;
    private final AgenticInputGuardrail inputGuardrail;
    private final TripOutputGuardrail outputGuardrail;

    @Inject
    public TripPlannerAdapter(TripPlannerWorkflow workflow,
                              AgenticInputGuardrail inputGuardrail,
                              TripOutputGuardrail outputGuardrail) {
        this.workflow = workflow;
        this.inputGuardrail = inputGuardrail;
        this.outputGuardrail = outputGuardrail;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TripPlan plan(String sessionId, String destination, int days, String preferences) {
        String prefs = preferences == null ? "" : preferences;
        // La petición del endpoint de viajes es inherentemente de viaje; se valida
        // igualmente contra el guardrail (incluye la palabra "viaje").
        inputGuardrail.check("planifica un viaje a " + destination + " " + prefs);

        ResultWithAgenticScope<String> result = workflow.plan(sessionId, destination, days, prefs);

        outputGuardrail.check(result.result());

        List<String> agentsUsed = result.agenticScope().agentInvocations().stream()
                .map(AgentInvocation::agentName)
                .distinct()
                .toList();

        return new TripPlan(result.result(), destination, days, agentsUsed);
    }
}
