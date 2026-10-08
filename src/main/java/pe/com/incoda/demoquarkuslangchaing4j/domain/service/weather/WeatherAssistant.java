package pe.com.incoda.demoquarkuslangchaing4j.domain.service.weather;

import pe.com.incoda.demoquarkuslangchaing4j.domain.model.AssistantAnswer;

/**
 * Puerto de dominio para el asistente meteorológico agentic. La capa de
 * aplicación depende de esta abstracción y no del workflow concreto, que vive en
 * infraestructura.
 */
public interface WeatherAssistant {

    /**
     * Atiende una consulta en lenguaje natural sobre clima, geocodificación o
     * planificación de viajes.
     *
     * @param sessionId identificador de sesión (aisla la memoria de chat)
     * @param question  pregunta del usuario en lenguaje natural
     * @return la respuesta, la intención detectada y los agentes usados
     */
    AssistantAnswer ask(String sessionId, String question);
}
