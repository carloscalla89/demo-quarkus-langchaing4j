package pe.com.incoda.demoquarkuslangchaing4j.domain.model.agentic;

import java.util.List;

/**
 * Resultado del asistente agentic enrutador (E2): la respuesta del experto
 * seleccionado junto a la intención detectada y los agentes invocados.
 *
 * @param answer     respuesta en lenguaje natural del experto
 * @param intent     intención detectada por el enrutador
 * @param agentsUsed nombres de los agentes que participaron en el workflow
 */
public record AgenticAnswer(
        String answer,
        RequestIntent intent,
        List<String> agentsUsed) {
}
