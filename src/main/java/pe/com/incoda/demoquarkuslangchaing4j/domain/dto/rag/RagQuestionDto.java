package pe.com.incoda.demoquarkuslangchaing4j.domain.dto.rag;

/**
 * Contrato de entrada por REST para preguntar al pipeline RAG.
 *
 * @param question pregunta del usuario en lenguaje natural
 */
public record RagQuestionDto(String question) {}
