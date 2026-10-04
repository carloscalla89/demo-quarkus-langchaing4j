# Guardrails en este proyecto (Quarkus + LangChain4j)

Documento didáctico que explica qué son los *guardrails*, cómo se declaran y
ejecutan con Quarkus LangChain4j, y cómo están implementados en esta aplicación.

> **Versiones:** Quarkus LangChain4j `1.13.3` sobre LangChain4j `1.19.3`. La
> implementación de guardrails vive en LangChain4j (la versión específica de
> Quarkus fue retirada); lo que aquí se describe es la API upstream.

## 1. ¿Qué es un guardrail?

Un *guardrail* es una regla que valida la interacción con el LLM para asegurar
que cumple lo esperado. Se aplica a la **entrada** (antes de llamar al modelo) o
a la **salida** (antes de entregar la respuesta al usuario).

Sirve, por ejemplo, para:

- rechazar preguntas fuera de dominio,
- limitar la longitud o detectar inyección de prompts,
- exigir formato, idioma o atribución en la respuesta,
- reintentar o reescribir una respuesta que no cumple.

## 2. Tipos de guardrail

| Tipo | Cuándo corre | Interfaz |
| --- | --- | --- |
| Entrada | Antes de llamar al LLM | `dev.langchain4j.guardrail.InputGuardrail` |
| Salida | Después de la respuesta del LLM | `dev.langchain4j.guardrail.OutputGuardrail` |

> No confundir con los **tool guardrails** (`ToolInputGuardrail` /
> `ToolOutputGuardrail`), que validan los argumentos y el resultado de una
> **tool** concreta. Ver
> [`quarkus-function-call-and-tools.md`](quarkus-function-call-and-tools.md).

## 3. API de LangChain4j

### Entrada

```java
public interface InputGuardrail {
    InputGuardrailResult validate(InputGuardrailRequest request);
}
```

`InputGuardrailRequest` expone el mensaje del usuario (`userMessage()`), los
parámetros comunes (`requestParams()`) y utilidades como `rewriteUserMessage()`.

### Salida

```java
public interface OutputGuardrail {
    OutputGuardrailResult validate(OutputGuardrailRequest request);
}
```

`OutputGuardrailRequest` expone la respuesta del LLM
(`responseFromLLM()`, un `ChatResponse`), el ejecutor de chat (`chatExecutor()`)
y los parámetros comunes.

### Resultados posibles

| Resultado | Efecto |
| --- | --- |
| `success()` | Continúa. |
| `successWith(texto)` | Continúa, reemplazando el texto. |
| `failure(mensaje)` | Falla la validación (acumulando fallos). |
| `fatal(mensaje)` | Falla y detiene la ejecución. |
| `retry(mensaje)` | **Solo salida**: reintenta la llamada al LLM. |
| `reprompt(mensaje, extra)` | **Solo salida**: reintenta con un prompt adicional. |

## 4. Cómo declararlos

Se declaran con anotaciones a nivel de **método** o de **clase** del AI Service:

```java
@InputGuardrails(WeatherInputGuardrail.class)
@OutputGuardrails(WeatherOutputGuardrail.class)
Result<String> ask(@MemoryId String memoryId, String question);
```

Las clases de guardrail son **beans CDI** que implementan la interfaz
correspondiente. El orden importa: se ejecutan en el orden en que se listan.

## 5. Reintentos y configuración

```properties
quarkus.langchain4j.guardrails.max-retries=3
```

- Quarkus usa por defecto **3** reintentos (LangChain4j, 2). `0` los desactiva.
- `@OutputGuardrails(maxRetries = N)` sobreescribe el valor para un método.
- Los **input guardrails no soportan retry/reprompt**: si fallan, el fallo se
  propaga al llamador.

## 6. Propagación de fallos

| Fallo | Excepción | Mapeo en este proyecto |
| --- | --- | --- |
| Guardrail de entrada | `InputGuardrailException` | HTTP **400** |
| Guardrail de salida (tras reintentos) | `OutputGuardrailException` | HTTP **502** |

Los mappers `InputGuardrailExceptionMapper` y `OutputGuardrailExceptionMapper`
(paquete `infrastructure/input/rest`) traducen la excepción a una respuesta JSON
con el mensaje del guardrail.

## 7. Advertencia: los guardrails de entrada ven el mensaje augmentado por RAG

> [!WARNING]
> Los guardrails de entrada **no** validan la pregunta original: validan el
> mensaje **ya augmentado con el contexto de RAG**. Si un `RetrievalAugmentor`
> está activo, el guardrail recibe la pregunta + los documentos recuperados, y
> cualquier comprobación (por ejemplo, longitud máxima) puede fallar aunque la
> pregunta sea corta.

**Síntoma real en este proyecto:** preguntar *"¿Qué tiempo hace en Lima?"*
devolvía `400` con:

```
The guardrail ... WeatherInputGuardrail failed with this message:
La pregunta es demasiado larga (máximo 500 caracteres).
```

**Causa:** el `quarkus-langchain4j-easy-rag` registra un `RetrievalAugmentor`
**global**. El valor por defecto de `@RegisterAiService` es
`retrievalAugmentor = BeanIfExistsRetrievalAugmentorSupplier`, así que el agente
meteorológico recibía chunks de la documentación de Quarkus. En
`AiServiceMethodImplementationSupport`, el mensaje del usuario se reemplaza por
`AugmentationResult.chatMessage()` **antes** de ejecutar
`GuardrailsSupport.executeInputGuardrails(...)`.

