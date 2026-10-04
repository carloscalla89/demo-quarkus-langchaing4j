package pe.com.incoda.demoquarkuslangchaing4j.application.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pe.com.incoda.demoquarkuslangchaing4j.infrastructure.rag.ingestion.RagIngestion;

import static dev.langchain4j.data.document.splitter.DocumentSplitters.recursive;

/**
 * Caso de uso de aplicación para indexar documentos en el almacén vectorial.
 * Usa el {@link EmbeddingStore} y el {@link EmbeddingModel} configurados, y
 * delega el reindexado completo en {@link RagIngestion}.
 */
@ApplicationScoped
public class RagIngestionService {

    @Inject
    EmbeddingStore store;

    @Inject
    EmbeddingModel embeddingModel;

    @Inject
    RagIngestion ragIngestion;

    /**
     * Divide e indexa un documento de texto en el almacén vectorial.
     *
     * @param source  identificador del documento (se guarda como metadata {@code file_name})
     * @param content texto completo del documento
     */
    public void ingest(String source, String content) {
        Document document = Document.from(content, Metadata.from(Document.FILE_NAME, source));
        EmbeddingStoreIngestor.builder()
                .embeddingStore(store)
                .embeddingModel(embeddingModel)
                .documentSplitter(recursive(300, 30))
                .build()
                .ingest(document);
    }

    /**
     * Borra el almacén y vuelve a indexar todos los documentos de {@code rag.location}.
     *
     * @return número de documentos indexados
     */
    public int reindex() {
        return ragIngestion.reindex();
    }
}
