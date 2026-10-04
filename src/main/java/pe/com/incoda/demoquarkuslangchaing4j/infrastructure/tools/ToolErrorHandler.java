package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.tools;

import dev.langchain4j.service.tool.ToolErrorContext;
import dev.langchain4j.service.tool.ToolErrorHandlerResult;
import dev.langchain4j.service.tool.ToolExecutionErrorHandler;
import io.quarkiverse.langchain4j.DefaultToolExecutionErrorHandler;
import jakarta.inject.Singleton;

/**
 * Manejador por defecto de errores de ejecución de tools para toda la
 * aplicación. Convierte las excepciones lanzadas por los métodos {@code @Tool}
 * en un mensaje que el LLM puede entender para recuperarse o pedir aclaración
 * al usuario.
 *
 * <p>Quarkus lo registra como bean global gracias a
 * {@link DefaultToolExecutionErrorHandler}.
 */
@Singleton
@DefaultToolExecutionErrorHandler
public class ToolErrorHandler implements ToolExecutionErrorHandler {

    /**
     * Maneja un fallo durante la ejecución de una tool.
     *
     * @param error   excepción lanzada por la tool
     * @param context contexto con la petición que provocó el error
     * @return resultado con el mensaje devuelto al modelo
     */
    @Override
    public ToolErrorHandlerResult handle(Throwable error, ToolErrorContext context) {
        return ToolErrorHandlerResult.text(ToolErrorMessages.execution(context, error));
    }
}
