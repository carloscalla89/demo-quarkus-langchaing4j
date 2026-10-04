# Function calling y tools en este proyecto (Quarkus + LangChain4j + Gemini)

Documento didáctico que explica qué es el *function calling*, cómo se declaran y
ejecutan las *tools* con Quarkus LangChain4j, y cómo está implementado en esta
aplicación.

> **Versiones:** Quarkus LangChain4j `1.13.3` sobre LangChain4j `1.19.3`. Todo lo
> que aparece aquí está verificado contra el código del proyecto y las
> dependencias reales.

## 1. ¿Qué es function calling / tools?

Un LLM por sí solo no puede consultar datos actuales ni ejecutar acciones: solo
genera texto. El *function calling* (o *tool calling*) permite que el modelo
**pida ejecutar una función Java**; la aplicación la ejecuta, le devuelve el
resultado y el modelo lo usa para responder.

Flujo típico:

1. El prompt enviado al modelo incluye la lista de tools disponibles (nombre,
   descripción y parámetros).
2. El modelo decide si llamar a una tool y con qué argumentos.
3. LangChain4j parsea los argumentos, ejecuta el método Java y envía el
   resultado al modelo.
4. El modelo repite (si hace falta encadenar tools) hasta dar la respuesta final.

Ejemplo en esta app: al preguntar *"¿Qué tiempo hace en Lima?"*, el modelo
invoca `get_weather_by_city("Lima")`, recibe el clima real y redacta la
respuesta.

## 2. Cómo se declara una tool

Una tool es un método Java anotado con `@Tool`. Sus parámetros pueden describirse
con `@P`:

```java
@Tool(name = "get_weather_by_city",
      value = "Devuelve el clima actual y el pronóstico de hasta 7 días de una ciudad. "
            + "Úsala cuando el usuario pregunte por el clima de un lugar concreto.")
public WeatherToolResult getWeatherByCity(
        @P("Nombre de la ciudad, por ejemplo 'Lima' o 'Barcelona'") String city,
        @P(value = "Número de días de pronóstico (1-7); por defecto 7", defaultValue = "7") int days) {
    // ...
}
```

### `@Tool`

| Elemento | Para qué sirve |
| --- | --- |
| `name` | Nombre que ve el modelo (si se omite, se usa el nombre del método). |
| `value` | Descripción de qué hace y **cuándo usarla**. |
| `returnBehavior` | Controla si el resultado se devuelve al LLM o se trata como respuesta final. |
| `searchBehavior` | Participación en la búsqueda de tools (avanzado). |
| `metadata` | Metadatos específicos del proveedor (avanzado). |

### `@P`

| Elemento | Para qué sirve |
| --- | --- |
| `name` | Nombre del parámetro tal como lo ve el modelo (por defecto, el real). |
| `description` / `value` | Descripción del parámetro (son sinónimos). |
| `required` | Si es obligatorio (por defecto `true`). |
| `defaultValue` | Valor por defecto si el modelo no lo envía. |

> Los nombres de los parámetros se conservan porque Quarkus compila con
> `-parameters`; por eso normalmente no hace falta poner `name`.

## 3. Qué ve realmente el modelo

Solo viajan al modelo:

- el **nombre** y la **descripción** de `@Tool`,
- los **nombres y descripciones** de `@P`.

El **Javadoc NO se envía** al modelo. Sirve para documentar el código, pero no
influye en la decisión del LLM. Por eso las descripciones de `@Tool`/`@P` deben
ser claras por sí solas.

**Regla de oro:** si una persona entiende para qué sirve la tool y cómo usarla
solo con esas descripciones, el modelo también.

## 4. Cómo se adjuntan las tools a un AI Service

Hay dos formas:

```java
// A nivel de AI Service (recomendado): aplica a todos los métodos
@RegisterAiService(tools = WeatherTools.class)
public interface WeatherForecastAgent extends WeatherAssistant { /* ... */ }

// A nivel de método (alternativa)
@ToolBox({WeatherTools.class, TravelTools.class})
String plan(String destination);
```

- `@RegisterAiService(tools = ...)` acepta una o varias clases.
- `@ToolBox` solo puede ir en **métodos** (su `@Target` es `METHOD`).

Cada clase referenciada debe ser un bean CDI y declarar métodos `@Tool`.

## 5. Tipos de retorno

| Tipo de retorno | Qué recibe el modelo |
| --- | --- |
| `String` | El texto tal cual. |
| `void` | El literal `"Success"`. |
| Records/objetos/colecciones | Se serializan a **JSON**. |

