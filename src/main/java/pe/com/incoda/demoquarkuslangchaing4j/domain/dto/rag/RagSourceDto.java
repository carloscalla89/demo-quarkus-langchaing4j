package pe.com.incoda.demoquarkuslangchaing4j.domain.dto.rag;

/**
 * Fuente (segmento de documento) recuperada por el pipeline RAG.
 *
 * @param source  nombre del documento de origen (metadata {@code file_name})
 * @param score   puntaje de similitud devuelto por el almacén vectorial
 * @param snippet extracto del texto del segmento
 */
public record RagSourceDto(String source, double score, String snippet) {}
