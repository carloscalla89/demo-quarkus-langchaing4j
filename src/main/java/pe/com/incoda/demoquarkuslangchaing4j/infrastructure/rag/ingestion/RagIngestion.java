package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.rag.ingestion;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.file.Path;
import java.util.List;

import static dev.langchain4j.data.document.splitter.DocumentSplitters.recursive;

/**
 * Ingesta de documentos del pipeline RAG manual. Al arrancar la aplicación (si
 * {@code rag.reindex-on-startup=true}) borra el almacén vectorial y vuelve a
 * indexar todos los documentos de {@code rag.location}, dividiéndolos en
 * segmentos con solapamiento.
 */
@ApplicationScoped
public class RagIngestion {

    @Inject
    EmbeddingStore store;

    @Inject
    EmbeddingModel embeddingModel;

    @ConfigProperty(name = "rag.location")
    Path documents;

    @ConfigProperty(name = "rag.segment-size", defaultValue = "300")
    int segmentSize;

    @ConfigProperty(name = "rag.overlap-size", defaultValue = "30")
    int overlapSize;

    @ConfigProperty(name = "rag.reindex-on-startup", defaultValue = "true")
    boolean reindexOnStartup;

    /**
     * Dispara la ingesta al arrancar la aplicación.
     *
     * @param event evento de arranque de Quarkus
     */
    void onStart(@Observes StartupEvent event) {
        if (reindexOnStartup) {
            reindex();
        }
    }

    /**
     * Borra el almacén vectorial y vuelve a indexar todos los documentos de
     * {@code rag.location}.
     *
     * @return número de documentos indexados
     */
    public int reindex() {
        store.removeAll();
        List<Document> docs = FileSystemDocumentLoader.loadDocumentsRecursively(documents);
        EmbeddingStoreIngestor.builder()
                .embeddingStore(store)
                .embeddingModel(embeddingModel)
                .documentSplitter(recursive(segmentSize, overlapSize))
                .build()
                .ingest(docs);
        Log.infof("Reindexed %d documents from %s", docs.size(), documents);
        return docs.size();
    }
}
