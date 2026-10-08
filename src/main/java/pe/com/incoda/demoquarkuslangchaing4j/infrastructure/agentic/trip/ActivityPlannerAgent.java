package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.trip;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.RetrievalAugmentorSupplier;
import dev.langchain4j.agentic.declarative.ToolsSupplier;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.agentic.runtime.CdiBean;

import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.support.NoOpRetrievalAugmentor;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.tools.TravelTools;

/**
 * Agente hoja (E1) que propone actividades usando {@code suggest_activity} de
 * {@link TravelTools}. Lee {@code request}, {@code location} y {@code weather}
 * del {@code AgenticScope} y escribe el resultado bajo la clave
 * {@code activities}.
 */
public interface ActivityPlannerAgent {

    /**
     * Propone actividades para el destino según el clima y las preferencias.
     *
     * @param request  petición original del usuario (incluye preferencias)
     * @param location destino resuelto
     * @param weather  clima esperado
     * @return lista de actividades sugeridas
     */
    @SystemMessage("""
        Eres un planificador de actividades turísticas.
        Usa la herramienta suggest_activity con el destino, el clima y las preferencias del usuario.
        Devuelve una lista breve de actividades concretas.
        """)
    @UserMessage("""
        Destino: {location}
        Clima: {weather}
        Petición del usuario: {request}
        """)
    @Agent(description = "Propone actividades para el destino del viaje.", outputKey = "activities")
    String planActivities(String request, String location, String weather);

    @ToolsSupplier
    static Object[] tools(@CdiBean TravelTools travelTools) {
        return new Object[] { travelTools };
    }

    @RetrievalAugmentorSupplier
    static RetrievalAugmentor retrievalAugmentor() {
        return NoOpRetrievalAugmentor.INSTANCE;
    }
}
