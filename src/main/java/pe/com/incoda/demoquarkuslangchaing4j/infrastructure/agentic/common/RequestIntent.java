package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.common;

/**
 * Intenciones que el router (E2) reconoce en una petición de usuario.
 */
public enum RequestIntent {

    /** Consulta sobre el clima de un lugar. */
    WEATHER,

    /** Consulta de geocodificación (coordenadas ↔ lugar). */
    GEOCODING,

    /** Planificación de un viaje (E1). */
    TRIP,

    /** Cualquier otra petición no soportada. */
    UNKNOWN
}
