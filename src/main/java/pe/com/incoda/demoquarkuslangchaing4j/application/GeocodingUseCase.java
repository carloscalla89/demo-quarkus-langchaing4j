package pe.com.incoda.demoquarkuslangchaing4j.application;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import pe.com.incoda.demoquarkuslangchaing4j.domain.model.GeoLocation;
import pe.com.incoda.demoquarkuslangchaing4j.domain.service.geocoding.GeocodingService;

/**
 * Caso de uso de aplicación que orquesta la geocodificación. Depende solo del
 * puerto de dominio, nunca de detalles de infraestructura como el cliente REST.
 */
@ApplicationScoped
public class GeocodingUseCase {

    private final GeocodingService geocodingService;

    @Inject
    public GeocodingUseCase(GeocodingService geocodingService) {
        this.geocodingService = geocodingService;
    }

    /**
     * Geocodificación directa: dirección → coordenadas.
     *
     * @param address dirección o lugar a buscar
     * @return la ubicación encontrada o vacío si no hay resultados
     */
    public Optional<GeoLocation> search(String address) {
        return geocodingService.search(address);
    }

    /**
     * Geocodificación directa devolviendo varias coincidencias.
     *
     * @param address dirección o lugar a buscar
     * @param limit   número máximo de coincidencias
     * @return lista de coincidencias (vacía si no hay resultados)
     */
    public List<GeoLocation> searchAll(String address, int limit) {
        return geocodingService.searchAll(address, limit);
    }

    /**
     * Geocodificación inversa: coordenadas → dirección.
     *
     * @param latitude  latitud en grados decimales
     * @param longitude longitud en grados decimales
     * @return la ubicación encontrada o vacío si no hay resultados
     */
    public Optional<GeoLocation> reverse(double latitude, double longitude) {
        return geocodingService.reverse(latitude, longitude);
    }
}
