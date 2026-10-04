package pe.com.incoda.demoquarkuslangchaing4j.infrastructure.input.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pe.com.incoda.demoquarkuslangchaing4j.application.rag.RagAskService;
import pe.com.incoda.demoquarkuslangchaing4j.application.rag.RagIngestionService;
import pe.com.incoda.demoquarkuslangchaing4j.domain.dto.rag.RagAnswerDto;
import pe.com.incoda.demoquarkuslangchaing4j.domain.dto.rag.RagDocumentDto;
import pe.com.incoda.demoquarkuslangchaing4j.domain.dto.rag.RagQuestionDto;

import java.util.List;

/**
 * Adaptador de entrada (infrastructure.input.rest) que expone el pipeline RAG
 * por REST: consultar, indexar documentos y reindexar.
 */
@Path("/rag")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RagResource {

    private final RagAskService askService;
    private final RagIngestionService ingestionService;

    @Inject
    public RagResource(RagAskService askService, RagIngestionService ingestionService) {
        this.askService = askService;
        this.ingestionService = ingestionService;
    }

    /**
     * Pregunta al pipeline RAG y devuelve la respuesta con las fuentes.
     *
     * @param request pregunta del usuario
     * @return 200 con {@link RagAnswerDto}; 400 si la pregunta está vacía
     */
    @POST
    @Path("/ask")
    public Response ask(RagQuestionDto request) {
        if (request == null || request.question() == null || request.question().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new RagAnswerDto("question must not be blank", List.of()))
                    .build();
        }
        return Response.ok(new RagAnswerDto(
                askService.ask(request.question()),
                askService.sources(request.question()))).build();
    }

    /**
     * Indexa un documento de texto en el almacén vectorial.
     *
     * @param request documento a indexar
     * @return 201 si se indexó; 400 si el contenido está vacío
     */
    @POST
    @Path("/documents")
    public Response addDocument(RagDocumentDto request) {
        if (request == null || request.content() == null || request.content().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        ingestionService.ingest(
                request.source() == null ? "inline" : request.source(),
                request.content());
        return Response.status(Response.Status.CREATED).build();
    }

    /**
     * Borra y reconstruye el índice a partir de los documentos de {@code rag.location}.
     *
     * @return 200 con el número de documentos indexados
     */
    @POST
    @Path("/reindex")
    public Response reindex() {
        return Response.ok(new RagAnswerDto("reindexed documents: " + ingestionService.reindex(), List.of())).build();
    }
}
