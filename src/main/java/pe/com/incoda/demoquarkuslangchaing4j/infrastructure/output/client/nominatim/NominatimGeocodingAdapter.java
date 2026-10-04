package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.output.client.nominatim;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import pe.com.incoda.demoquarkuslangchaing4j.domain.model.GeoLocation;
import pe.com.incoda.demoquarkuslangchaing4j.domain.service.geocoding.GeocodingService;

/**
 * Adaptador de salida (infrastructure.output.client.nominatim) que implementa el
 * puerto de dominio {@link GeocodingService} sobre el cliente REST de Nominatim.
 * Traduce los DTO del proveedor al modelo de dominio en este límite.
 */
@ApplicationScoped
public class NominatimGeocodingAdapter implements GeocodingService {

    private static final Logger LOG = Logger.getLogger(NominatimGeocodingAdapter.class);

    private final NominatimClient client;

    @Inject
    public NominatimGeocodingAdapter(@RestClient NominatimClient client) {
        this.client = client;
    }

    /**
     * Geocodificación directa: dirección → coordenadas.
     *
     * @param address dirección o lugar a buscar; si es vacío devuelve {@code Optional.empty()}
     * @return la mejor coincidencia o vacío si no hay resultados
     */
    @Override
    public Optional<GeoLocation> search(String address) {
        if (address == null || address.isBlank()) {
            return Optional.empty();
        }
        List<NominatimPlaceDto> results =
                client.search(address, NominatimClient.FORMAT_JSONV2, 1, 1);
        if (results == null || results.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(results.get(0).toDomain());
        } catch (NumberFormatException e) {
            LOG.warnf("Nominatim returned unparseable coordinates for address '%s'", address);
            return Optional.empty();
        }
    }

    /**
     * Geocodificación directa devolviendo varias coincidencias.
     *
     * @param address dirección o lugar a buscar; si es vacío devuelve lista vacía
     * @param limit   número máximo de coincidencias (mínimo 1)
     * @return lista de coincidencias parseables (vacía si no hay resultados)
     */
    @Override
    public List<GeoLocation> searchAll(String address, int limit) {
        if (address == null || address.isBlank()) {
            return List.of();
        }
        List<NominatimPlaceDto> results =
                client.search(address, NominatimClient.FORMAT_JSONV2, Math.max(1, limit), 1);
        if (results == null || results.isEmpty()) {
            return List.of();
        }
        List<GeoLocation> locations = new ArrayList<>();
        for (NominatimPlaceDto dto : results) {
            try {
                locations.add(dto.toDomain());
            } catch (NumberFormatException e) {
                LOG.warnf("Nominatim returned unparseable coordinates for address '%s'", address);
            }
        }
        return locations;
    }

    /**
     * Geocodificación inversa: coordenadas → dirección.
     *
     * @param latitude  latitud en grados decimales
     * @param longitude longitud en grados decimales
     * @return la ubicación encontrada o vacío si no hay resultados
     */
    @Override
    public Optional<GeoLocation> reverse(double latitude, double longitude) {
        NominatimPlaceDto result =
                client.reverse(latitude, longitude, NominatimClient.FORMAT_JSONV2);
        if (result == null || result.latitude() == null || result.longitude() == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(result.toDomain());
        } catch (NumberFormatException e) {
            LOG.warnf("Nominatim returned unparseable coordinates for lat=%s lon=%s", latitude, longitude);
            return Optional.empty();
        }
    }
}
