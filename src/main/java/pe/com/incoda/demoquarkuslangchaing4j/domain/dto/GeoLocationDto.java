package pe.com.incoda.demoquarkuslangchaing4j.domain.dto;

import pe.com.incoda.demoquarkuslangchaing4j.domain.model.GeoLocation;

/**
 * Contrato expuesto por la API REST de entrada para una ubicación geocodificada.
 * Solo la infraestructura (principalmente {@code input/rest}) debe referenciar
 * este tipo; el modelo de dominio {@link GeoLocation} nunca se expone hacia afuera.
 *
 * @param latitude    latitud en grados decimales (WGS84)
 * @param longitude   longitud en grados decimales (WGS84)
 * @param displayName descripción legible de la ubicación
 */
public record GeoLocationDto(double latitude, double longitude, String displayName) {

    /**
     * Crea el DTO a partir del modelo de dominio.
     *
     * @param location ubicación de dominio
     * @return el DTO equivalente
     */
    public static GeoLocationDto from(GeoLocation location) {
        return new GeoLocationDto(location.latitude(), location.longitude(), location.displayName());
    }
}
