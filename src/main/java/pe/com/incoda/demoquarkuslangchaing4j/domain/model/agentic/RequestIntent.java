package pe.com.incoda.demoquarkuslangchaing4j.domain.model.agentic;

/**
 * Intención detectada por el agente enrutador del flujo agentic.
 *
 * <p>Es un tipo puro de dominio: no depende de frameworks. La capa REST lo
 * expone como {@code String} a través de los DTOs.
 */
public enum RequestIntent {

    /** El usuario pregunta por el clima. */
    WEATHER,

    /** El usuario pide coordenadas o una ubicación. */
    GEOCODING,

    /** El usuario quiere planificar un viaje o itinerario. */
    TRIP,

    /** La petición no encaja en ninguna categoría conocida. */
    UNKNOWN
}
