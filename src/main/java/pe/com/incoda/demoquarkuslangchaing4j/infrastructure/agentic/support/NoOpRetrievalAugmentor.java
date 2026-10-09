package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.support;

import java.util.List;

import dev.langchain4j.rag.AugmentationRequest;
import dev.langchain4j.rag.AugmentationResult;
import dev.langchain4j.rag.RetrievalAugmentor;

/**
 * Augmentor RAG no-op. Devuelve el mensaje original sin añadir contexto.
 *
 * <p>Se usa como {@code @RetrievalAugmentorSupplier} en los agentes agentic para
 * excluirlos del {@code RetrievalAugmentor} global que publica
 * {@code RagRetriever}; de lo contrario el mensaje se aumentaría con documentos
 * irrelevantes (y podría disparar los guardrails de entrada).
 */
public class NoOpRetrievalAugmentor implements RetrievalAugmentor {

    /**
     * Devuelve el mensaje sin modificar y sin contenidos recuperados.
     *
     * @param request petición de aumento
     * @return resultado con el mismo mensaje y sin contenidos
     */
    @Override
    public AugmentationResult augment(AugmentationRequest request) {
        return new AugmentationResult(request.chatMessage(), List.of());
    }
}