En este proyecto las tools devuelven modelos de dominio
(`WeatherToolResult`, `GeoLocation`), que LangChain4j serializa a JSON. Ventaja:
el mismo objeto se reutiliza para construir la respuesta REST estructurada (ver
`WeatherAgentService`).

> Cuida el tamaño del JSON: devolver el pronóstico completo de 7 días añade
> tokens y ruido. Aquí las tools aceptan un parámetro `days` para recortarlo.

## 6. Límites y control de invocaciones

Configuración global en `application.properties`:

```properties
quarkus.langchain4j.ai-service.max-tool-calls-per-response=4
quarkus.langchain4j.ai-service.max-tool-calling-round-trips=5
```

| Propiedad | Default | Qué controla |
| --- | --- | --- |
| `max-tool-calls-per-response` | `0` (sin límite) | Tools que puede pedir el modelo en una respuesta. |
| `max-tool-calling-round-trips` | `10` | Vueltas de "modelo → tool → modelo". |
| `max-tool-executions` | `10` (deprecado) | Número total de ejecuciones de tools. |

A nivel de AI Service también existen `maxToolCallsPerResponse`,
`maxToolCallingRoundTrips` y `maxSequentialToolInvocations`.

Sobre forzar el uso de tools: LangChain4j expone `ToolChoice.AUTO/REQUIRED/NONE`
en `ChatRequestParameters`, pero la extensión de Gemini (`1.13.3`) no lo expone
por configuración; en la práctica se pide en el prompt ("SIEMPRE invoca una
herramienta").

## 7. Memoria obligatoria y `@MemoryId`

Un AI Service que usa tools **debe tener memoria de chat**. Quarkus lo valida en
build: si pones `NoChatMemoryProviderSupplier` en un servicio con tools, falla
con `Tool usage requires chat memory`.

Sin `@MemoryId`, todas las peticiones comparten la misma memoria (fuga de
contexto entre usuarios). La solución es aislar por sesión:

```java
Result<String> ask(@MemoryId String memoryId, String question);
```

Y acotar la ventana:

```properties
quarkus.langchain4j.chat-memory.memory-window.max-messages=10
```

En esta app, `WeatherAgentService` usa el `sessionId` del request o genera un
`UUID`.

## 8. Manejo de errores

### Errores de ejecución (la tool lanza una excepción)

Se define un bean global con `@DefaultToolExecutionErrorHandler` que implementa
`ToolExecutionErrorHandler`:

```java
@Singleton
@DefaultToolExecutionErrorHandler
public class ToolErrorHandler implements ToolExecutionErrorHandler {
    @Override
    public ToolErrorHandlerResult handle(Throwable error, ToolErrorContext context) {
        return ToolErrorHandlerResult.text(ToolErrorMessages.execution(context, error));
    }
}
```

El texto devuelto se envía al modelo como resultado de la tool, que puede
recuperarse o pedir aclaración.

### Errores de argumentos (el modelo envía argumentos inválidos)

Va en un **método estático de la propia interfaz** del AI Service:

```java
@HandleToolArgumentError
static String handleToolArgumentError(Throwable error, ToolErrorContext context) {
    return ToolErrorMessages.arguments(context, error);
}
```

> Los mensajes de error deben ser **recuperables**: indicar qué falló y qué
> hacer, no solo un código.

## 9. Observabilidad

- Evento CDI `ToolExecutedEvent` (`dev.langchain4j.observability.api.event`), con
  `request()` y `resultText()`. En este proyecto lo escucha
  `ToolLoggingListener`.
- Trazas OpenTelemetry: cada tool crea un span `langchain4j.tools.<nombre>`.
  Opcionalmente, `quarkus.langchain4j.tracing.include-tool-arguments` y
  `include-tool-result`.
- En dev, `log-requests`/`log-responses` muestran las peticiones al LLM con los
  `toolCall`.

`Result<T>` (cuando el método lo devuelve) expone `toolExecutions()`,
`tokenUsage()` y `finishReason()`. `ToolExecution.resultObject()` devuelve el
objeto real devuelto por la tool (útil para respuestas estructuradas).

## 10. Patrones y antipatrones

**Patrones**

- Tools de **alto nivel** y orientadas al negocio (`get_weather_by_city`) que
  encapsulan varios pasos (geocodificar + pronosticar).
- Nombres de negocio (`geocode_city`, no `search`) y descripciones "cuándo usar".
- Parámetros simples y con `@P`; valores por defecto cuando aplique.
- Devolver datos ya "listos" (unidades, zona horaria, atribución) para que el
  modelo no tenga que deducirlos.
- Geocodificación con **varias coincidencias** (top-N) para desambiguar.

**Antipatrones**

- Exponer un cliente REST tal cual como tool: el modelo tendría que adivinar
  parámetros de infraestructura (`format`, `limit`, `addressdetails`).
- Un AI Service usado como tool (un LLM dentro de otro LLM): duplica coste y
  latencia.
- Nombres genéricos (`search`, `weather`) y descripciones vacías.
- Devolver JSON enorme o con claves que no coinciden con lo que dice el prompt.

## 11. Cómo está implementado en este proyecto

| Pieza | Clase | Rol |
| --- | --- | --- |
| Tools de clima | `WeatherTools` | `get_weather_by_city`, `get_weather_by_coordinates`, `geocode_city`, `reverse_geocode` |
| Tool de viaje | `TravelTools` | `suggest_activity` (heurística local) |
| Errores | `ToolErrorHandler`, `ToolErrorMessages` | Manejo central de errores |
| Logging | `ToolLoggingListener` | Observa `ToolExecutedEvent` |
| Agente | `WeatherForecastAgent` | `@RegisterAiService(tools = WeatherTools.class)` |
| Agente de viaje | `TravelPlanner` | `@RegisterAiService(tools = {WeatherTools, TravelTools})` |
| Servicio | `WeatherAgentService` | Mapea `Result` → `WeatherAgentAnswerDto` |
| Puerto | `WeatherAssistant` | Abstracción de dominio del agente |

Las tools viven en `infrastructure/tools` y envuelven los casos de uso
(`WeatherUseCase`, `GeocodingUseCase`); los AI Services que las usan viven en
`infrastructure/ai` para que `domain`/`application` no dependan de `@Tool`.

## 12. Ejemplos de uso

```bash
# Clima por ciudad (invoca get_weather_by_city)
curl -s -X POST http://localhost:8080/weather/agent \
  -H "Content-Type: application/json" \
  -d '{"question":"¿Qué tiempo hace en Lima?"}'

# Con opciones (sesión, días, ciudad)
curl -s -X POST http://localhost:8080/weather/agent \
  -H "Content-Type: application/json" \
  -d '{"question":"¿Qué tiempo hará en Cusco?","sessionId":"user-42","days":3,"city":"Cusco"}'
```

La respuesta incluye `toolsUsed` (qué tools se invocaron) y los datos
estructurados. En consola (dev) verás algo como:

```
Tool ejecutada: get_weather_by_city | argumentos: {"city":"Lima","days":7} | resultado: {...}
```

## 13. Detalles que conviene tener claros

- **Prompt alineado con la salida real:** si la tool devuelve
  `current.temperatureC`, el `@SystemMessage` debe describir esa misma forma; si
  no, el modelo alucina.
- **Las tools requieren memoria**, y por defecto es compartida; usa `@MemoryId`.
- **`resultObject()` vs `result()`:** el primero da el objeto Java devuelto por
  la tool; el segundo, su representación en texto/JSON.
- **Los guardrails de entrada se ejecutan sobre el mensaje augmentado por RAG**,
  no sobre la pregunta original. Es un gotcha real que puede romper la
  validación. Ver la advertencia en
  [`quarkus-guardrails.md`](quarkus-guardrails.md#7-advertencia-los-guardrails-de-entrada-ven-el-mensaje-augmentado-por-rag).
- **Los límites** (`max-tool-calls-per-response`, round-trips) evitan bucles y
  costes descontrolados.

## 14. Analogía

Las tools son como el **mostrador de atención al cliente** de una tienda: el
modelo (el vendedor) no tiene acceso directo al almacén, pero puede pedir al
mostrador "tráeme el clima de Lima". El mostrador (tu código Java) hace el
trabajo real y le entrega el resultado; el vendedor solo lo explica al cliente.

## 15. Glosario

- **Tool / function:** método Java que el modelo puede invocar.
- **Function calling:** capacidad del modelo de pedir ejecutar tools.
- **`@Tool` / `@P`:** anotaciones que describen la tool y sus parámetros.
- **ToolBox:** anotación de método para adjuntar tools.
- **AI Service:** interfaz que Quarkus implementa como agente con LLM.
- **`Result<T>`:** envoltorio con la respuesta y metadatos (tools, tokens...).
- **`@MemoryId`:** identificador que aísla la memoria de chat por conversación.

## Ver también

- [Guardrails en este proyecto](quarkus-guardrails.md)
- [RAG en este proyecto](quarkus-rag.md)
- [Quarkus Overview](quarkus-overview.md)
