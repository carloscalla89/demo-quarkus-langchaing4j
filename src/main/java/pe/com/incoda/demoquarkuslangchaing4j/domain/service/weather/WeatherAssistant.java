package pe.com.incoda.demoquarkuslangchaing4j.domain.service.weather;

import dev.langchain4j.service.Result;

/**
 * Puerto de dominio para el asistente meteorológico. La capa de aplicación
 * depende de esta abstracción y no del AI Service concreto, que vive en
 * infraestructura.
 */
public interface WeatherAssistant {

    /**
     * Responde una consulta en lenguaje natural sobre el clima.
     *
     * @param memoryId identificador de memoria de chat (aísla conversaciones)
     * @param question pregunta del usuario
     * @return resultado con la respuesta y los metadatos de la ejecución (tools usadas, tokens, etc.)
     */
    Result<String> ask(String memoryId, String question);
}