**Solución:** desactivar RAG en los agentes que no lo necesitan:

```java
@ApplicationScoped
@RegisterAiService(
        tools = WeatherTools.class,
        retrievalAugmentor = RegisterAiService.NoRetrievalAugmentorSupplier.class)
public interface WeatherForecastAgent extends WeatherAssistant { /* ... */ }
```

RAG se conserva solo donde aporta valor (por ejemplo `DocumentationAssistant`).

## 8. Streaming y acumulador

En métodos que devuelven `Multi`/`TokenStream`, Quarkus ensambla la respuesta
completa antes de ejecutar los guardrails de salida. Puedes controlar cuándo se
invoca la cadena con `@OutputGuardrailAccumulator` y una implementación de
`io.quarkiverse.langchain4j.guardrails.OutputTokenAccumulator`.

## 9. Buenas prácticas

- Coloca los guardrails **baratos primero** (longitud, patrón) antes que los que
  llaman a servicios o a otro LLM.
- No invoques un LLM dentro de un input guardrail salvo que sea imprescindible:
  encarece cada petición.
- Devuelve mensajes **claros y accionables**.
- Recuerda que `retry`/`reprompt` de salida **vuelven a ejecutar el LLM** (y, si
  hay tools, también las tools): cuidado con el coste.
- Ten presente el gotcha de RAG de la sección 7.

## 10. Cómo está implementado en este proyecto

| Pieza | Clase | Rol |
| --- | --- | --- |
| Guardrail de entrada | `WeatherInputGuardrail` | Valida dominio/longitud por palabras clave |
| Guardrail de salida | `WeatherOutputGuardrail` | Exige la atribución de Open-Meteo |
| Mapper entrada | `InputGuardrailExceptionMapper` | `InputGuardrailException` → 400 |
| Mapper salida | `OutputGuardrailExceptionMapper` | `OutputGuardrailException` → 502 |
| Aplicación | `WeatherForecastAgent` | `@InputGuardrails` / `@OutputGuardrails` |

Ejemplo del guardrail de entrada:

```java
@ApplicationScoped
public class WeatherInputGuardrail implements InputGuardrail {
    @Override
    public InputGuardrailResult validate(InputGuardrailRequest request) {
        String text = request.userMessage().singleText();
        if (text == null || text.isBlank()) {
            return failure("La pregunta no puede estar vacía.");
        }
        if (text.length() > 500) {
            return failure("La pregunta es demasiado larga (máximo 500 caracteres).");
        }
        if (noEsSobreClima(text)) {
            return failure("Solo puedo responder preguntas relacionadas con el clima.");
        }
        return success();
    }
}
```

Ejemplo del guardrail de salida (reintenta si falta la atribución):

```java
@ApplicationScoped
public class WeatherOutputGuardrail implements OutputGuardrail {
    @Override
    public OutputGuardrailResult validate(OutputGuardrailRequest request) {
        String text = request.responseFromLLM().aiMessage().text();
        if (!text.toLowerCase(Locale.ROOT).contains("open-meteo")) {
            return retry("Falta la atribución 'Datos meteorológicos de Open-Meteo.com'.");
        }
        return success();
    }
}
```

## 11. Ejemplos

```bash
# Pregunta fuera de dominio -> 400 (guardrail de entrada)
curl -s -X POST http://localhost:8080/weather/agent \
  -H "Content-Type: application/json" \
  -d '{"question":"escríbeme un poema"}'
# {"answer":"Solo puedo responder preguntas relacionadas con el clima o ubicaciones."}

# Respuesta válida -> 200
curl -s -X POST http://localhost:8080/weather/agent \
  -H "Content-Type: application/json" \
  -d '{"question":"¿Qué tiempo hace en Lima?"}'
```

## 12. Detalles que conviene tener claros

- Los guardrails de **entrada** no reintentan; los de **salida** sí
  (`retry`/`reprompt`).
- El fallo de salida definitivo (tras agotar reintentos) es un `502`; conviene
  tenerlo mapeado.
- El guardrail de entrada ve el mensaje **augmentado por RAG** (sección 7).
- Un guardrail puede **reescribir** el mensaje (`successWith` /
  `rewriteUserMessage`), no solo aceptarlo o rechazarlo.

## 13. Analogía

Un guardrail es como el **control de seguridad de un aeropuerto**: revisa lo que
entra (input) y lo que sale (output). Si algo no cumple las normas, se detiene el
paso; y en la salida, si falta un requisito, se puede "reenviar" al pasajero para
que lo corrija (`retry`/`reprompt`).

## 14. Glosario

- **Guardrail:** regla que valida entrada o salida del LLM.
- **Input/Output Guardrail:** guardrail antes/después del modelo.
- **`failure` / `fatal`:** fallo recuperable / fallo que detiene.
- **`retry` / `reprompt`:** reintento de salida (con o sin prompt extra).
- **Tool guardrail:** guardrail específico de argumentos/resultado de una tool.
- **`max-retries`:** número de reintentos de los guardrails de salida.

## Ver también

- [Function calling y tools en este proyecto](quarkus-function-call-and-tools.md)
- [RAG en este proyecto](quarkus-rag.md)
- [Quarkus Overview](quarkus-overview.md)
