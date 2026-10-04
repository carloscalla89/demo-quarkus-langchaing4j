package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.input.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import pe.com.incoda.demoquarkuslangchaing4j.application.WeatherUseCase;
import pe.com.incoda.demoquarkuslangchaing4j.domain.dto.WeatherReportDto;

/**
 * Adaptador de entrada (infrastructure.input.rest) que expone los casos de uso
 * meteorológicos por REST. Se comunica solo con {@link WeatherReportDto}
 * (domain.dto).
 */
@Path("/weather")
@Produces(MediaType.APPLICATION_JSON)
public class WeatherResource {

    private final WeatherUseCase weatherUseCase;

    @Inject
    public WeatherResource(WeatherUseCase weatherUseCase) {
        this.weatherUseCase = weatherUseCase;
    }

    /**
     * Pronóstico por coordenadas.
     *
     * @param latitude  latitud en grados decimales
     * @param longitude longitud en grados decimales
     * @return 200 con {@link WeatherReportDto}; 404 si no hay datos
     */
    @GET
    public Response forecast(@QueryParam("lat") double latitude, @QueryParam("lon") double longitude) {
        return weatherUseCase.forecast(latitude, longitude)
                .map(WeatherReportDto::from)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();
    }

    /**
     * Pronóstico a partir de una dirección (geocodificación + clima).
     *
     * @param address dirección o lugar a consultar
     * @return 200 con {@link WeatherReportDto}; 400 si {@code address} está vacío;
     *         404 si no se pudo geocodificar o no hay datos
     */
    @GET
    @Path("/search")
    public Response search(@QueryParam("address") String address) {
        if (address == null || address.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Query parameter 'address' must not be blank")
                    .build();
        }
        return weatherUseCase.forecastByAddress(address)
                .map(WeatherReportDto::from)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();
    }
}
