package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.output.client.nominatim;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * Cliente REST (MicroProfile) para la API pública de Nominatim. La URL base y
 * las cabeceras se configuran bajo la clave {@code nominatim}
 * (quarkus.rest-client.nominatim.*).
 */
@RegisterRestClient(configKey = "nominatim")
@Path("/")
public interface NominatimClient {

    /**
     * {@code jsonv2} es obligatorio: el servidor público redirige a la Web UI
     * cuando se omite {@code format}.
     */
    String FORMAT_JSONV2 = "jsonv2";

    /**
     * Geocodificación directa.
     *
     * @param query          dirección o lugar a buscar
     * @param format         formato de salida (usar {@link #FORMAT_JSONV2})
     * @param limit          número máximo de resultados
     * @param addressDetails 1 para incluir el desglose de la dirección, 0 si no
     * @return lista de coincidencias
     */
    @GET
    @Path("/search")
    List<NominatimPlaceDto> search(
            @QueryParam("q") String query,
            @QueryParam("format") String format,
            @QueryParam("limit") int limit,
            @QueryParam("addressdetails") int addressDetails);

    /**
     * Geocodificación inversa.
     *
     * @param latitude  latitud en grados decimales
     * @param longitude longitud en grados decimales
     * @param format    formato de salida (usar {@link #FORMAT_JSONV2})
     * @return la ubicación correspondiente
     */
    @GET
    @Path("/reverse")
    NominatimPlaceDto reverse(
            @QueryParam("lat") double latitude,
            @QueryParam("lon") double longitude,
            @QueryParam("format") String format);
}
