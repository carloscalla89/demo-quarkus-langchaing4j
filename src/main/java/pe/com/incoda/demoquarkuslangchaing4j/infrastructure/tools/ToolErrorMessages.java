package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.tools;

import dev.langchain4j.service.tool.ToolErrorContext;

/**
 * Textos compartidos para los manejadores de error de las tools. Centraliza el
 * formato de los mensajes que se devuelven al LLM cuando una tool falla o
 * recibe argumentos inválidos.
 */
public final class ToolErrorMessages {

    private ToolErrorMessages() {
    }

    /**
     * Construye el mensaje para un fallo de ejecución de una tool.
     *
     * @param context contexto del error (puede ser {@code null})
     * @param error   excepción lanzada por la tool (puede ser {@code null})
     * @return mensaje recuperable para el modelo
     */
    public static String execution(ToolErrorContext context, Throwable error) {
        return "La herramienta '" + toolName(context) + "' falló: " + message(error)
                + ". Pide aclaración al usuario o reformula la petición.";
    }

    /**
     * Construye el mensaje para argumentos inválidos de una tool.
     *
     * @param context contexto del error (puede ser {@code null})
     * @param error   excepción lanzada al parsear los argumentos (puede ser {@code null})
     * @return mensaje recuperable para el modelo
     */
    public static String arguments(ToolErrorContext context, Throwable error) {
        return "Argumentos inválidos para la herramienta '" + toolName(context) + "': "
                + message(error) + ". Revisa los argumentos y vuelve a intentarlo.";
    }

    private static String toolName(ToolErrorContext context) {
        if (context == null || context.toolExecutionRequest() == null) {
            return "desconocida";
        }
        return context.toolExecutionRequest().name();
    }

    private static String message(Throwable error) {
        if (error == null || error.getMessage() == null) {
            return "error desconocido";
        }
        return error.getMessage();
    }
}
