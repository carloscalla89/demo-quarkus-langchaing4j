package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.tools;

import org.jboss.logging.Logger;

import dev.langchain4j.observability.api.event.ToolExecutedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

/**
 * Listener de observabilidad ligero que registra en el log cada ejecución de
 * tool. Escucha el evento CDI {@link ToolExecutedEvent} que emite Quarkus
 * LangChain4j, sin requerir extensiones de métricas ni de trazas.
 */
@ApplicationScoped
public class ToolLoggingListener {

    private static final Logger LOG = Logger.getLogger(ToolLoggingListener.class);

    /**
     * Registra en DEBUG la petición y el resultado de cada tool ejecutada.
     *
     * @param event evento con el nombre, los argumentos y el resultado de la tool
     */
    public void onToolExecuted(@Observes ToolExecutedEvent event) {
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Tool ejecutada: %s | argumentos: %s | resultado: %s",
                    event.request().name(),
                    event.request().arguments(),
                    event.resultText());
        }
    }
}
