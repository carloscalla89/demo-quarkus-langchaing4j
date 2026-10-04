package pe.com.incoda.demoquarkuslangchaing4j.domain.dto.rag;

import java.util.List;

/**
 * Contrato de salida por REST con la respuesta del pipeline RAG y las fuentes
 * recuperadas que la sustentan.
 *
 * @param answer  respuesta generada por el modelo
 * @param sources segmentos recuperados que se usaron como contexto
 */
public record RagAnswerDto(String answer, List<RagSourceDto> sources) {}
