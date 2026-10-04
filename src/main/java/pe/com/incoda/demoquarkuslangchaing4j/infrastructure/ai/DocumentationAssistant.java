package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.ai;

import dev.langchain4j.service.SystemMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * AI Service que responde preguntas usando recuperación de documentos (RAG).
 * Quarkus genera la implementación a partir de esta interfaz; el
 * {@code RetrievalAugmentor} se inyecta automáticamente (ver {@code RagRetriever}).
 */
@RegisterAiService
@ApplicationScoped
@SystemMessage("You are a Quarkus documentation assistant. Use the retrieved content to answer user questions.")
public interface DocumentationAssistant {

    /**
     * Responde una pregunta usando el contexto recuperado del almacén vectorial.
     *
     * @param question pregunta del usuario en lenguaje natural
     * @return respuesta fundamentada en el contexto recuperado
     */
    String ask(String question);

}
