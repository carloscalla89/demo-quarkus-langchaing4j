package pe.com.incoda.demoquarkuslangchaing4j.domain.model;

import java.util.List;

/**
 * Resultado del asistente agentic: la respuesta en lenguaje natural junto con la
 * intención detectada y los agentes que participaron. Es un modelo de dominio,
 * sin dependencias de framework ni de infraestructura.
 *
 * @param answer     respuesta final del workflow
 * @param intent     intención detectada (p. ej. {@code "WEATHER"}), o {@code null}
 * @param agentsUsed nombres de los agentes invocados
 */
public record AssistantAnswer(String answer, String intent, List<String> agentsUsed) {
}
