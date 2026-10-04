package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.output.client.openmeteo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import pe.com.incoda.demoquarkuslangchaing4j.domain.model.DailyForecast;
import pe.com.incoda.demoquarkuslangchaing4j.domain.model.WeatherCode;
import pe.com.incoda.demoquarkuslangchaing4j.domain.model.WeatherReport;
import pe.com.incoda.demoquarkuslangchaing4j.domain.model.WeatherSnapshot;
import pe.com.incoda.demoquarkuslangchaing4j.domain.service.weather.WeatherService;

/**
 * Adaptador de salida (infrastructure.output.client.openmeteo) que implementa el
 * puerto de dominio {@link WeatherService} sobre el cliente REST de Open-Meteo.
 * Traduce el DTO del proveedor al modelo de dominio en este límite.
 */
@ApplicationScoped
public class OpenMeteoWeatherAdapter implements WeatherService {

    private static final Logger LOG = Logger.getLogger(OpenMeteoWeatherAdapter.class);

    private final OpenMeteoClient client;

    @Inject
    public OpenMeteoWeatherAdapter(@RestClient OpenMeteoClient client) {
        this.client = client;
    }

    /**
     * Obtiene las condiciones actuales y el pronóstico diario para las coordenadas.
     *
     * @param latitude  latitud en grados decimales
     * @param longitude longitud en grados decimales
     * @param days      número de días de pronóstico (1..7)
     * @return el reporte de dominio o vacío si el proveedor no devuelve datos
     */
    @Override
    public Optional<WeatherReport> forecast(double latitude, double longitude, int days) {
        OpenMeteoResponse response = client.forecast(latitude, longitude, days);

        if (response == null || response.current() == null) {
            LOG.warnf("Open-Meteo returned no current weather for lat=%s lon=%s", latitude, longitude);
            return Optional.empty();
        }

        OpenMeteoResponse.Current c = response.current();
        int code = intValue(c.weatherCode());
        WeatherSnapshot current = new WeatherSnapshot(
                doubleValue(c.temperature()),
                doubleValue(c.apparentTemperature()),
                intValue(c.relativeHumidity()),
                code,
                WeatherCode.describe(code),
                doubleValue(c.windSpeed()),
                c.time());

        return Optional.of(new WeatherReport(response.timezone(), current, buildDaily(response.daily())));
    }

    /**
     * Convierte la sección diaria del proveedor en la lista de dominio.
     *
     * @param daily sección diaria de la respuesta (puede ser {@code null})
     * @return lista de pronósticos diarios (vacía si no hay datos)
     */
    private List<DailyForecast> buildDaily(OpenMeteoResponse.Daily daily) {
        List<DailyForecast> result = new ArrayList<>();
        if (daily == null || daily.time() == null) {
            return result;
        }
        for (int i = 0; i < daily.time().size(); i++) {
            int code = intAt(daily.weatherCode(), i);
            result.add(new DailyForecast(
                    daily.time().get(i),
                    doubleAt(daily.temperatureMax(), i),
                    doubleAt(daily.temperatureMin(), i),
                    code,
                    WeatherCode.describe(code),
                    intAt(daily.precipitationProbabilityMax(), i)));
        }
        return result;
    }

    /**
     * Normaliza un {@link Double} nulo a 0.
     *
     * @param value valor de entrada (puede ser {@code null})
     * @return el valor o 0 si es nulo
     */
    private static double doubleValue(Double value) {
        return value == null ? 0d : value;
    }

    /**
     * Normaliza un {@link Integer} nulo a 0.
     *
     * @param value valor de entrada (puede ser {@code null})
     * @return el valor o 0 si es nulo
     */
    private static int intValue(Integer value) {
        return value == null ? 0 : value;
    }

    /**
     * Lee un valor double de una lista de forma segura (índice fuera de rango → 0).
     *
     * @param values lista de valores (puede ser {@code null})
     * @param index  posición a leer
     * @return el valor en la posición o 0 si no existe
     */
    private static double doubleAt(List<Double> values, int index) {
        return values != null && index < values.size() ? doubleValue(values.get(index)) : 0d;
    }

    /**
     * Lee un valor int de una lista de forma segura (índice fuera de rango → 0).
     *
     * @param values lista de valores (puede ser {@code null})
     * @param index  posición a leer
     * @return el valor en la posición o 0 si no existe
     */
    private static int intAt(List<Integer> values, int index) {
        return values != null && index < values.size() ? intValue(values.get(index)) : 0;
    }
}
