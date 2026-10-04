package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.rag.retriever;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Configura el pipeline de recuperación RAG como beans CDI:
 * <ul>
 *   <li>un {@link ContentRetriever} que busca en el almacén vectorial, y</li>
 *   <li>un {@link RetrievalAugmentor} que inyecta el contexto recuperado en el
 *       prompt (lo usan automáticamente los AI Services).</li>
 * </ul>
 */
@ApplicationScoped
public class RagRetriever {

    /**
     * Crea el retriever de contenidos sobre el almacén vectorial.
     *
     * @param store          almacén vectorial (pgvector)
     * @param embeddingModel modelo de embeddings para vectorizar la consulta
     * @param maxResults     número máximo de segmentos a recuperar
     * @param minScore       puntaje mínimo de similitud (0 = sin filtro)
     * @return el retriever configurado
     */
    @Produces
    @ApplicationScoped
    public ContentRetriever contentRetriever(
            EmbeddingStore store,                       // raw, igual que RagIngestion
            EmbeddingModel embeddingModel,
            @ConfigProperty(name = "rag.max-results", defaultValue = "5") int maxResults,
            @ConfigProperty(name = "rag.min-score", defaultValue = "0.0") double minScore) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(store)
                .embeddingModel(embeddingModel)
                .maxResults(maxResults)
                .minScore(minScore)
                .build();
    }

    /**
     * Crea el augmentor que inyecta el contexto recuperado en el prompt.
     *
     * @param contentRetriever retriever de contenidos
     * @return el augmentor por defecto configurado con el retriever
     */
    @Produces
    @ApplicationScoped
    public RetrievalAugmentor retrievalAugmentor(ContentRetriever contentRetriever) {
        return DefaultRetrievalAugmentor.builder()
                .contentRetriever(contentRetriever)
                .build();
    }
}
