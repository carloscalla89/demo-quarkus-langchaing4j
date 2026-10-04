package pe.com.incoda.demoquarkuslangchaing4j.domain.model;

/**
 * Objeto de valor de dominio que representa una ubicación geocodificada:
 * coordenadas más una etiqueta legible. Sin anotaciones de framework ni campos
 * específicos de proveedor.
 *
 * @param latitude    latitud en grados decimales (WGS84)
 * @param longitude   longitud en grados decimales (WGS84)
 * @param displayName descripción legible devuelta por el proveedor
 */
public record GeoLocation(double latitude, double longitude, String displayName) {
}
