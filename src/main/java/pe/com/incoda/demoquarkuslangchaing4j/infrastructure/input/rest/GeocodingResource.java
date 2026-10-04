package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.input.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import pe.com.incoda.demoquarkuslangchaing4j.application.GeocodingUseCase;
import pe.com.incoda.demoquarkuslangchaing4j.domain.dto.GeoLocationDto;

/**
 * Adaptador de entrada (infrastructure.input.rest) que expone los casos de uso
 * de geocodificación por REST. Se comunica solo con {@link GeoLocationDto}
 * (domain.dto), convirtiendo desde el modelo de dominio {@code GeoLocation} en
 * este límite.
 */
@Path("/geocoding")
@Produces(MediaType.APPLICATION_JSON)
public class GeocodingResource {

    private final GeocodingUseCase geocodingUseCase;

    @Inject
    public GeocodingResource(GeocodingUseCase geocodingUseCase) {
        this.geocodingUseCase = geocodingUseCase;
    }

    /**
     * Geocodificación directa por dirección.
     *
     * @param address dirección o lugar a buscar
     * @return 200 con {@link GeoLocationDto}; 400 si {@code address} está vacío;
     *         404 si no hay coincidencias
     */
    @GET
    @Path("/search")
    public Response search(@QueryParam("address") String address) {
        if (address == null || address.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Query parameter 'address' must not be blank")
                    .build();
        }
        return geocodingUseCase.search(address)
                .map(GeoLocationDto::from)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();
    }

    /**
     * Geocodificación inversa por coordenadas.
     *
     * @param latitude  latitud en grados decimales
     * @param longitude longitud en grados decimales
     * @return 200 con {@link GeoLocationDto}; 404 si no hay coincidencias
     */
    @GET
    @Path("/reverse")
    public Response reverse(@QueryParam("lat") double latitude, @QueryParam("lon") double longitude) {
        return geocodingUseCase.reverse(latitude, longitude)
                .map(GeoLocationDto::from)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();
    }
}
