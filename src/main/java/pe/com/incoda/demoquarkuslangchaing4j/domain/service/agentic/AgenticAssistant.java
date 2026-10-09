package pe.com.incoda.demoquarkuslangchaing4j.domain.service.agentic;

import pe.com.incoda.demoquarkuslangchaing4j.domain.model.agentic.AgenticAnswer;

/**
 * Puerto de dominio del asistente agentic enrutador (E2). La capa de aplicación
 * depende de esta abstracción; el workflow de agentes concreto vive en
 * infraestructura.
 */
public interface AgenticAssistant {

    /**
     * Atiende una petición en lenguaje natural enrutándola al experto adecuado.
     *
     * @param sessionId identificador de sesión para aislar la memoria de chat (puede ser {@code null})
     * @param request   petición del usuario
     * @return respuesta del experto, intención detectada y agentes invocados
     */
    AgenticAnswer ask(String sessionId, String request);
}
