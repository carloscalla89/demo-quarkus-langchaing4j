# RAG en este proyecto (Quarkus + LangChain4j + pgvector)

Documento didáctico que explica qué es el RAG, para qué sirve indexar los
documentos en una base de datos vectorial y cómo está implementado en esta
aplicación.

## 1. ¿Qué es un embedding?

Un *embedding* convierte texto en un **vector** (una lista de números) que
captura su **significado**. Textos con significado parecido quedan **cerca** en
ese espacio vectorial, aunque no compartan palabras exactas.

## 2. ¿Qué significa "indexar" documentos?

Indexar es:

1. Cargar los documentos de una carpeta (`rag.location`).
2. Partirlos en **segmentos** (chunks) con solapamiento.
3. Calcular el embedding de cada segmento con el modelo configurado.
4. Guardar en la **base de datos vectorial** (pgvector) el vector, el texto del
   segmento y su **metadata** (por ejemplo, `file_name`).

En este proyecto lo hace `RagIngestion` (`infrastructure/rag/ingestion`).

## 3. ¿Para qué sirve?

Para poder hacer **búsqueda semántica** en tiempo de consulta:

1. La pregunta del usuario se convierte en embedding con el **mismo modelo**.
2. La base vectorial busca los **vectores más cercanos** (similitud coseno o
   distancia).
3. Devuelve los **top-k segmentos** más relevantes.
4. Esos segmentos se inyectan en el prompt del LLM, que responde **con tu
   información** en lugar de solo con lo aprendido en entrenamiento.

Sin indexar, el LLM no puede "consultar" tus documentos: o se los pasas todos en
el prompt (imposible por límite de contexto y costo) o **alucina**.

## 4. ¿Por qué una base **vectorial** y no una búsqueda normal?

- La búsqueda por palabras clave falla con sinónimos y paráfrasis.
- La vectorial encuentra por **significado**: "¿cómo arranca rápido Quarkus?"
  puede recuperar un párrafo que diga "tiempo de inicio reducido" aunque no
  aparezca la palabra "arranca".
- Usa un **índice ANN** (HNSW / IVFFlat en pgvector) para no comparar contra
  todos los vectores: eso da velocidad con muchos documentos.

## 5. Cómo está implementado aquí

| Pieza | Clase | Rol |
| --- | --- | --- |
| Ingesta | `RagIngestion` | Divide y vectoriza los documentos de `rag.location`; reindexa al arrancar |
| Ingesta manual | `RagIngestionService` | Indexa un documento puntual o dispara reindexado |
| Recuperación | `RagRetriever` | Produce `ContentRetriever` y `RetrievalAugmentor` (beans CDI) |
| Caso de uso | `RagAskService` | Pregunta al asistente y devuelve las fuentes |
| AI Service | `DocumentationAssistant` | Responde usando el contexto recuperado |
| API | `RagResource` | `POST /rag/ask`, `POST /rag/documents`, `POST /rag/reindex` |

### Configuración relevante (`application.properties`)

```properties
rag.location=src/main/resources/rag
rag.segment-size=300
rag.overlap-size=30
rag.max-results=5
rag.min-score=0.0
rag.reindex-on-startup=true

quarkus.langchain4j.ai.gemini.embedding-model.model-id=gemini-embedding-001
quarkus.langchain4j.ai.gemini.embedding-model.output-dimension=1536
quarkus.langchain4j.pgvector.dimension=1536
```

## 6. Detalles que conviene tener claros

- **Dimensión:** el vector de la base debe coincidir con el del modelo de
  embeddings (`output-dimension=1536` ↔ `pgvector.dimension=1536`). Si cambias de
  modelo o dimensión, hay que **reindexar**.
- **Chunking:** el tamaño y el solapamiento de los segmentos afectan la calidad
  del retrieval; por eso se experimenta con `segment-size`/`overlap-size`.
- **Metadata:** además del vector se guardan datos (`file_name`, sección, etc.)
  que permiten **citar fuentes** y **filtrar** búsquedas.
- **Indexar ≠ consultar:** indexar es costoso y se hace una vez (o al actualizar
  documentos); consultar es barato y se hace por pregunta.
- **Producción:** no reindexar en cada arranque; persistir el índice y usar
  `rag.reindex-on-startup=false`.
- **Alcance global:** el `RetrievalAugmentor` que registra
  `quarkus-langchain4j-easy-rag` se aplica a **todos** los AI Services por
  defecto. Si un agente/asistente no debe usar RAG, desactívalo con
  `retrievalAugmentor = RegisterAiService.NoRetrievalAugmentorSupplier.class`.
  Ver el detalle en
  [Guardrails](quarkus-guardrails.md#7-advertencia-los-guardrails-de-entrada-ven-el-mensaje-augmentado-por-rag).

## 7. Analogía

La base vectorial es como el **catálogo de una biblioteca organizado por temas**:
no guardas los libros ordenados por título, sino por "de qué hablan". Cuando
preguntas algo, el catálogo te lleva directo a los estantes relevantes en lugar de
leer toda la biblioteca.

## 8. Glosario

- **Embedding:** representación numérica del significado de un texto.
- **Chunk/segmento:** fragmento de documento que se vectoriza por separado.
- **Vector store:** base de datos que almacena y busca vectores (pgvector).
- **Retrieval:** recuperación de los segmentos más relevantes para una consulta.
- **Augmentation:** inyectar el contexto recuperado en el prompt del LLM.
- **RAG:** Retrieval-Augmented Generation (generación aumentada por recuperación).

## Ver también

- [Function calling y tools en este proyecto](quarkus-function-call-and-tools.md)
- [Guardrails en este proyecto](quarkus-guardrails.md)
- [Quarkus Overview](quarkus-overview.md)
