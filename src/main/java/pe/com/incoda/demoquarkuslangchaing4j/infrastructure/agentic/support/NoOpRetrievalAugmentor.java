package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.agentic.support;

import java.util.List;

import dev.langchain4j.rag.AugmentationRequest;
import dev.langchain4j.rag.AugmentationResult;
import dev.langchain4j.rag.RetrievalAugmentor;

/**
 * {@link RetrievalAugmentor} neutro para los agentes agentic. Devuelve el
 * mensaje original sin añadir contenido recuperado.
 *
 * <p>El {@code RetrievalAugmentor} global de easy-rag augmenta todos los AI
 * services por defecto, incluidos los agentes declarativos. En este proyecto el
 * RAG solo debe aplicarse a {@code DocumentationAssistant}, por lo que los
 * agentes del workflow se desmarcan con {@code @RetrievalAugmentorSupplier}
 * devolviendo esta implementación.
 */
public final class NoOpRetrievalAugmentor implements RetrievalAugmentor {

    /** Instancia compartida (el augmentor es sin estado). */
    public static final NoOpRetrievalAugmentor INSTANCE = new NoOpRetrievalAugmentor();

    private NoOpRetrievalAugmentor() {
    }

    /**
     * Devuelve el mensaje de entrada sin modificar.
     *
     * @param request petición de augmentación
     * @return el mismo mensaje, sin contenidos recuperados
     */
    @Override
    public AugmentationResult augment(AugmentationRequest request) {
        return new AugmentationResult(request.chatMessage(), List.of());
    }
}
