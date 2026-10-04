package pe.com.incoda.demoquarkuslangchaing4j.domain.dto.rag;

/**
 * Contrato de entrada por REST para indexar un documento nuevo en el almacén
 * vectorial.
 *
 * @param source  identificador del documento (se guarda como metadata {@code file_name});
 *                si es {@code null} se usa {@code "inline"}
 * @param content texto completo del documento a ingerir
 */
public record RagDocumentDto(String source, String content) {}
