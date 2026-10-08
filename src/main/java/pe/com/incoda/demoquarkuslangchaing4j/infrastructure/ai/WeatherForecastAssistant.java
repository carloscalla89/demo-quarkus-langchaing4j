package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.guardrail.InputGuardrails;
import dev.langchain4j.service.guardrail.OutputGuardrails;
import dev.langchain4j.service.tool.ToolErrorContext;
import io.quarkiverse.langchain4j.HandleToolArgumentError;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

import pe.com.incoda.demoquarkuslangchaing4j.domain.service.weather.WeatherAssistant;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.guardrails.WeatherInputGuardrail;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.guardrails.WeatherOutputGuardrail;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.tools.ToolErrorMessages;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.tools.WeatherTools;

/**
 * Adaptador de IA (infrastructure.ai) que implementa el puerto
 * {@link WeatherAssistant} mediante un AI Service de Quarkus LangChain4j. El
 * modelo decide cuándo invocar las tools de {@link WeatherTools}.
 *
 * <p>Devuelve {@link Result} para poder exponer las tools usadas y los metadatos
 * de la ejecución. Aplica un guardrail de entrada (dominio) y uno de salida
 * (atribución).
 */
@ApplicationScoped
@RegisterAiService(tools = WeatherTools.class, retrievalAugmentor = RegisterAiService.NoRetrievalAugmentorSupplier.class)
public interface WeatherForecastAssistant extends WeatherAssistant {

    /**
     * Responde una consulta meteorológica usando las tools disponibles.
     *
     * @param memoryId identificador de memoria de chat (aísla conversaciones)
     * @param question pregunta del usuario (p. ej. el clima de una ciudad)
     * @return resultado con la respuesta y las tools invocadas
     */
    @Override
    @SystemMessage("""
        Eres un meteorólogo que responde en el idioma indicado en el contexto
        (por defecto español), en un máximo de 3 líneas.

        Reglas:
        - SIEMPRE invoca una herramienta antes de responder. Nunca respondas de memoria.
        - Si el usuario indica "ciudad", "días", "idioma" o "unidades" en el contexto,
          respétalos al llamar a la herramienta.
        - Si geocode_city devuelve varias coincidencias, pide al usuario que elija una.
        - Nunca inventes cifras: si una herramienta falla o no encuentra el lugar, dilo
          y pide aclaración.
        - Incluye siempre la atribución "Datos meteorológicos de Open-Meteo.com".

        La herramienta get_weather_by_city / get_weather_by_coordinates devuelve un JSON:
        {
          "location": "Lima",
          "timezone": "America/Lima",
          "units": "metric",
          "current": {
            "temperatureC": 22.0,            // temperatura actual en grados Celsius
            "apparentTemperatureC": 21.5,    // sensación térmica en grados Celsius
            "relativeHumidityPercent": 60,   // humedad relativa en porcentaje
            "weatherCode": 1,                // código meteorológico WMO
            "description": "Mainly clear",   // descripción legible del código
            "windSpeedKmh": 12.0,            // velocidad del viento en km/h
            "observedAt": "2026-10-04T10:00" // instante de observación (ISO-8601)
          },
          "daily": [
            {
              "date": "2026-10-04",
              "temperatureMaxC": 25.0,
              "temperatureMinC": 18.0,
              "weatherCode": 1,
              "description": "Mainly clear",
              "precipitationProbabilityPercent": 10
            }
          ],
          "attribution": "Datos meteorológicos de Open-Meteo.com ..."
        }

        Resume lo más relevante (temperatura, descripción y viento).
        """)
    @InputGuardrails(WeatherInputGuardrail.class)
    @OutputGuardrails(WeatherOutputGuardrail.class)
    Result<String> ask(@MemoryId String memoryId, String question);

    /**
     * Maneja argumentos inválidos al invocar una tool. LangChain4j exige que este
     * manejador sea un método estático de la propia interfaz AI Service.
     *
     * @param error   excepción lanzada al parsear los argumentos
     * @param context contexto con la petición que provocó el error
     * @return mensaje devuelto al modelo
     */
    @HandleToolArgumentError
    static String handleToolArgumentError(Throwable error, ToolErrorContext context) {
        return ToolErrorMessages.arguments(context, error);
    }
}
