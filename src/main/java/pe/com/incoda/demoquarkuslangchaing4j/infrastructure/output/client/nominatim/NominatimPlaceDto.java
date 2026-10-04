package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.output.client.nominatim;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import pe.com.incoda.demoquarkuslangchaing4j.domain.model.GeoLocation;

/**
 * Payload del proveedor para un lugar de Nominatim (resultado de búsqueda o
 * reverse). Vive en infrastructure/output/client/nominatim porque es un detalle
 * de la API externa, no un contrato que expongamos. Nominatim devuelve lat/lon
 * como cadenas, por lo que se parsean al convertir al modelo de dominio.
 *
 * @param latitude    latitud como cadena (tal cual la devuelve Nominatim)
 * @param longitude   longitud como cadena (tal cual la devuelve Nominatim)
 * @param displayName descripción legible del lugar
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NominatimPlaceDto(
        @JsonProperty("lat") String latitude,
        @JsonProperty("lon") String longitude,
        @JsonProperty("display_name") String displayName) {

    /**
     * Convierte el DTO del proveedor al modelo de dominio.
     *
     * @return la ubicación de dominio
     * @throws NumberFormatException si lat/lon no son numéricos
     */
    public GeoLocation toDomain() {
        return new GeoLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), displayName);
    }
}
