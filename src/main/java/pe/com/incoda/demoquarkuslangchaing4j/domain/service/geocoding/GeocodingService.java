package pe.com.incoda.demoquarkuslangchaing4j.domain.service.geocoding;

import java.util.List;
import java.util.Optional;

import pe.com.incoda.demoquarkuslangchaing4j.domain.model.GeoLocation;

/**
 * Puerto de dominio (salida) para geocodificación. Las capas de dominio y
 * aplicación dependen solo de esta abstracción; la implementación concreta vive
 * en {@code infrastructure/output/client/nominatim}.
 */
public interface GeocodingService {

    /**
     * Geocodificación directa: dirección o texto de lugar → coordenadas.
     *
     * @param address dirección o lugar a buscar; si es vacío devuelve {@code Optional.empty()}
     * @return la mejor coincidencia o vacío si no hay resultados
     */
    Optional<GeoLocation> search(String address);

    /**
     * Geocodificación directa devolviendo varias coincidencias, para poder
     * desambiguar entre lugares con el mismo nombre.
     *
     * @param address dirección o lugar a buscar
     * @param limit   número máximo de coincidencias
     * @return lista de coincidencias (vacía si no hay resultados)
     */
    List<GeoLocation> searchAll(String address, int limit);

    /**
     * Geocodificación inversa: coordenadas → dirección o descripción del lugar.
     *
     * @param latitude  latitud en grados decimales (WGS84)
     * @param longitude longitud en grados decimales (WGS84)
     * @return la ubicación encontrada o vacío si no hay resultados
     */
    Optional<GeoLocation> reverse(double latitude, double longitude);
}
