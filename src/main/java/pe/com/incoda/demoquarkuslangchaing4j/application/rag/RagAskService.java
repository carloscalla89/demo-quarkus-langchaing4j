package pe.com.incoda.demoquarkuslangchaing4j.application.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.ContentMetadata;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import pe.com.incoda.demoquarkuslangchaing4j.domain.dto.rag.RagSourceDto;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.ai.DocumentationAssistant;

import java.util.List;

/**
 * Caso de uso de aplicación que responde preguntas con RAG y expone las fuentes
 * recuperadas. Depende del AI Service {@link DocumentationAssistant} y del
 * {@link ContentRetriever}.
 *
 * <p>Nota didáctica: la recuperación se ejecuta dos veces (una dentro del
 * augmentor que usa el asistente y otra explícita para listar las fuentes). Es
 * una simplificación para poder mostrar las citas.</p>
 */
@ApplicationScoped
public class RagAskService {

    private final DocumentationAssistant assistant;
    private final ContentRetriever contentRetriever;

    @Inject
    public RagAskService(DocumentationAssistant assistant, ContentRetriever contentRetriever) {
        this.assistant = assistant;
        this.contentRetriever = contentRetriever;
    }

    /**
     * Responde la pregunta con el asistente RAG.
     *
     * @param question pregunta del usuario
     * @return respuesta generada por el modelo
     */
    public String ask(String question) {
        return assistant.ask(question);
    }

    /**
     * Recupera las fuentes (segmentos) relevantes para la pregunta.
     *
     * @param question pregunta del usuario
     * @return lista de fuentes con su puntaje y un extracto de texto
     */
    public List<RagSourceDto> sources(String question) {
        return contentRetriever.retrieve(Query.from(question)).stream()
                .map(RagAskService::toSource)
                .toList();
    }

    /**
     * Convierte un segmento recuperado en su DTO de fuente.
     *
     * @param content contenido recuperado por el retriever
     * @return el DTO con origen, score y extracto
     */
    private static RagSourceDto toSource(Content content) {
        String text = content.textSegment().text();
        String source = content.textSegment().metadata().getString(Document.FILE_NAME);
        Object score = content.metadata().get(ContentMetadata.SCORE);
        String snippet = text.length() > 300 ? text.substring(0, 300) + "..." : text;
        return new RagSourceDto(
                source == null ? "unknown" : source,
                score instanceof Number n ? n.doubleValue() : 0.0,
                snippet);
    }
}
